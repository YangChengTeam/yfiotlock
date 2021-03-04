package com.yc.yfiotlock.model.engin;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.coorchice.library.ImageEngine;
import com.coorchice.library.image_engine.Engine;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class GlideEngine implements Engine {

    private Context context;

    public GlideEngine(Context context) {
        this.context = context;
    }

    @Override
    public void load(String url, final ImageEngine.Callback callback) {
        Glide.with(context).asDrawable().load(url).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                // 主要是通过callback返回Drawable对象给SuperTextView
                callback.onCompleted(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }

        });
    }
}
