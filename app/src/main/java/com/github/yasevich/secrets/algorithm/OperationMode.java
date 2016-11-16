package com.github.yasevich.secrets.algorithm;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.crypto.Cipher;

@IntDef(value = { Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE })
@Retention(RetentionPolicy.SOURCE)
public @interface OperationMode {
}
