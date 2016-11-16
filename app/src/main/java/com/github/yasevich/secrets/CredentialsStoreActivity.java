package com.github.yasevich.secrets;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.github.yasevich.secrets.algorithm.OperationMode;
import com.github.yasevich.secrets.databinding.ActivityCredentialsStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;
import com.github.yasevich.secrets.databinding.ViewStoreActionsBinding;
import com.github.yasevich.secrets.store.CredentialsStore;
import com.github.yasevich.secrets.store.Store;

import javax.crypto.Cipher;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class CredentialsStoreActivity extends StoreActivity {

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 100;

    private Store store;
    private KeyguardManager keyguardManager;

    private ActivityCredentialsStoreBinding binding;

    @OperationMode
    private int operationMode;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, CredentialsStoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credentials_store);

        store = new CredentialsStore(this);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS:
                if (resultCode == RESULT_OK) {
                    onOperation(operationMode);
                }
                break;
        }
    }

    @Override
    @NonNull
    public Store getStore() {
        return store;
    }

    @Override
    protected boolean isStoreActionsEnabled() {
        return keyguardManager.isKeyguardSecure();
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

    @Override
    protected void onOperation(@OperationMode int mode) {
        operationMode = mode;
        super.onOperation(mode);
    }

    @Override
    protected void handleError(@NonNull Throwable e) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && e instanceof UserNotAuthenticatedException) {
            log("user authentication is required");
            requestAuthentication(getOperationName());
        } else {
            super.handleError(e);
        }
    }

    @NonNull
    private CharSequence getOperationName() {
        ViewStoreActionsBinding actions = binding.storeActions;
        return (operationMode == Cipher.ENCRYPT_MODE ? actions.encrypt : actions.decrypt).getText();
    }

    private void requestAuthentication(@NonNull CharSequence operation) {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(operation, "Confirm operation");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        } else {
            log("try again");
        }
    }
}
