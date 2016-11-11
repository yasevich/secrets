package com.github.yasevich.secrets;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.github.yasevich.secrets.databinding.ActivitySimpleStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.spec.SecretKeySpec;

public final class SimpleStoreActivity extends StoreActivity {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "BouncyCastle";

    @Nullable
    private byte[] store;

    private ActivitySimpleStoreBinding binding;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, SimpleStoreActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_simple_store);

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
    @Override
    protected ViewLogBinding getViewLogBinding() {
        return binding.included;
    }

    @NonNull
    private KeyStore getKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        if (store == null) {
            keyStore.load(null);
        } else {
            keyStore.load(new ByteArrayInputStream(store), getPassword());
        }
        return keyStore;
    }

    private void storeSecret(@NonNull String secret)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        log("storing secret: " + secret);
        KeyStore.SecretKeyEntry keyEntry = new KeyStore.SecretKeyEntry(new SecretKeySpec(secret.getBytes(), ALGORITHM));
        log("key entry: " + keyEntry);

        KeyStore keyStore = getKeyStore();
        keyStore.setEntry(KEY_ALIAS, keyEntry, null);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        keyStore.store(stream, getPassword());
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

        log("found entry: " + entry);
        String secret = new String(entry.getSecretKey().getEncoded());
        log("restored secret: " + secret);
        return secret;
    }

    private void logStore() {
        log("store contains: " + toBase64(store));
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
            handleError(e);
        }
    }

    private void onRestore() {
        try {
            binding.secret.setText(getSecret());
        } catch (Throwable e) {
            handleError(e);
        }
    }

    @NonNull
    private char[] getPassword() {
        return binding.password.getText()
                .toString()
                .toCharArray();
    }
}
