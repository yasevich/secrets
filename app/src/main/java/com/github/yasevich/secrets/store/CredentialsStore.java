package com.github.yasevich.secrets.store;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.github.yasevich.secrets.algorithm.Algorithm;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class CredentialsStore extends BaseAndroidKeyStore {

    @NonNull
    private final Context context;

    public CredentialsStore(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected AlgorithmParameterSpec createAlgorithmParameterSpec(@NonNull String alias, @NonNull Algorithm algorithm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return createKeyGenParameterSpec(alias, algorithm);
        } else {
            return createKeyPairGeneratorSpec(alias);
        }
    }

    @SuppressWarnings("deprecation")
    @NonNull
    private KeyPairGeneratorSpec createKeyPairGeneratorSpec(@NonNull String alias) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 100);

        return new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setEncryptionRequired()
                .setSubject(new X500Principal("CN=" + alias))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
    }
}
