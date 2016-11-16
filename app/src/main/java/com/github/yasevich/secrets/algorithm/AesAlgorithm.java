package com.github.yasevich.secrets.algorithm;

import android.support.annotation.NonNull;

final class AesAlgorithm extends BaseAlgorithm {

    private static final AesAlgorithm INSTANCE = new AesAlgorithm();

    private AesAlgorithm() {
    }

    @NonNull
    static AesAlgorithm getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public String getName() {
        return "AES";
    }

    @NonNull
    @Override
    public String getBlockMode() {
        return "CBC";
    }

    @NonNull
    @Override
    public String getEncryptionPadding() {
        return "PKCS7Padding";
    }
}
