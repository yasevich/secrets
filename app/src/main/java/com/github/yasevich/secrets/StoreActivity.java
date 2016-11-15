package com.github.yasevich.secrets;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.security.keystore.KeyProperties;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.github.yasevich.secrets.databinding.ViewLogBinding;
import com.github.yasevich.secrets.databinding.ViewStoreActionsBinding;
import com.github.yasevich.secrets.store.Store;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public abstract class StoreActivity extends AppCompatActivity {

    // Whether aliases are case sensitive is implementation dependent. In order to avoid problems, it is recommended not
    // to use aliases in a KeyStore that only differ in case. Source:
    // https://developer.android.com/reference/java/security/KeyStore.html
    protected static final String KEY_ALIAS = "secret";

    protected static final String ALGORITHM = "AES";

    @SuppressLint("InlinedApi")
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    @SuppressLint("InlinedApi")
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;

    private static final String IV_FILE = "iv";

    private static final int IV_LENGTH = 16;

    @Nullable
    private ViewStoreActionsBinding storeActionsBinding;
    @Nullable
    private ViewLogBinding logBinding;

    @Nullable
    protected static String toBase64(@Nullable byte[] data) {
        return data != null ? Base64.encodeToString(data, Base64.DEFAULT) : null;
    }

    @Nullable
    protected static byte[] fromBase64(@Nullable String data) {
        return data != null ? Base64.decode(data, Base64.DEFAULT) : null;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        storeActionsBinding = getStoreActionsBinding();
        if (isStoreActionsEnabled()) {
            storeActionsBinding.removeKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRemove();
                }
            });

            storeActionsBinding.encrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeActionsBinding.encrypt.setEnabled(false);
                    storeActionsBinding.decrypt.setEnabled(true);
                    onOperation(Cipher.ENCRYPT_MODE);
                }
            });

            storeActionsBinding.decrypt.setEnabled(false);
            storeActionsBinding.decrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeActionsBinding.encrypt.setEnabled(true);
                    storeActionsBinding.decrypt.setEnabled(false);
                    onOperation(Cipher.DECRYPT_MODE);
                }
            });
        } else {
            log("store actions not available");
            disableViews(storeActionsBinding.removeKey, storeActionsBinding.encrypt, storeActionsBinding.decrypt);
        }

        logBinding = getLogBinding();
        logBinding.log.setTypeface(Typeface.MONOSPACE);
        logBinding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logBinding.log.setText(null);
            }
        });
    }

    @NonNull
    protected abstract Store getStore();

    protected abstract boolean isStoreActionsEnabled();

    @NonNull
    protected abstract EditText getSecret();

    @NonNull
    protected abstract ViewStoreActionsBinding getStoreActionsBinding();

    @NonNull
    protected abstract ViewLogBinding getLogBinding();

    protected final void onRemove() {
        try {
            getStore().removeKey(KEY_ALIAS);
            log("key removed");
        } catch (Throwable e) {
            handleError(e);
        }
    }

    protected final void perform(@NonNull SecretKey key, @OperationMode int mode)
            throws GeneralSecurityException, IOException {

        EditText secret = getSecret();
        String original = secret.getText().toString();

        byte[] bytes;
        Cipher cipher = getCipher();

        if (mode == Cipher.ENCRYPT_MODE) {
            bytes = original.getBytes();
            cipher.init(mode, key);
            saveIv(cipher.getIV());
        } else {
            bytes = fromBase64(original);
            byte[] iv = loadIv();

            if (iv == null) {
                throw new IllegalStateException("trying to call decypt before encrypt");
            } else {
                cipher.init(mode, key, new IvParameterSpec(iv));
            }
        }

        log("original: " + Arrays.toString(bytes));
        bytes = cipher.doFinal(bytes);
        log("result: " + Arrays.toString(bytes));

        secret.setText(mode == Cipher.ENCRYPT_MODE ? toBase64(bytes) : new String(bytes));
    }

    protected final void log(@NonNull String message) {
        if (logBinding == null) return;

        StringBuilder builder = new StringBuilder(logBinding.log.getText());
        if (builder.length() > 0) {
            builder.append('\n');
        }
        logBinding.log.setText(builder.append(message).toString());
    }

    protected void onOperation(@OperationMode int mode) {
        log("operation: started in mode " + mode);

        try {
            Store store = getStore();
            Key key = store.getKey(KEY_ALIAS);
            if (key == null) {
                log("creating new key");
                key = store.createKey(KEY_ALIAS);
                log("new key created");
            } else {
                log("key found");
            }
            perform((SecretKey) key, mode);
        } catch (Throwable e) {
            handleError(e);
        }

        log("operation: finished\n");
    }

    protected void handleError(@NonNull Throwable e) {
        log(e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace();
    }

    private static void disableViews(@NonNull View... views) {
        for (View view : views) {
            view.setEnabled(false);
        }
    }

    @NonNull
    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(ALGORITHM + "/" + BLOCK_MODE + "/" + ENCRYPTION_PADDING);
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

    @IntDef(value = { Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface OperationMode {
    }
}
