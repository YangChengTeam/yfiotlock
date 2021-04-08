package com.yc.yfiotlock.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewKt;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
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
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.user.PhoneTokenInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.LoginEngin;
import com.yc.yfiotlock.model.bean.user.LoginEvent;
import com.yc.yfiotlock.view.widgets.MyItemDivider;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rx.Observer;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 *
 */
/*
 * Created by　Dullyoung on 2021/3/3
 */
public class CommonUtil {

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

    public static void startBrowser(Context context, String url) {
        LogUtil.msg("startBrowser: url " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://".concat(url);
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));
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
    }


    public static void startFastLogin(Context context) {
        LoginEvent loginEvent = new LoginEvent();
        PhoneNumberAuthHelper phoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(context, new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                Log.d("onekeylogin", "onTokenSuccess: " + s);
                try {
                    PhoneTokenInfo tokenInfo = JSONObject.parseObject(s, PhoneTokenInfo.class);
                    switch (tokenInfo.getCode()) {
                        case "600024"://"终端支持认证"
                            loginEvent.setStateString(LoginEvent.State.WAITING);
                            EventBus.getDefault().post(loginEvent);
                            startVerify(context, this);
                            break;
                        case "600001"://"唤起授权页成功"
                            loginEvent.setStateString(LoginEvent.State.EVOKE_SUCCESS);
                            EventBus.getDefault().post(loginEvent);
                            break;
                        case "600000"://授权成功
                            startLoginWithToken(context, this, tokenInfo.getToken());
                            break;
                        default:
                            this.onTokenFailed(s);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    this.onTokenFailed(s);
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.i("onekeylogin", "onTokenFailed: " + s);
                loginEvent.setStateString(LoginEvent.State.FAILED);
                EventBus.getDefault().post(loginEvent);
                PhoneTokenInfo tokenInfo = null;
                try {
                    tokenInfo = JSONObject.parseObject(s, PhoneTokenInfo.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showFailTip(context, tokenInfo == null ? "000001" : tokenInfo.getCode());
                PhoneNumberAuthHelper.getInstance(context, this).hideLoginLoading();
            }
        });

        phoneNumberAuthHelper.setAuthSDKInfo(Config.ALI_PHONE_SDK_APPID);
        //DEBUG mode phoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        phoneNumberAuthHelper.checkEnvAvailable(2);
        loginEvent.setStateString(LoginEvent.State.CHECKING);
        EventBus.getDefault().post(loginEvent);
    }

    /**
     * @param editText      要操作的文本框
     * @param limit         输入字符限制
     * @param doubleChinese 汉字是否占两个字符
     */
    public static void setEditTextLimit(EditText editText, int limit, boolean doubleChinese) {
        if (!doubleChinese) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(limit)});
            return;
        }
        final int maxLen = limit;
        InputFilter filter = (src, start, end, dest, dstart, dend) -> {
            int dindex = 0;
            int count = 0;

            while (count <= maxLen && dindex < dest.length()) {
                char c = dest.charAt(dindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLen) {
                return dest.subSequence(0, dindex - 1);
            }

            int sindex = 0;
            while (count <= maxLen && sindex < src.length()) {
                char c = src.charAt(sindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLen) {
                sindex--;
            }

            return src.subSequence(0, sindex);
        };

        editText.setFilters(new InputFilter[]{filter});

    }

    /**
     * 查看、隐藏密码
     *
     * @param v 要操作的editText
     */
    public static void hiddenEditText(EditText v, ImageView imageView) {
        //可见的时候设回密码不可见状态
        if (v.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            v.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setImageResource(R.mipmap.see);
        } else {//不可见的时候设置为可见
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageView.setImageResource(R.mipmap.secret);
        }
        v.setSelection(v.getText().length());
    }

    private static void startLoginWithToken(Context context, TokenResultListener listener, String token) {
        LoginEngin engin = new LoginEngin(context);
        engin.aliFastLogin(token).subscribe(new Observer<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                PhoneNumberAuthHelper.getInstance(context, listener).hideLoginLoading();
                ToastCompat.show(context, e + "");
                Log.i("onekeylogin", "onError: " + e);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> info) {
                Log.i("onekeylogin", "onNext: " + info);
                if (info != null && info.getCode() == 1 && info.getData() != null) {
                    UserInfoCache.setUserInfo(info.getData());

                    VUiKit.postDelayed(200, () -> {
                        context.startActivity(new Intent(context, MainActivity.class));
                        //hide loading
                        PhoneNumberAuthHelper.getInstance(context, listener).hideLoginLoading();
                        //close one-key login page
                        PhoneNumberAuthHelper.getInstance(context, listener).quitLoginPage();
                        //avoid memory leak
                        PhoneNumberAuthHelper.getInstance(context, listener).setAuthListener(null);
                        EventBus.getDefault().post(info.getData());
                    });
                } else {
                    ToastCompat.show(context, info == null ? "登录失败" : info.getMsg());
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
            case "600009":
                ToastCompat.show(context, "无法判断运营商");
                break;
            case "600013":
                ToastCompat.show(context, "60013 运营商维护升级，该功能不可用");
                break;
            case "600014":
                ToastCompat.show(context, "60014 运营商维护升级，该功能不可用");
                break;
            case "600015":
                ToastCompat.show(context, "请求超时");
                break;
            case "600025":
                ToastCompat.show(context, "终端检测参数错误");
                break;
            //点击返回，⽤户取消免密登录
            case "700000":
                //点击切换按钮，⽤户取消免密登录
            case "700001":
                //点击登录按钮事件
            case "700002":
                //点击check box事件
            case "700003":
                //点击协议富文本文字事件
            case "700004":
                break;
            default:
                ToastCompat.show(context, "一键登录失败,请尝试其他登录方式，错误代码：" + code);
                break;
        }
    }

    private static void startVerify(Context context, TokenResultListener tokenResultListener) {
        PhoneNumberAuthHelper helper = PhoneNumberAuthHelper.getInstance(context, tokenResultListener);
        AuthUIConfig uiConfig = new AuthUIConfig.Builder()
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
                .setSwitchAccText("其他手机号码登录")
                .setSwitchAccTextColor(Color.WHITE)
                .setSwitchAccTextSize(15)
                .setAppPrivacyOne("《用户协议》", Config.PRIVACY_POLICY)
                .setAppPrivacyTwo("《隐私政策》", Config.USER_AGREEMENT)
                .setPrivacyBefore("登录代表同意")
                .setCheckboxHidden(true)
                .create();
        helper.setAuthUIConfig(uiConfig);
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

    public static String getSsid(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return "";
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid ="";
        if (info.getSupplicantState().equals(SupplicantState.COMPLETED)){
            ssid=info.getSSID();
            if (!TextUtils.isEmpty(ssid)) {
                ssid = ssid.replace("\"", "");
            }
        }
        return ssid;
    }

    /**
     * 2.4 GHz band frequency of first channel in MHz
     */
    public static final int BAND_24_GHZ_START_FREQ_MHZ = 2412;
    /**
     * 2.4 GHz band frequency of last channel in MHz
     */
    public static final int BAND_24_GHZ_END_FREQ_MHZ = 2484;

    /**
     * @param freqMhz WiFi频率
     * @return 是否是2.4G的WiFi
     */
    public static boolean is24GHz(int freqMhz) {
        return freqMhz >= BAND_24_GHZ_START_FREQ_MHZ && freqMhz <= BAND_24_GHZ_END_FREQ_MHZ;
    }

    public static String formatTime(int timestamp) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(timestamp * 1000L);
        return DateFormat.format("yyyy-MM-dd hh:mm:ss", cal).toString();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
