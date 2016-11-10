package com.github.yasevich.secrets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.yasevich.secrets.databinding.ViewLogBinding;

public abstract class StoreActivity extends AppCompatActivity {

    private ViewLogBinding binding;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding = getViewLogBinding();
        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.log.setText(null);
            }
        });
    }

    @NonNull
    protected abstract ViewLogBinding getViewLogBinding();

    protected final void log(@NonNull String message) {
        StringBuilder builder = new StringBuilder(binding.log.getText());
        if (builder.length() > 0) {
            builder.append('\n');
        }
        binding.log.setText(builder.append(message).toString());
    }
}
