package com.github.yasevich.secrets;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.yasevich.secrets.databinding.ViewLogBinding;

abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    private ViewLogBinding logBinding;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        logBinding = getLogBinding();
        logBinding.log.setTypeface(Typeface.MONOSPACE);
        logBinding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logBinding.log.setText(null);
            }
        });
    }

    @NonNull
    protected abstract ViewLogBinding getLogBinding();

    protected final void log(@NonNull String message) {
        if (logBinding == null) return;

        StringBuilder builder = new StringBuilder(logBinding.log.getText());
        if (builder.length() > 0) {
            builder.append('\n');
        }
        logBinding.log.setText(builder.append(message).toString());
    }
}
