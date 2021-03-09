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
import com.yc.yfiotlock.download.DownloadManager;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

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
        show();
    }

    private UpdateInfo mUpdateInfo;

    @OnClick({R.id.iv_cancel, R.id.tv_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                dismiss();
                break;
            case R.id.tv_update:
                DownloadManager.updateApp(mUpdateInfo);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(UpdateInfo updateInfo) {
        if (mPbProcess != null) {
            mPbProcess.setProgress(updateInfo.getProgress());
            mTvUpdate.setClickable(updateInfo.getProgress() == 100);
            String text = updateInfo.getProgress() == 100 ? "安装" : updateInfo.getProgress() + "%";
            mTvUpdate.setText(text);
        }
    }
}
