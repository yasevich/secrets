package com.github.yasevich.secrets.store;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.github.yasevich.secrets.algorithm.Algorithm;

import java.security.spec.AlgorithmParameterSpec;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class CredentialsStore extends BaseAndroidKeyStore {

    @NonNull
    @Override
    protected AlgorithmParameterSpec createAlgorithmParameterSpec(@NonNull String alias, @NonNull Algorithm algorithm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return createKeyGenParameterSpec(alias, algorithm);
        } else {
            throw new UnsupportedOperationException("not implemented yet");
        }
    }
}
