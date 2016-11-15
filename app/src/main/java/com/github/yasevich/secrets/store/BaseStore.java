package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

abstract class BaseStore implements Store {

    @Override
    public final void removeKey(@NonNull String alias) throws GeneralSecurityException, IOException {
        getKeyStore().deleteEntry(alias);
    }

    @Nullable
    @Override
    public final Key getKey(@NonNull String alias) throws GeneralSecurityException, IOException {
        return getKeyStore().getKey(alias, null);
    }

    @NonNull
    protected abstract KeyStore getKeyStore() throws GeneralSecurityException, IOException;
}
