package com.github.yasevich.secrets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.widget.EditText;

import com.github.yasevich.secrets.algorithm.OperationMode;
import com.github.yasevich.secrets.databinding.ActivityFingerprintStoreBinding;
import com.github.yasevich.secrets.databinding.ViewLogBinding;
import com.github.yasevich.secrets.databinding.ViewStoreActionsBinding;
import com.github.yasevich.secrets.store.FingerprintStore;
import com.github.yasevich.secrets.store.Store;

import javax.crypto.Cipher;

@TargetApi(Build.VERSION_CODES.M)
public final class FingerprintStoreActivity extends StoreActivity {

    @NonNull
    private final Store store = new FingerprintStore();

    private FingerprintManagerCompat fingerprintManager;

    private ActivityFingerprintStoreBinding binding;

    @OperationMode
    private int operationMode = Cipher.ENCRYPT_MODE;

    public static void start(@NonNull Context context) {
        context.startActivity(new Intent(context, FingerprintStoreActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fingerprint_store);

        fingerprintManager = FingerprintManagerCompat.from(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        authenticate();
    }

    @NonNull
    @Override
    protected Store getStore() {
        return store;
    }

    @Override
    protected boolean isStoreActionsEnabled() {
        return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
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
        super.onOperation(mode);
        operationMode = mode == Cipher.ENCRYPT_MODE ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE;
    }

    private void authenticate() {
        fingerprintManager.authenticate(null, 0, null, new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationFailed() {
                log("authentication failed");
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                log("authentication error: " + errMsgId + ' ' + errString);
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                log("authentication help: " + helpMsgId + ' ' + helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                log("authentication succeeded");
                if (operationMode == Cipher.ENCRYPT_MODE) {
                    binding.storeActions.encrypt.callOnClick();
                } else {
                    binding.storeActions.decrypt.callOnClick();
                }
                authenticate();
            }
        }, null);
    }
}
