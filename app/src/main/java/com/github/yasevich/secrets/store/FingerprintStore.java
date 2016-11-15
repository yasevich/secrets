package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;

import com.github.yasevich.secrets.algorithm.Algorithm;

import java.security.spec.AlgorithmParameterSpec;

public final class FingerprintStore extends BaseAndroidKeyStore {

    @NonNull
    @Override
    protected AlgorithmParameterSpec createAlgorithmParameterSpec(@NonNull String alias, @NonNull Algorithm algorithm) {
        throw new UnsupportedOperationException("to be implemented");
    }
}
