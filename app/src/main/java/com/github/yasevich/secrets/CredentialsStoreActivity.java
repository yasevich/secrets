package com.github.yasevich.secrets;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

public final class CredentialsStoreActivity extends AppCompatActivity {

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, CredentialsStoreActivity.class));
    }
}
