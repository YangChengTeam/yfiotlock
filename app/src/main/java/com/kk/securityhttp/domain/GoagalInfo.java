package com.kk.securityhttp.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kk.securityhttp.utils.FileUtil;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.PathUtil;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by zhangkai on 16/9/19.
 */
public class GoagalInfo {

    public String publicKey;

    public ChannelInfo channelInfo = null;
    private PackageInfo packageInfo = null;


    public String uuid = "";
    public String channel = "default";

    public String configPath = "";

    public Object extra;

    private static GoagalInfo goagalInfo = new GoagalInfo();

    public static GoagalInfo get() {
        return goagalInfo;
    }

    public void init(Context context) {
        configPath = PathUtil.getConfigPath(context);

        String result1 = null;
        String result2 = null;
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zf = null;
        try {
            zf = new ZipFile(sourceDir);
            result1 = FileUtil.readString(context.getAssets().open("gamechannel.json"));
            if (TextUtils.isEmpty(result1)) {
                ZipEntry ze1 = zf.getEntry("META-INF/gamechannel.json");
                InputStream in1 = zf.getInputStream(ze1);
                result1 = FileUtil.readString(in1);
            }
            LogUtil.msg("渠道->" + result1);

            ZipEntry ze2 = zf.getEntry("META-INF/rsa_public_key.pem");
            InputStream in2 = zf.getInputStream(ze2);
            result2 = FileUtil.readString(in2);
            LogUtil.msg("公钥->" + result2);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.msg("apk中gamechannel或rsa_public_key文件不存在", LogUtil.W);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
        }

        String name = gamechannelFilename;
        if (result1 != null) {
            FileUtil.writeInfoToFile(result1, configPath, name);
        } else {
            result1 = FileUtil.readInfoFromFile(configPath, name);
        }

        if (result1 != null) {
            channel = result1;
        }

        if (result2 != null) {
            publicKey = getPublicKey(result2);
        }

        name = rasPublickeylFilename;
        if (result2 != null) {
            publicKey = getPublicKey(result2);
            FileUtil.writeInfoToFile(result2, configPath, name);
        } else {
            result2 = FileUtil.readInfoFromFile(configPath, name);
            if (result2 != null) {
                publicKey = getPublicKey(result2);
            }
        }

        channelInfo = getChannelInfo();
        uuid = getUid(context);

    }

    private String rasPublickeylFilename = "rsa_public_key.pem";
    private String gamechannelFilename = "gamechannel.json";

    public GoagalInfo setRasPublickeylFilename(String rasPublickeylFilename) {
        this.rasPublickeylFilename = rasPublickeylFilename;
        return this;
    }

    public GoagalInfo setGamechannelFilename(String gamechannelFilename) {
        this.gamechannelFilename = gamechannelFilename;
        return this;
    }

    private ChannelInfo getChannelInfo() {
        try {
            return JSON.parseObject(channel, ChannelInfo.class);
        } catch (Exception e) {
            LogUtil.msg("渠道信息解析错误->" + e.getMessage());
        }
        return null;
    }

    private String getPublicKey(InputStream in) {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.startsWith("----")) {
                    continue;
                }
                result += mLine;
            }
        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                }
            }
        }
        return result;
    }

    public String getPublicKeyString() {
        publicKey = getPublicKey(publicKey);
        return publicKey;
    }

    public String getPublicKey(String key) {
        return key.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\r", "")
                .replace("\n", "");
    }

    @SuppressLint("MissingPermission")
    public String getUid(Context context) {
        String uid = MMKV.defaultMMKV().getString("uuid", "");
        if (TextUtils.isEmpty(uid)) {
            uid = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return uid;
    }

    public String getOaid() {
        return MMKV.defaultMMKV().getString("oaid", "");
    }

    public PackageInfo getPackageInfo() {
        PackageInfo pInfo = null;
        try {
            pInfo = App.getApp().getPackageManager().getPackageInfo(App.getApp().getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            pInfo = new PackageInfo();
            pInfo.versionCode = 1;
            pInfo.versionName = "1.0.0";
        }
        return pInfo;
    }
}
