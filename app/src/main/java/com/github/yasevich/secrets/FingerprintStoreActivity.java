package com.github.yasevich.secrets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.github.yasevich.secrets.databinding.ActivityFingerprintStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;
import com.github.yasevich.secrets.databinding.ViewStoreActionsBinding;
import com.github.yasevich.secrets.store.FingerprintStore;
import com.github.yasevich.secrets.store.Store;

@TargetApi(Build.VERSION_CODES.M)
public final class FingerprintStoreActivity extends StoreActivity {

    @NonNull
    private final Store store = new FingerprintStore();

    private ActivityFingerprintStoreBinding binding;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, FingerprintStoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fingerprint_store);
    }

    @NonNull
    @Override
    protected Store getStore() {
        return store;
    }

    @Override
    protected boolean isStoreActionsEnabled() {
        return false;
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
}
