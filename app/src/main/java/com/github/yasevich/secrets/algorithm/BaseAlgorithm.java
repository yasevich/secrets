package com.github.yasevich.secrets.algorithm;

import android.support.annotation.NonNull;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

abstract class BaseAlgorithm implements Algorithm {

    @NonNull
    final Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(getName() + "/" + getBlockMode() + "/" + getEncryptionPadding());
    }
}
