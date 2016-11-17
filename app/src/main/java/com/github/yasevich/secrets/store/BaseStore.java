package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

abstract class BaseStore implements Store {

    @Nullable
    private KeyStore keyStore;

    @Override
    public void removeEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        getKeyStore().deleteEntry(alias);
    }

    @Nullable
    @Override
    public final KeyStore.Entry getEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        return getKeyStore().getEntry(alias, getProtectionParameter());
    }

    @NonNull
    protected abstract KeyStore loadKeyStore() throws GeneralSecurityException, IOException;

    @Nullable
    protected KeyStore.ProtectionParameter getProtectionParameter() {
        return null;
    }

    @NonNull
    final KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        if (keyStore == null) {
            keyStore = loadKeyStore();
        }
        return keyStore;
    }
}
