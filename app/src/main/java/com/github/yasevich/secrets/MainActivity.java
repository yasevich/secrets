package com.github.yasevich.secrets;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.yasevich.secrets.databinding.ActivityMainBinding;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.simpleStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleStoreActivity.start(view.getContext());
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
}
