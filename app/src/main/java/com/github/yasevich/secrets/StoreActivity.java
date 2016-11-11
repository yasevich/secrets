package com.github.yasevich.secrets;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import com.github.yasevich.secrets.databinding.ViewLogBinding;

public abstract class StoreActivity extends AppCompatActivity {

    // Whether aliases are case sensitive is implementation dependent. In order to avoid problems, it is recommended not
    // to use aliases in a KeyStore that only differ in case. Source:
    // https://developer.android.com/reference/java/security/KeyStore.html
    protected static final String KEY_ALIAS = "secret";

    protected static final String ALGORITHM = "AES";

    private ViewLogBinding binding;

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
        binding = getViewLogBinding();
        binding.log.setTypeface(Typeface.MONOSPACE);
        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.log.setText(null);
            }
        });
    }

    @NonNull
    protected abstract ViewLogBinding getViewLogBinding();

    protected final void handleError(@NonNull Throwable e) {
        log(e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace();
    }

    protected final void log(@NonNull String message) {
        StringBuilder builder = new StringBuilder(binding.log.getText());
        if (builder.length() > 0) {
            builder.append('\n');
        }
        binding.log.setText(builder.append(message).toString());
    }
}
