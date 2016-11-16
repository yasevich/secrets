package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

abstract class BaseStore implements Store {

    @Override
    public final void removeEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        getKeyStore().deleteEntry(alias);
    }

    @Nullable
    @Override
    public final KeyStore.Entry getEntry(@NonNull String alias) throws GeneralSecurityException, IOException {
        return getKeyStore().getEntry(alias, getProtectionParameter());
    }

    @NonNull
    protected abstract KeyStore getKeyStore() throws GeneralSecurityException, IOException;

    @Nullable
    protected KeyStore.ProtectionParameter getProtectionParameter() {
        return null;
    }
}
