package com.github.yasevich.secrets.store;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.github.yasevich.secrets.algorithm.Algorithm;

import java.security.spec.AlgorithmParameterSpec;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class FingerprintStore extends BaseAndroidKeyStore {

    @NonNull
    @Override
    protected AlgorithmParameterSpec createAlgorithmParameterSpec(@NonNull String alias, @NonNull Algorithm algorithm) {
        return createKeyGenParameterSpec(alias, algorithm);
    }
}
