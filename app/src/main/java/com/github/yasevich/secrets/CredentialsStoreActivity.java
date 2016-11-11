package com.github.yasevich.secrets;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.yasevich.secrets.databinding.ActivityCredentialsStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class CredentialsStoreActivity extends StoreActivity {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "AndroidKeyStore";

    private static final String IV_FILE = "iv";

    private static final int IV_LENGTH = 16;
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 100;

    @SuppressLint("InlinedApi")
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    @SuppressLint("InlinedApi")
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;

    private KeyguardManager keyguardManager;

    private ActivityCredentialsStoreBinding binding;

    @OperationMode
    private int operationMode;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, CredentialsStoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credentials_store);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {
            binding.encrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.encrypt.setEnabled(false);
                    binding.decrypt.setEnabled(true);
                    onOperation(Cipher.ENCRYPT_MODE);
                }
            });

            binding.decrypt.setEnabled(false);
            binding.decrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.encrypt.setEnabled(true);
                    binding.decrypt.setEnabled(false);
                    onOperation(Cipher.DECRYPT_MODE);
                }
            });
        } else {
            log("Secure lock screen hasn't set up.");
            disableViews(binding.secret, binding.encrypt, binding.decrypt);
        }

        generateKeyIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS:
                if (resultCode == RESULT_OK) {
                    onOperation(operationMode);
                }
                break;
        }
    }

    @NonNull
    @Override
    protected ViewLogBinding getViewLogBinding() {
        return binding.included;
    }

    private static void disableViews(@NonNull View... views) {
        for (View view : views) {
            view.setEnabled(false);
        }
    }

    @NonNull
    private static KeyStore getKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null);
        return keyStore;
    }

    @NonNull
    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(ALGORITHM + "/" + BLOCK_MODE + "/" + ENCRYPTION_PADDING);
    }

    private boolean generateKeyIfNeeded() {
        try {
            if (getKeyStore().containsAlias(KEY_ALIAS)) {
                log("key is present");
                return true;
            }
            generateKey();
            log("new key is generated");
            return true;
        } catch (Throwable e) {
            handleError(e);
        }
        return false;
    }

    private void generateKey() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            log("generating new key for API " + Build.VERSION_CODES.M + '+');
            int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(KEY_ALIAS, purposes)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(30)
                    .build();

            try {
                KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM, KEYSTORE_TYPE);
                generator.init(spec);
                generator.generateKey();
            } catch (Throwable e) {
                handleError(e);
            }
        } else {
            log("not implemented yet");
        }
    }

    private void performOperation(@NonNull SecretKey key, @OperationMode int mode) {
        String original = binding.secret.getText().toString();

        try {
            Cipher cipher = getCipher();

            byte[] bytes;
            if (mode == Cipher.ENCRYPT_MODE) {
                bytes = original.getBytes();
                cipher.init(mode, key);
                saveIv(cipher.getIV());
            } else {
                bytes = fromBase64(original);
                byte[] iv = loadIv();

                if (iv == null) {
                    log("you should do encrypt first");
                    return;
                } else {
                    cipher.init(mode, key, new IvParameterSpec(iv));
                }
            }

            log("original: " + Arrays.toString(bytes));
            bytes = cipher.doFinal(bytes);
            log("result: " + Arrays.toString(bytes));
            binding.secret.setText(mode == Cipher.ENCRYPT_MODE ? toBase64(bytes) : new String(bytes));
        } catch (Throwable e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && e instanceof UserNotAuthenticatedException) {
                requestAuthentication(binding.encrypt.getText());
            } else {
                handleError(e);
            }
        }
    }

    private void saveIv(@NonNull byte[] iv) throws IOException {
        log("saving IV: " + Arrays.toString(iv));
        FileOutputStream stream = openFileOutput(IV_FILE, MODE_PRIVATE);
        stream.write(iv);
        stream.close();
    }

    @Nullable
    private byte[] loadIv() throws IOException {
        try {
            byte[] iv = new byte[IV_LENGTH];
            FileInputStream stream = openFileInput(IV_FILE);
            //noinspection ResultOfMethodCallIgnored
            stream.read(iv);
            stream.close();
            log("loaded IV: " + Arrays.toString(iv));
            return iv;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private void onOperation(@OperationMode int mode) {
        log("operation: started in mode " + mode);

        operationMode = mode;
        if (generateKeyIfNeeded()) {
            try {
                performOperation((SecretKey) getKeyStore().getKey(KEY_ALIAS, null), mode);
            } catch (Throwable e) {
                handleError(e);
            }
        } else {
            log("no key available; operation " + mode + " is canceled");
        }

        log("operation: finished\n");
    }

    private void requestAuthentication(@NonNull CharSequence operation) {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Confirm operation", operation);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        } else {
            log("try again");
        }
    }

    @IntDef(value = { Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE })
    @Retention(RetentionPolicy.SOURCE)
    private @interface OperationMode {
    }
}
