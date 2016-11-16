package com.github.yasevich.secrets.algorithm;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;

public final class RsaAlgorithm extends BaseAlgorithm {

    private static final RsaAlgorithm INSTANCE = new RsaAlgorithm();

    private RsaAlgorithm() {
    }

    @NonNull
    public static RsaAlgorithm getInstance() {
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

    @NonNull
    @Override
    public Cipher getCipher(@NonNull Context context, @OperationMode int mode, @NonNull KeyStore.Entry entry)
            throws GeneralSecurityException, IOException {

        if (entry instanceof KeyStore.PrivateKeyEntry) {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            Key key = mode == Cipher.ENCRYPT_MODE ?
                    privateKeyEntry.getCertificate().getPublicKey() :
                    privateKeyEntry.getPrivateKey();

            Cipher cipher = getCipher();
            cipher.init(mode, key);
            return cipher;
        } else {
            throw new IllegalArgumentException("entry '" + entry + "' is not supported");
        }
    }
}
