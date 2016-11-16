package com.github.yasevich.secrets.algorithm;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;

public final class CipherFactory {

    private CipherFactory() {
    }

    @NonNull
    public static Cipher getCipher(@NonNull Context context, @OperationMode int mode, @NonNull KeyStore.Entry entry)
            throws GeneralSecurityException, IOException {

        Algorithm algorithm;
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            algorithm = RsaAlgorithm.getInstance();
        } else if (entry instanceof KeyStore.SecretKeyEntry) {
            algorithm = AesAlgorithm.getInstance();
        } else {
            throw new IllegalArgumentException("entry '" + entry + "' is not supported");
        }
        return algorithm.getCipher(context, mode, entry);
    }
}
