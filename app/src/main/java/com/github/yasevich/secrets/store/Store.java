package com.github.yasevich.secrets.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

public interface Store {

    @NonNull
    Key createKey(@NonNull String alias) throws GeneralSecurityException, IOException;

    void removeKey(@NonNull String alias) throws GeneralSecurityException, IOException;

    @Nullable
    Key getKey(@NonNull String alias) throws GeneralSecurityException, IOException;
}
