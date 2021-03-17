package com.yc.yfiotlock.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class AnimatinUtil {
    public static void rotate(View view) {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(rotate);
    }

    public static void scale(View view, float value) {
        ScaleAnimation scale = new ScaleAnimation(1f, 1.f + value, 1f, 1.f + value, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(3000);
        scale.setFillAfter(false);
        scale.setRepeatMode(ScaleAnimation.REVERSE);
        scale.setRepeatCount(ScaleAnimation.INFINITE);
        view.startAnimation(scale);
    }
}
