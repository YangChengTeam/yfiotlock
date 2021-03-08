package com.yc.yfiotlock.download;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.PathUtil;
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
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.UpdateInfo;
import com.yc.yfiotlock.utils.CommonUtils;

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
            return App.getApp().getBaseContext();
        }
    }


    public static void setContext(WeakReference<Context> context) {
        mContext = context;
    }


    public static void init(WeakReference<Context> context) {
        if (!TextUtils.isEmpty(parentDir) || context == null) return;
        OkDownload.with();
        DownloadDispatcher.setMaxParallelRunningCount(3);
        parentDir = PathUtil.createDir(context.get(), "/apks");
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

    public static void updateApp(UpdateInfo upgradeInfo) {
        requestPermissionCount++;
        BaseActivity baseActivity = (BaseActivity) CommonUtils.findActivity(getContext());
        if (baseActivity != null && baseActivity instanceof BaseActivity) {
            baseActivity.getPermissionHelper().setMustPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
            baseActivity.getPermissionHelper().checkAndRequestPermission(baseActivity, new PermissionHelper.OnRequestPermissionsCallback() {
                @Override
                public void onRequestPermissionSuccess() {
                    DownloadManager.init(new WeakReference<>(baseActivity));

                    DownloadTask task = new DownloadTask.Builder(upgradeInfo.getDownUrl(), new File(parentDir))
                            .setConnectionCount(1)
                            .setFilename(getUpdateFileName(upgradeInfo))
                            .setMinIntervalMillisCallbackProcess(500)
                            .setPassIfAlreadyCompleted(true)
                            .setPreAllocateLength(false)
                            .build();
                    task.setTag(upgradeInfo);

                    mDownloadListener = new DownloadListener4WithSpeed() {
                        @Override
                        public void taskStart(@NonNull DownloadTask task) {

                        }

                        @Override
                        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

                        }

                        @Override
                        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

                        }

                        @Override
                        public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
                            if (task.getTag() instanceof UpdateInfo) {
                                UpdateInfo upgradeInfo = (UpdateInfo) task.getTag();
                                upgradeInfo.setTotalSize(info.getTotalLength());
                                upgradeInfo.setOffsetSize(info.getTotalOffset());
                                upgradeInfo.setDownloading(true);
                                EventBus.getDefault().post(upgradeInfo);
                            }
                        }

                        @Override
                        public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

                        }

                        @Override
                        public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                            if (task.getTag() instanceof UpdateInfo) {
                                UpdateInfo upgradeInfo = (UpdateInfo) task.getTag();
                                upgradeInfo.setSpeed(taskSpeed.speed());
                                upgradeInfo.setOffsetSize(currentOffset);
                                upgradeInfo.setDownloading(true);
                                EventBus.getDefault().post(upgradeInfo);
                            }
                        }

                        @Override
                        public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

                        }

                        @Override
                        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                            UpdateInfo updateInfo = (UpdateInfo) task.getTag();
                            if (cause == EndCause.COMPLETED) {
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
                                    installSelf(updateInfo);
                                    if (task.getTag() instanceof UpdateInfo) {
                                        UpdateInfo upgradeInfo = (UpdateInfo) task.getTag();
                                        upgradeInfo.setDownloading(false);
                                        EventBus.getDefault().post(upgradeInfo);
                                    }
                                }
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
                    updateApp(upgradeInfo);

                }

            });
        }
    }

    public static void installSelf(UpdateInfo upgradeInfo) {
        File file = new File(parentDir, getUpdateFileName(upgradeInfo));
        String packageName = DownloadUtils.getPackageNameByFile(getContext(), file);
        if (TextUtils.isEmpty(packageName)) {
            if (file.delete()) {
                upgradeInfo.setDownloading(true);
                ToastCompat.show(getContext(), getContext().getResources().getString(R.string.download_again_tip));
            }
            updateApp(upgradeInfo);
        } else {
            DownloadUtils.installApp(getContext(), file);
        }
    }

    private static String getUpdateFileName(UpdateInfo updateInfo) {
        return getContext().getResources().getString(R.string.app_name) + updateInfo.getVersionCode() + ".apk";
    }

}
