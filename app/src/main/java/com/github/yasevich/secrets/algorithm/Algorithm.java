package com.github.yasevich.secrets.algorithm;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;

public interface Algorithm {

    @NonNull
    String getName();

    @NonNull
    String getBlockMode();

    @NonNull
    String getEncryptionPadding();

    @NonNull
    Cipher getCipher(@NonNull Context context, @OperationMode int mode, @NonNull KeyStore.Entry entry)
            throws GeneralSecurityException, IOException;
}
