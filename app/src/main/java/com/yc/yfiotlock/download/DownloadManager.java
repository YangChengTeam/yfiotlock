package com.yc.yfiotlock.download;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.tencent.mmkv.MMKV;

import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;


public class DownloadManager {
    private static String parentDir;
    private static WeakReference<Context> mContext;
    private static DownloadListener4WithSpeed mDownloadListener;
    private static Map<String, DownloadTask> taskMap = new ArrayMap<>();


    public static String getParentDir() {
        return parentDir;
    }

    public static Context getContext() {
        if (mContext != null && mContext.get() != null) {
            return mContext.get();
        } else {
            return App.getApp().getApplicationContext();
        }
    }


    public static void setContext(WeakReference<Context> context) {
        mContext = context;
    }


    public static void init(WeakReference<Context> context) {
        if (!TextUtils.isEmpty(parentDir) || context == null) {
            return;
        }
        OkDownload.with();
        DownloadDispatcher.setMaxParallelRunningCount(3);
        parentDir = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    private static void redownload(DownloadTask task, UpdateInfo updateInfo) {
        String key = task.getId() + "redownload";
        int n = MMKV.defaultMMKV().getInt(key, 0);
        if (n < 10) {
            n++;
            MMKV.defaultMMKV().putInt(key, n);
            LogUtil.msg("redownload times" + n);
            VUiKit.postDelayed((n + 200) * n, () -> {
                task.enqueue(mDownloadListener);
            });
        } else {
            MMKV.defaultMMKV().putInt(key, 0);
            updateInfo.setDownloading(false);
        }
    }


    public static long getFileSize(File file) {
        if (file.exists()) {
            return file.length();
        }
        return 0;
    }

    private static int requestPermissionCount = 0;

    public static final String TAG = "DownloadManager";

    private static boolean checkDownLoadUrlCorrect(String url) {
        return !TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://")) && url.endsWith(".apk");
    }

    private static void saveDownloadCache(UpdateInfo updateInfo) {
        CacheUtil.setCache("downloadUpdateApkCache" + Build.VERSION.SDK_INT, updateInfo);
    }

    public static UpdateInfo getUpdateInfo() {
        return CacheUtil.getCache("downloadUpdateApkCache" + Build.VERSION.SDK_INT, UpdateInfo.class);
    }

    public static void updateApp(UpdateInfo updateInfo) {
        requestPermissionCount++;
        BaseActivity baseActivity = (BaseActivity) CommonUtil.findActivity(getContext());
        if (baseActivity != null && baseActivity instanceof BaseActivity) {
            baseActivity.getPermissionHelper().setMustPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
            baseActivity.getPermissionHelper().checkAndRequestPermission(baseActivity, new PermissionHelper.OnRequestPermissionsCallback() {
                @Override
                public void onRequestPermissionSuccess() {
                    if (!checkDownLoadUrlCorrect(updateInfo.getDownUrl())) {
                        ToastCompat.showCenter(getContext(), updateInfo.getDownUrl() + "下载地址有误，请联系客服");
                        return;
                    }
                    DownloadManager.init(new WeakReference<>(baseActivity));

                    DownloadTask task = new DownloadTask.Builder(updateInfo.getDownUrl(), new File(parentDir))
                            .setConnectionCount(1)
                            .setFilename(getUpdateFileName(updateInfo))
                            .setMinIntervalMillisCallbackProcess(500)
                            .setPassIfAlreadyCompleted(true)
                            .setPreAllocateLength(false)
                            .build();
                    task.setTag(updateInfo);

                    mDownloadListener = new DownloadListener4WithSpeed() {
                        @Override
                        public void taskStart(@NonNull DownloadTask task) {
                            Log.i("DownloadManager", "taskStart: ");
                        }

                        @Override
                        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
                            Log.i(TAG, "connectStart: ");
                        }

                        @Override
                        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                            Log.i(TAG, "connectEnd: ");
                        }

                        @Override
                        public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {

                            if (task.getTag() instanceof UpdateInfo) {
                                UpdateInfo updateInfo = (UpdateInfo) task.getTag();
                                updateInfo.setTotalSize(info.getTotalLength());
                                updateInfo.setOffsetSize(info.getTotalOffset());
                                updateInfo.setDownloading(true);
                                Log.i(TAG, "infoReady: " + updateInfo.getProgress());
                                EventBus.getDefault().post(updateInfo);
                            }
                        }

                        @Override
                        public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

                        }

                        @Override
                        public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                            Log.i(TAG, "progress: " + currentOffset);
                            if (task.getTag() instanceof UpdateInfo) {
                                UpdateInfo updateInfo = (UpdateInfo) task.getTag();
                                updateInfo.setSpeed(taskSpeed.speed());
                                updateInfo.setOffsetSize(currentOffset);
                                updateInfo.setDownloading(true);
                                EventBus.getDefault().post(updateInfo);
                                saveDownloadCache(updateInfo);
                            }
                        }

                        @Override
                        public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {
                            Log.i(TAG, "blockEnd: ");
                        }

                        @Override
                        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                            Log.i(TAG, "taskEnd: " + cause);
                            UpdateInfo updateInfo = (UpdateInfo) task.getTag();
                            if (cause == EndCause.COMPLETED) {
                                saveDownloadCache(updateInfo);
                                File file = new File(parentDir, getUpdateFileName(updateInfo));
                                long offsetSize = getFileSize(file);
                                if (offsetSize != 0) {
                                    String packageName = DownloadUtils.getPackageNameByFile(getContext(), file);
                                    if (TextUtils.isEmpty(packageName)) {
                                        if (file.delete()) {
                                            updateInfo.setDownloading(true);
                                        }
                                        redownload(task, updateInfo);
                                        return;
                                    }
                                    updateInfo.setTotalSize(offsetSize);
                                    updateInfo.setOffsetSize(offsetSize);
                                    installSelf(updateInfo);
                                    updateInfo.setDownloading(false);
                                    EventBus.getDefault().post(updateInfo);
                                    saveDownloadCache(updateInfo);
                                }
                            } else if (cause == EndCause.ERROR) {
                                ToastCompat.show(getContext(), "更新失败" + realCause);
                            }
                        }
                    };

                    task.enqueue(mDownloadListener);

                }

                @Override
                public void onRequestPermissionError() {
                    if (requestPermissionCount >= 3) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("提示")
                                .setMessage("请授予存储权限，否则无法下载更新文件。若无权限申请弹窗，请手动到设置-应用-找到乐乐游戏-选择权限-打开存储权限")
                                .setPositiveButton("我知道了", (dialog, which) -> {
                                    try {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                        intent.setData(uri);
                                        getContext().startActivity(intent);
                                        ToastCompat.show(getContext(), "请授予存储权限");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                }).create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xffff9b27);
                        return;
                    }
                    ToastCompat.show(getContext(), "请授予存储权限");
                    updateApp(updateInfo);

                }

            });
        }
    }

    public static void installSelf(UpdateInfo updateInfo) {
        File file = new File(parentDir, getUpdateFileName(updateInfo));
        String packageName = DownloadUtils.getPackageNameByFile(getContext(), file);
        if (TextUtils.isEmpty(packageName)) {
            if (file.delete()) {
                updateInfo.setDownloading(true);
                ToastCompat.show(getContext(), getContext().getResources().getString(R.string.download_again_tip));
            }
            updateApp(updateInfo);
        } else {
            DownloadUtils.installApp(getContext(), file);
        }
    }

    private static String getUpdateFileName(UpdateInfo updateInfo) {
        return getContext().getResources().getString(R.string.app_name) + updateInfo.getVersionCode() + ".apk";
    }

}
