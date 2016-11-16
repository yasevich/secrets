package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public interface Store {

    @NonNull
    KeyStore.Entry createEntry(@NonNull String alias) throws GeneralSecurityException, IOException;

    void removeEntry(@NonNull String alias) throws GeneralSecurityException, IOException;

    @Nullable
    KeyStore.Entry getEntry(@NonNull String alias) throws GeneralSecurityException, IOException;
}
