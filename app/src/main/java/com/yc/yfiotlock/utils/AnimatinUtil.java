package com.yc.yfiotlock.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.kk.securityhttp.utils.VUiKit;

public class AnimatinUtil {
    public static void rotate(View view) {
        VUiKit.post(() -> {
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(5000);
            rotate.setRepeatCount(Animation.INFINITE);
            rotate.setInterpolator(new AccelerateDecelerateInterpolator());
            view.startAnimation(rotate);
        });
    }

    public static void scale(View view, float value) {
        VUiKit.post(() -> {
            ScaleAnimation scale = new ScaleAnimation(1f, 1.f + value, 1f, 1.f + value, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
            scale.setDuration(3000);
            scale.setFillAfter(false);
            scale.setRepeatMode(ScaleAnimation.REVERSE);
            scale.setRepeatCount(ScaleAnimation.INFINITE);
            view.startAnimation(scale);
        });
    }

    public static void heightZero(View view) {
        VUiKit.post(() -> {
            ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = val;
                    view.setLayoutParams(layoutParams);
                }
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
            anim.setDuration(300);
            anim.start();
        });
    }
}
