package com.github.yasevich.secrets;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.yasevich.secrets.databinding.ActivityCredentialsStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;

public final class CredentialsStoreActivity extends StoreActivity {

    private ActivityCredentialsStoreBinding binding;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, CredentialsStoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credentials_store);
    }

    @NonNull
    @Override
    protected ViewLogBinding getViewLogBinding() {
        return binding.included;
    }
}
