package com.github.yasevich.secrets.store;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.github.yasevich.secrets.algorithm.AesAlgorithm;
import com.github.yasevich.secrets.algorithm.Algorithm;
import com.github.yasevich.secrets.algorithm.RsaAlgorithm;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyGenerator;

abstract class BaseAndroidKeyStore extends BaseStore {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "AndroidKeyStore";

    @NonNull
    @Override
    public KeyStore.Entry createEntry(@NonNull String alias) throws GeneralSecurityException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Algorithm algorithm = AesAlgorithm.getInstance();
            KeyGenerator generator = KeyGenerator.getInstance(algorithm.getName(), KEYSTORE_TYPE);
            generator.init(createAlgorithmParameterSpec(alias, algorithm));
            return new KeyStore.SecretKeyEntry(generator.generateKey());
        } else {
            Algorithm algorithm = RsaAlgorithm.getInstance();
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm.getName(), KEYSTORE_TYPE);
            generator.initialize(createAlgorithmParameterSpec(alias, algorithm));
            generator.generateKeyPair();

            try {
                KeyStore.Entry entry = getEntry(alias);
                if (entry == null) {
                    throw new IllegalStateException("entry '" + alias + "' has been removed");
                }
                return entry;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @NonNull
    protected final KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null);
        return keyStore;
    }

    @NonNull
    protected abstract AlgorithmParameterSpec createAlgorithmParameterSpec(
            @NonNull String alias, @NonNull Algorithm algorithm);

    @SuppressWarnings("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    final AlgorithmParameterSpec createKeyGenParameterSpec(@NonNull String alias, @NonNull Algorithm algorithm) {
        int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
        return new KeyGenParameterSpec.Builder(alias, purposes)
                .setBlockModes(algorithm.getBlockMode())
                .setEncryptionPaddings(algorithm.getEncryptionPadding())
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(30)
                .build();
    }
}
