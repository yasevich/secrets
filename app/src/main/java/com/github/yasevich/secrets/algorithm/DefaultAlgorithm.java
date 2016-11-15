package com.github.yasevich.secrets.algorithm;

import android.support.annotation.NonNull;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

final class DefaultAlgorithm implements Algorithm {

    private static final DefaultAlgorithm INSTANCE = new DefaultAlgorithm();

    private DefaultAlgorithm() {
    }

    @NonNull
    static DefaultAlgorithm getInstance() {
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

    @NonNull
    @Override
    public Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(getName() + "/" + getBlockMode() + "/" + getEncryptionPadding());
    }
}
