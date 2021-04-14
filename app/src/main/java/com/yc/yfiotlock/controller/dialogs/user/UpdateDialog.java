package com.yc.yfiotlock.controller.dialogs.user;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.dialogs.BaseDialog;
import com.yc.yfiotlock.download.AppDownloadManager;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public class UpdateDialog extends BaseDialog {
    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.iv_cancel)
    ImageView mIvCancel;
    @BindView(R.id.tv_new_version)
    TextView mTvNewVersion;
    @BindView(R.id.stv_version)
    SuperTextView mStvVersion;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.sv_contont)
    ScrollView mSvContont;
    @BindView(R.id.pb_process)
    ProgressBar mPbProcess;
    @BindView(R.id.tv_update)
    TextView mTvUpdate;

    public UpdateDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_dialog_update;
    }

    @Override
    protected void initViews() {

    }

    public void show(UpdateInfo updateInfo) {
        mUpdateInfo = updateInfo;
        mTvContent.setText(updateInfo.getDesc());
        mTvUpdate.setText("立即更新");
        mStvVersion.setText("v".concat(updateInfo.getVersion()));
        setProcess(AppDownloadManager.getInstance().getUpdateInfo());
        isMust = (updateInfo.getIsMust() == 1);
        mIvCancel.setVisibility(isMust ? View.GONE : View.VISIBLE);
        setCanceledOnTouchOutside(isMust);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        show();
    }

    @Override
    public void onBackPressed() {
        if (!isMust) {
            super.onBackPressed();
        }
    }

    private boolean isMust = false;
    private UpdateInfo mUpdateInfo;

    @Override
    public void bindClick() {
        setClick(R.id.iv_cancel, this::dismiss);
        setClick(R.id.tv_update, () -> AppDownloadManager.getInstance().updateApp(mUpdateInfo));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(UpdateInfo updateInfo) {
        setProcess(updateInfo);
    }

    private void setProcess(UpdateInfo updateInfo) {
        if (updateInfo == null) {
            return;
        }
        if (mPbProcess != null) {
            mPbProcess.setProgress(updateInfo.getProgress());
            mTvUpdate.setClickable(updateInfo.getProgress() == 100);
            String text = updateInfo.getProgress() == 100 ? "安装" : updateInfo.getProgress() + "%";
            mTvUpdate.setText(text);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        AppDownloadManager.getInstance().stopTask();
    }
}
