package com.yc.yfiotlock.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.ScreenUtil;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.view.widgets.MyItemDivider;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/*
 * Created by　Dullyoung on 2021/3/3
 */
public class CommonUtils {

    public static boolean isActivityDestory(Context context) {
        Activity activity = findActivity(context);
        return activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
    }

    public static Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    public static void setItemDivider(Context context, RecyclerView recyclerView) {
        MyItemDivider myItemDivider = new MyItemDivider(context, DividerItemDecoration.VERTICAL)
                .setPadding(ScreenUtil.dip2px(context, 15))
                .setDrawable(ContextCompat.getDrawable(context, R.drawable.shape_line_divide))
                .setCountNotDraw(1);
        recyclerView.addItemDecoration(myItemDivider);
    }

    public static void setItemDividerFull(Context context, RecyclerView recyclerView) {
        MyItemDivider myItemDivider = new MyItemDivider(context, DividerItemDecoration.VERTICAL)
                .setDrawable(ContextCompat.getDrawable(context, R.drawable.shape_line_divide));
        recyclerView.addItemDecoration(myItemDivider);
    }

    public static void setItemDivider2(Context context, RecyclerView recyclerView, int notDrawCount) {
        MyItemDivider myItemDivider = new MyItemDivider(context, DividerItemDecoration.VERTICAL)
                .setPadding(ScreenUtil.dip2px(context, 15))
                .setDrawable(ContextCompat.getDrawable(context, R.drawable.shape_line_divide))
                .setHeadNotDraw(notDrawCount)
                .setCountNotDraw(1);
        recyclerView.addItemDecoration(myItemDivider);
    }

    public static void setItemDividerWithNoPadding(Context context, RecyclerView recyclerView) {
        MyItemDivider myItemDivider = new MyItemDivider(context, DividerItemDecoration.VERTICAL)
                .setDrawable(ContextCompat.getDrawable(context, R.drawable.shape_line_divide))
                .setCountNotDraw(1);
        recyclerView.addItemDecoration(myItemDivider);
    }

    public static void startBrowser(Context context, String url) {
        LogUtil.msg("startBrowser: url " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastCompat.show(context, "未能打开链接");
        }
    }

    public static void copy(Context context, String text) {
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText(null, text);
        mClipboardManager.setPrimaryClip(mClipData);
    }

    public static void copyWithToast(Context context, String copyText, String toastText) {
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText(null, copyText);
        mClipboardManager.setPrimaryClip(mClipData);

        ToastCompat.showCenter(context, toastText);
    }

    public static void joinQQGroup(Context context, String groupNumber) {
        String uri = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + groupNumber + "&card_type=group&source=qrcode";
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            ToastUtil.toast2(context, "未安装手Q或安装的版本不支持");
            // 未安装手Q或安装的版本不支持
        }
    }


}
