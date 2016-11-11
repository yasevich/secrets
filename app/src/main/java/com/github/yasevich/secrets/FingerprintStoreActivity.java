package com.github.yasevich.secrets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.yasevich.secrets.databinding.ActivityFingerprintStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;

@TargetApi(Build.VERSION_CODES.M)
public final class FingerprintStoreActivity extends StoreActivity {

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
    protected ViewLogBinding getViewLogBinding() {
        return binding.included;
    }
}
