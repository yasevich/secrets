package com.github.yasevich.secrets.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.yasevich.secrets.algorithm.AesAlgorithm;
import com.github.yasevich.secrets.algorithm.Algorithm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public final class SimpleStore extends BaseStore {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "BouncyCastle";

    private static final String KEYSTORE_FILE = "bc.keystore";

    @NonNull
    private final Context context;

    @Nullable
    private char[] password;

    public SimpleStore(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public KeyStore.Entry createEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        Algorithm algorithm = AesAlgorithm.getInstance();

        KeyGenerator generator = KeyGenerator.getInstance(algorithm.getName());
        SecretKey secretKey = generator.generateKey();

        KeyStore keyStore = getKeyStore();

        KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
        keyStore.setEntry(alias, entry, getProtectionParameter());

        OutputStream stream = null;
        try {
            stream = context.openFileOutput(KEYSTORE_FILE, Context.MODE_PRIVATE);
            keyStore.store(stream, password);
        } finally {
            if (stream != null) {
                //noinspection ThrowFromFinallyBlock
                stream.close();
            }
        }

        return entry;
    }

    @Override
    public void removeEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        context.deleteFile(KEYSTORE_FILE);
        getKeyStore().load(null);
    }

    public void setPassword(@Nullable char[] password) {
        this.password = password;
    }

    @NonNull
    protected KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        InputStream inputStream = null;

        try {
            inputStream = openKeyStoreStream();
            keyStore.load(inputStream, password);
        } finally {
            if (inputStream != null) {
                //noinspection ThrowFromFinallyBlock
                inputStream.close();
            }
        }

        return keyStore;
    }

    @Nullable
    @Override
    protected KeyStore.ProtectionParameter getProtectionParameter() {
        return new KeyStore.PasswordProtection(password);
    }

    @Nullable
    private InputStream openKeyStoreStream() {
        try {
            return context.openFileInput(KEYSTORE_FILE);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
