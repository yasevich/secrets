package com.github.yasevich.secrets.store;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.github.yasevich.secrets.algorithm.Algorithm;

import java.security.spec.AlgorithmParameterSpec;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class CredentialsStore extends BaseAndroidKeyStore {

    @SuppressWarnings("WrongConstant")
    @NonNull
    @Override
    protected AlgorithmParameterSpec createAlgorithmParameterSpec(@NonNull String alias, @NonNull Algorithm algorithm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            return new KeyGenParameterSpec.Builder(alias, purposes)
                    .setBlockModes(algorithm.getBlockMode())
                    .setEncryptionPaddings(algorithm.getEncryptionPadding())
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(30)
                    .build();
        } else {
            throw new UnsupportedOperationException("not implemented yet");
        }
    }
}
