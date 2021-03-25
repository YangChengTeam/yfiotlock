package com.yc.yfiotlock.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntRange;

import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.demo.MainActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fingerprintcompat.AonFingerChangeCallback;
import com.yc.yfiotlock.libs.fingerprintcompat.FingerManager;
import com.yc.yfiotlock.libs.fingerprintcompat.SimpleFingerCheckCallback;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/22
 * 设备的安全密码设置界面工具
 **/
public class SafeUtils {
    /**
     * 无密
     */
    public static final int NO_PASSWORD = 0;
    /**
     * 密码
     */
    public static final int PASSWORD_TYPE = 1;
    /**
     * 指纹
     */
    public static final int FINGERPRINT_TYPE = 2;

    /**
     * {@link com.yc.yfiotlock.model.bean.lock.DeviceSafeSettingInfo }
     * private final static int PASSWORD_TYPE = 1;     验证密码
     * private final static int FINGERPRINT_TYPE = 2;  验证指纹
     *
     * @param deviceInfo 设备信息
     * @param type       安全密码设置类型  0无密
     */
    public static void setSafePwdType(DeviceInfo deviceInfo, @IntRange(from = 0, to = 2) int type) {
        MMKV.defaultMMKV().putInt("safeType" + deviceInfo.getMacAddress(), type);
    }

    /**
     * @param deviceInfo 设备信息
     * @return private final static int PASSWORD_TYPE = 1;     验证密码
     * private final static int FINGERPRINT_TYPE = 2;  验证指纹
     * 0,无密码
     */
    public static int getSafePwdType(DeviceInfo deviceInfo) {
        return MMKV.defaultMMKV().getInt("safeType" + deviceInfo.getMacAddress(), 0);
    }

    /**
     * @param deviceInfo 设备信息
     * @return 返回用户设置的安全密码
     */
    public static String getSafePwd(DeviceInfo deviceInfo) {
        return MMKV.defaultMMKV().getString("safePwd" + deviceInfo.getMacAddress(), "default");
    }

    /**
     * 密码默认值
     */
    public static final String DEFAULT = "default";

    /**
     * @param deviceInfo 设备信息
     * @param pwd        保存安全密码
     */
    public static void setSafePwd(DeviceInfo deviceInfo, String pwd) {
        MMKV.defaultMMKV().putString("safePwd" + deviceInfo.getMacAddress(), pwd);
    }

    public static void useFinger(Activity context, Callback<String> stringCallback) {
        useFinger(context, stringCallback, "");
    }

    public static void useFinger(Activity context, Callback<String> stringCallback, String laseFailReason) {
        switch (FingerManager.checkSupport(context)) {
            case DEVICE_UNSUPPORTED:
                stringCallback.onFailure(new Response());
                ToastCompat.show(context, "您的设备不支持指纹");
                break;
            case SUPPORT_WITHOUT_DATA:
                stringCallback.onFailure(new Response());
                ToastCompat.show(context, "请在系统录入指纹后再验证");
                break;
            case SUPPORT:
                FingerManager.build().setApplication(App.getApp())
                        .setTitle("指纹验证")
                        .setDes("请按下指纹")
                        .setSubTitle(laseFailReason)
                        .setNegativeText("取消")
                        .setFingerCheckCallback(new SimpleFingerCheckCallback() {

                            @Override
                            public void onSucceed() {
                                stringCallback.onSuccess("验证成功");
                            }

                            @Override
                            public void onError(int code, String error) {
                                SafeUtils.showFailTip(context, code);

                                /**
                                 * 只要不是  7 9 10 就重试
                                 *
                                 * 7 {@link android.hardware.biometrics.BiometricPrompt#BIOMETRIC_ERROR_LOCKOUT}
                                 * 该操作被取消，因为该API由于尝试过多而被锁定。尝试5次失败后会发生这种情况，持续30秒。
                                 * 9 {@link android.hardware.biometrics.BiometricPrompt#BIOMETRIC_ERROR_LOCKOUT_PERMANENT}
                                 * 该操作被取消，因为BIOMETRIC_ERROR_LOCKOUT发生了太多次。禁用生物特征认证，直到用户通过强认证（PIN /图案/密码）解锁
                                 * 5是代码取消验证  不会走onError 而是走onCancel
                                 * 10是用户取消验证
                                 */
                                if (code != BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT
                                        && code != BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT
                                        && code != BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED) {
                                    useFinger(context, stringCallback);
                                }

                            }

                            @Override
                            public void onError(String error) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        })
                        .setFingerChangeCallback(new AonFingerChangeCallback() {

                            @Override
                            protected void onFingerDataChange() {
                                ToastCompat.show(context, "指纹数据发生了变化");
                            }
                        })
                        .create()
                        .startListener(context);
                break;
            default:
                break;
        }
    }


    public static void showFailTip(Context context, int code) {
        switch (code) {
            case BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT:
                ToastCompat.show(context, "失败次数过多，请稍后再试");
                break;
            case BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT:
                ToastCompat.show(context, "失败次数过多，指纹识别已被禁用，重新解锁手机后启用。");
                break;
            default:
                break;

        }

    }

}
