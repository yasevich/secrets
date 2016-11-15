package com.github.yasevich.secrets.store;

import android.annotation.SuppressLint;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class AndroidKeyStore extends BaseStore {

    // Android provides the following KeyStore types:
    // https://developer.android.com/reference/java/security/KeyStore.html
    private static final String KEYSTORE_TYPE = "AndroidKeyStore";

    @SuppressLint("InlinedApi")
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    @SuppressLint("InlinedApi")
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;

    @NonNull
    @Override
    public Key createKey(@NonNull String alias) throws GeneralSecurityException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias, purposes)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(ENCRYPTION_PADDING)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(30)
                    .build();

            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM, KEYSTORE_TYPE);
            generator.init(spec);
            return generator.generateKey();
        } else {
            throw new UnsupportedOperationException("not implemented yet");
        }
    }

    @NonNull
    protected KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null);
        return keyStore;
    }
}
