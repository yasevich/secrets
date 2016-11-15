package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;

import com.github.yasevich.secrets.algorithm.Algorithm;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyGenerator;

abstract class BaseAndroidKeyStore extends BaseStore {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "AndroidKeyStore";

    @NonNull
    @Override
    public Key createKey(@NonNull String alias) throws GeneralSecurityException {
        Algorithm algorithm = Algorithm.Factory.getAlgorithm();

        KeyGenerator generator = KeyGenerator.getInstance(algorithm.getName(), KEYSTORE_TYPE);
        generator.init(createAlgorithmParameterSpec(alias, algorithm));
        return generator.generateKey();
    }

    @NonNull
    protected abstract AlgorithmParameterSpec createAlgorithmParameterSpec(
            @NonNull String alias, @NonNull Algorithm algorithm);

    @NonNull
    protected final KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null);
        return keyStore;
    }
}
