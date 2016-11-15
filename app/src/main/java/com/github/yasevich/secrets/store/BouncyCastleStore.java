package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public final class BouncyCastleStore extends BaseStore {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "BouncyCastle";

    @Nullable
    private byte[] store;
    @Nullable
    private char[] password;

    @NonNull
    @Override
    public Key createKey(@NonNull String alias) throws GeneralSecurityException, IOException {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        SecretKey secretKey = generator.generateKey();

        KeyStore keyStore = getKeyStore();
        keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(secretKey), null);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        keyStore.store(stream, password);
        store = stream.toByteArray();

        return secretKey;
    }

    public void setPassword(@Nullable char[] password) {
        this.password = password;
    }

    @NonNull
    protected KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        if (store == null) {
            keyStore.load(null);
        } else {
            keyStore.load(new ByteArrayInputStream(store), password);
        }
        return keyStore;
    }
}
