package com.yc.yfiotlock.libs.fingerprintcompat;

import android.os.CancellationSignal;

import androidx.annotation.NonNull;

public interface IBiometricPromptImpl {
    void authenticate(@NonNull CancellationSignal cancel);
}
