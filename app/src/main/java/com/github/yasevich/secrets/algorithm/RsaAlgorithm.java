package com.github.yasevich.secrets.algorithm;

import android.support.annotation.NonNull;

final class RsaAlgorithm extends BaseAlgorithm {

    private static final RsaAlgorithm INSTANCE = new RsaAlgorithm();

    private RsaAlgorithm() {
    }

    @NonNull
    static RsaAlgorithm getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String getName() {
        return "RSA";
    }

    @NonNull
    @Override
    public String getBlockMode() {
        return "ECB";
    }

    @NonNull
    @Override
    public String getEncryptionPadding() {
        return "PKCS1Padding";
    }
}
