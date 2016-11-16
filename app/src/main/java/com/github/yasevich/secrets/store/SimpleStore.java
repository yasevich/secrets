package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.yasevich.secrets.algorithm.AesAlgorithm;
import com.github.yasevich.secrets.algorithm.Algorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;

public final class SimpleStore extends BaseStore {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "BouncyCastle";

    @Nullable
    private byte[] store;
    @Nullable
    private char[] password;

    @NonNull
    @Override
    public KeyStore.Entry createEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        Algorithm algorithm = AesAlgorithm.getInstance();

        KeyGenerator generator = KeyGenerator.getInstance(algorithm.getName());
        KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(generator.generateKey());

        KeyStore keyStore = getKeyStore();
        keyStore.setEntry(alias, entry, getProtectionParameter());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        keyStore.store(stream, password);
        store = stream.toByteArray();

        return entry;
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

    @Nullable
    @Override
    protected KeyStore.ProtectionParameter getProtectionParameter() {
        return new KeyStore.PasswordProtection(password);
    }
}
