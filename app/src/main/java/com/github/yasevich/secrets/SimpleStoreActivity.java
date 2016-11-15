package com.github.yasevich.secrets;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.github.yasevich.secrets.databinding.ActivitySimpleStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;
import com.github.yasevich.secrets.databinding.ViewStoreActionsBinding;
import com.github.yasevich.secrets.store.BouncyCastleStore;

public final class SimpleStoreActivity extends StoreActivity {

    @NonNull
    private final BouncyCastleStore store = new BouncyCastleStore();

    private ActivitySimpleStoreBinding binding;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, SimpleStoreActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_simple_store);
    }

    @Override
    @NonNull
    public BouncyCastleStore getStore() {
        store.setPassword(getPassword());
        return store;
    }

    @Override
    protected boolean isStoreActionsEnabled() {
        return true;
    }

    @NonNull
    @Override
    protected EditText getSecret() {
        return binding.secret;
    }

    @NonNull
    @Override
    protected ViewStoreActionsBinding getStoreActionsBinding() {
        return binding.storeActions;
    }

    @NonNull
    @Override
    protected ViewLogBinding getLogBinding() {
        return binding.log;
    }

    @NonNull
    private char[] getPassword() {
        return binding.password.getText()
                .toString()
                .toCharArray();
    }
}
