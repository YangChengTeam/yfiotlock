package com.yc.yfiotlock.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.net.entry.Response;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.ScreenUtil;
import com.kk.utils.ToastUtil;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.user.LoginActivity;
import com.yc.yfiotlock.controller.activitys.user.MainActivity;
import com.yc.yfiotlock.model.bean.user.PhoneTokenInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.LoginEngin;
import com.yc.yfiotlock.view.widgets.MyItemDivider;

import org.greenrobot.eventbus.EventBus;

import rx.Observer;

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

    public static void startLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
        PhoneNumberAuthHelper phoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(context, new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                Log.i("aaaa", "onTokenSuccess: " + s);
                try {
                    PhoneTokenInfo tokenInfo = JSONObject.parseObject(s, PhoneTokenInfo.class);
                    switch (tokenInfo.getCode()) {
                        case "600024"://"终端支持认证"
                            startVerify(context, this);
                            break;
                        case "600001"://"唤起授权页成功"
                            EventBus.getDefault().post(tokenInfo);
                            break;
                        case "600000":
                            startLoginWithToken(context, this, tokenInfo.getToken());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.onTokenFailed("");
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.i("aaaa", "onTokenFailed: " + s);
                PhoneTokenInfo phoneTokenInfo = new PhoneTokenInfo();
                phoneTokenInfo.setCode("600001");
                EventBus.getDefault().post(phoneTokenInfo);
            }
        });

        phoneNumberAuthHelper.setAuthSDKInfo(Config.ALI_PHONE_SDK_APPID);
        phoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        phoneNumberAuthHelper.checkEnvAvailable(2);
    }

    public static void isVerifyEnable(Context context) {
        PhoneNumberAuthHelper phoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(context, new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                Log.i("aaaa", "onTokenSuccess: " + s);
                try {
                    PhoneTokenInfo tokenInfo = JSONObject.parseObject(s, PhoneTokenInfo.class);
                    switch (tokenInfo.getCode()) {
                        case "600024"://"终端支持认证"
                            startVerify(context, this);
                            break;
                        case "600001"://"唤起授权页成功"
                            break;
                        case "600000":
                            startLoginWithToken(context, this, tokenInfo.getToken());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.onTokenFailed("");
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.i("aaaa", "onTokenFailed: " + s);
                Response response = new Response();
                response.body = s;
                PhoneTokenInfo tokenInfo = JSONObject.parseObject(s, PhoneTokenInfo.class);
                showFailTip(context, tokenInfo.getCode());
                PhoneNumberAuthHelper.getInstance(context, this).hideLoginLoading();
            }
        });

        phoneNumberAuthHelper.setAuthSDKInfo(Config.ALI_PHONE_SDK_APPID);
        phoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        phoneNumberAuthHelper.checkEnvAvailable(2);
    }

    private static void startLoginWithToken(Context context, TokenResultListener listener, String token) {
        LoginEngin engin = new LoginEngin(context);
        engin.aliFastLogin(token).subscribe(new Observer<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                PhoneNumberAuthHelper.getInstance(context, listener).hideLoginLoading();
                PhoneNumberAuthHelper.getInstance(context, listener).quitLoginPage();
                PhoneNumberAuthHelper.getInstance(context, listener).setAuthListener(null);
            }

            @Override
            public void onError(Throwable e) {
                PhoneNumberAuthHelper.getInstance(context, listener).hideLoginLoading();
                ToastCompat.show(context,e+"");
                Log.i("aaaa", "onError: "+e);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> info) {
                Log.i("aaaa", "onNext: "+info);
                if (info != null && info.getCode() == 1 && info.getData() != null) {
                    UserInfoCache.setUserInfo(info.getData());
                    EventBus.getDefault().post(info.getData());
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    ToastCompat.show(context, info == null ? "登陆失败失败" : info.getMsg());
                }
            }
        });

    }


    private static void showFailTip(Context context, String code) {
        switch (code) {
            case "600002":
                ToastCompat.show(context, "唤起授权页失败,请尝试其他登录方式");
                break;
            case "600004":
                ToastCompat.show(context, "获取运营商配置信息失败,请尝试其他登录方式");
                break;
            case "600005":
                ToastCompat.show(context, "手机终端不安全,请尝试其他登录方式");
                break;
            case "600007":
                ToastCompat.show(context, "未检测到SIM卡,请检查SIM卡后重试");
                break;
            case "600008":
                ToastCompat.show(context, "移动网络未开启,请开启移动网络后重试");
                break;
            case "700000"://点击返回，⽤户取消免密登录
            case "700001"://点击切换按钮，⽤户取消免密登录
            case "700002"://点击登录按钮事件
            case "700003"://点击check box事件
            case "700004"://点击协议富文本文字事件
                break;
            default:
                ToastCompat.show(context, "一键登录失败,请尝试其他登录方式，错误代码：" + code);
                break;
        }
    }

    private static void startVerify(Context context, TokenResultListener tokenResultListener) {
        PhoneNumberAuthHelper helper = PhoneNumberAuthHelper.getInstance(context, tokenResultListener);
        AuthUIConfig authUIConfig = new AuthUIConfig.Builder()
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
                .setSloganText("")
                .setNavText("")
                .setNavReturnImgPath("icon_back_white")
                .setLogoImgPath("logo")
                .setStatusBarColor(0xff000000)
                .setLogBtnBackgroundPath("fast_login_btn_bg")
                .setPageBackgroundPath("login_bg")
                .setNavColor(Color.TRANSPARENT)
                .setNavReturnHidden(true)
                .setNavReturnImgWidth(27 / 3)
                .setNavReturnImgHeight(51 / 3)
                .setNavReturnScaleType(ImageView.ScaleType.FIT_CENTER)
                .setLogoWidth(402 / 3)
                .setLogoHeight(117 / 3)
                .setLogoScaleType(ImageView.ScaleType.FIT_CENTER)
                .setNumberSize(25)
                .setNumberColor(Color.WHITE)
                .setLogBtnText("本机号码一键登录")
                .setLogBtnWidth(274)
                .setLogBtnHeight(42)
                .setLogBtnTextSize(15)
                .setSwitchAccText("其他手机号码登陆")
                .setSwitchAccTextColor(Color.WHITE)
                .setSwitchAccTextSize(15)
                .setAppPrivacyOne("《用户协议》", Config.PRIVACY_POLICY)
                .setAppPrivacyTwo("《隐私政策》", Config.USER_AGREEMENT)
                .setPrivacyBefore("登陆代表同意")
                .setCheckboxHidden(true)
                .create();
        helper.setAuthUIConfig(authUIConfig);
        helper.getLoginToken(context, 3000);
    }

    public static UpdateInfo getNeedUpgradeInfo(UpdateInfo updateInfo) {

        if (updateInfo == null) {
            return null;
        }

        PackageInfo packageInfo = GoagalInfo.get().getPackageInfo();
        int versionCode = 0;
        if (packageInfo != null) {
            versionCode = packageInfo.versionCode;
        }

        if (versionCode != 0 && updateInfo.getVersionCode() > versionCode) {
            return updateInfo;
        }

        return null;
    }

}
