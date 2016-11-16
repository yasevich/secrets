package com.github.yasevich.secrets;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.yasevich.secrets.databinding.ActivityMainBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;

import java.security.Provider;
import java.security.Security;

public final class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.simpleStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleStoreActivity.start(view.getContext());
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.credentialsStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CredentialsStoreActivity.start(view.getContext());
                }
            });
        } else {
            binding.credentialsStore.setEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.fingerprintStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FingerprintStoreActivity.start(view.getContext());
                }
            });
        } else {
            binding.fingerprintStore.setEnabled(false);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        log("API level: " + Build.VERSION.SDK_INT + " " + Build.VERSION.CODENAME);
        log("List of all available providers:");
        for (Provider provider : Security.getProviders()) {
            log(provider.toString());
        }
    }

    @NonNull
    @Override
    protected ViewLogBinding getLogBinding() {
        return binding.log;
    }
}
