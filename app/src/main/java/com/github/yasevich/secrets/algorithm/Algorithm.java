package com.github.yasevich.secrets.algorithm;

import android.support.annotation.NonNull;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public interface Algorithm {

    @NonNull
    String getName();

    @NonNull
    String getBlockMode();

    @NonNull
    String getEncryptionPadding();

    @NonNull
    Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException;

    final class Factory {

        private Factory() {
        }

        public static Algorithm getAlgorithm() {
            return DefaultAlgorithm.getInstance();
        }
    }
}
