package com.github.yasevich.secrets.algorithm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public final class AesAlgorithm extends BaseAlgorithm {

    private static final AesAlgorithm INSTANCE = new AesAlgorithm();

    private static final String IV_FILE = "iv";

    private static final int IV_LENGTH = 16;

    private AesAlgorithm() {
    }

    @NonNull
    public static AesAlgorithm getInstance() {
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
    public Cipher getCipher(@NonNull Context context, @OperationMode int mode, @NonNull KeyStore.Entry entry)
            throws GeneralSecurityException, IOException {

        if (entry instanceof KeyStore.SecretKeyEntry) {
            Cipher cipher = getCipher();
            Key key = ((KeyStore.SecretKeyEntry) entry).getSecretKey();

            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(mode, key);
                saveIv(context, cipher.getIV());
            } else {
                byte[] iv = loadIv(context);
                if (iv == null) {
                    throw new IllegalStateException("trying to call decrypt before encrypt");
                } else {
                    cipher.init(mode, key, new IvParameterSpec(iv));
                }
            }

            return cipher;
        } else {
            throw new IllegalArgumentException("entry '" + entry + "' is not supported");
        }
    }

    private static void saveIv(@NonNull Context context, @NonNull byte[] iv) throws IOException {
        FileOutputStream stream = context.openFileOutput(IV_FILE, Context.MODE_PRIVATE);
        stream.write(iv);
        stream.close();
    }

    @Nullable
    private static byte[] loadIv(@NonNull Context context) throws IOException {
        try {
            byte[] iv = new byte[IV_LENGTH];
            FileInputStream stream = context.openFileInput(IV_FILE);
            //noinspection ResultOfMethodCallIgnored
            stream.read(iv);
            stream.close();
            return iv;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
