package com.github.yasevich.secrets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.github.yasevich.secrets.algorithm.CipherFactory;
import com.github.yasevich.secrets.algorithm.OperationMode;
import com.github.yasevich.secrets.databinding.ViewLogBinding;
import com.github.yasevich.secrets.databinding.ViewStoreActionsBinding;
import com.github.yasevich.secrets.store.Store;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Arrays;

import javax.crypto.Cipher;

public abstract class StoreActivity extends BaseActivity {

    // Whether aliases are case sensitive is implementation dependent. In order to avoid problems, it is recommended not
    // to use aliases in a KeyStore that only differ in case. Source:
    // https://developer.android.com/reference/java/security/KeyStore.html
    protected static final String KEY_ALIAS = "secret";

    @Nullable
    private ViewStoreActionsBinding storeActionsBinding;

    @Nullable
    protected static String toBase64(@Nullable byte[] data) {
        return data != null ? Base64.encodeToString(data, Base64.DEFAULT) : null;
    }

    @Nullable
    protected static byte[] fromBase64(@Nullable String data) {
        return data != null ? Base64.decode(data, Base64.DEFAULT) : null;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        storeActionsBinding = getStoreActionsBinding();
        if (isStoreActionsEnabled()) {
            storeActionsBinding.removeKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRemove();
                }
            });

            storeActionsBinding.encrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeActionsBinding.encrypt.setEnabled(false);
                    storeActionsBinding.decrypt.setEnabled(true);
                    onOperation(Cipher.ENCRYPT_MODE);
                }
            });

            storeActionsBinding.decrypt.setEnabled(false);
            storeActionsBinding.decrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeActionsBinding.encrypt.setEnabled(true);
                    storeActionsBinding.decrypt.setEnabled(false);
                    onOperation(Cipher.DECRYPT_MODE);
                }
            });
        } else {
            log("store actions not available");
            disableViews(storeActionsBinding.removeKey, storeActionsBinding.encrypt, storeActionsBinding.decrypt);
        }
    }

    @NonNull
    protected abstract Store getStore();

    protected abstract boolean isStoreActionsEnabled();

    @NonNull
    protected abstract EditText getSecret();

    @NonNull
    protected abstract ViewStoreActionsBinding getStoreActionsBinding();

    @NonNull
    protected abstract ViewLogBinding getLogBinding();

    protected final void onRemove() {
        try {
            getStore().removeEntry(KEY_ALIAS);
            log("key removed");
        } catch (Throwable e) {
            handleError(e);
        }
    }

    protected final void perform(@NonNull KeyStore.Entry entry, @OperationMode int mode)
            throws GeneralSecurityException, IOException {

        EditText secret = getSecret();
        String original = secret.getText().toString();

        byte[] bytes = mode == Cipher.ENCRYPT_MODE ? original.getBytes() : fromBase64(original);
        log("original: " + Arrays.toString(bytes));

        bytes = CipherFactory.getCipher(this, mode, entry).doFinal(bytes);
        log("result: " + Arrays.toString(bytes));

        secret.setText(mode == Cipher.ENCRYPT_MODE ? toBase64(bytes) : new String(bytes));
    }

    protected void onOperation(@OperationMode int mode) {
        log("operation: started in mode " + mode);

        try {
            Store store = getStore();
            KeyStore.Entry entry = store.getEntry(KEY_ALIAS);
            if (entry == null) {
                log("creating new key");
                entry = store.createEntry(KEY_ALIAS);
                log("new key created");
            } else {
                log("key found");
            }
            perform(entry, mode);
        } catch (Throwable e) {
            handleError(e);
        }

        log("operation: finished\n");
    }

    protected void handleError(@NonNull Throwable e) {
        log(e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace();
    }

    private static void disableViews(@NonNull View... views) {
        for (View view : views) {
            view.setEnabled(false);
        }
    }
}
