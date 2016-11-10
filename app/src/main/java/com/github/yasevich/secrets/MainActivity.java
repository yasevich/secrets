package com.github.yasevich.secrets;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.github.yasevich.secrets.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    // Whether aliases are case sensitive is implementation dependent. In order to avoid problems, it is recommended not
    // to use aliases in a KeyStore that only differ in case. Source:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEY_ALIAS = "secret";
    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "BouncyCastle";

    private static final char[] KEYSTORE_PASSWORD = "password".toCharArray();

    @Nullable
    private byte[] store;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStore();
            }
        });

        binding.restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRestore();
            }
        });
    }

    @NonNull
    private KeyStore getKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        if (store == null) {
            keyStore.load(null);
        } else {
            keyStore.load(new ByteArrayInputStream(store), KEYSTORE_PASSWORD);
        }
        return keyStore;
    }

    private void storeSecret(@NonNull String secret)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        log("storing secret: " + secret);
        KeyStore.SecretKeyEntry keyEntry = new KeyStore.SecretKeyEntry(new SecretKeySpec(secret.getBytes(), "AES"));
        log("key entry: " + keyEntry.toString());

        KeyStore keyStore = getKeyStore();
        keyStore.setEntry(KEY_ALIAS, keyEntry, null);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        keyStore.store(stream, KEYSTORE_PASSWORD);
        store = stream.toByteArray();

        logStore();
    }

    @Nullable
    private String getSecret()
            throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, CertificateException,
            IOException {

        logStore();

        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) getKeyStore().getEntry(KEY_ALIAS, null);
        if (entry == null) {
            log("no secret found");
            return null;
        }

        log("found entry: " + entry.toString());
        String secret = new String(entry.getSecretKey().getEncoded());
        log("restored secret: " + secret);
        return secret;
    }

    private void logStore() {
        log("store contains: " + Base64.encodeToString(store, Base64.DEFAULT));
    }

    private void log(@NonNull String message) {
        StringBuilder builder = new StringBuilder(binding.log.getText());
        if (builder.length() > 0) {
            builder.append('\n');
        }
        binding.log.setText(builder.append(message).toString());
    }

    private void onStore() {
        String secret = binding.secret.getText().toString();
        if (TextUtils.isEmpty(secret)) {
            log("secret is empty\nnothing is stored");
            return;
        }

        try {
            storeSecret(secret);
        } catch (Throwable e) {
            log(e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onRestore() {
        try {
            binding.secret.setText(getSecret());
        } catch (Throwable e) {
            log(e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
