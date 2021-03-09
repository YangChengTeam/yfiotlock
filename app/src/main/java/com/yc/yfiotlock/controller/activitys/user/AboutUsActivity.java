package com.yc.yfiotlock.controller.activitys.user;

import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.user.UpdateDialog;
import com.yc.yfiotlock.download.DownloadManager;
import com.yc.yfiotlock.model.bean.user.AboutInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.model.bean.user.UpgradeInfo;
import com.yc.yfiotlock.model.engin.UpdateEngine;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.view.adapters.AboutAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.ll_logo)
    LinearLayout mLlLogo;
    @BindView(R.id.rv_about)
    RecyclerView mRvAbout;
    @BindView(R.id.stv_check)
    SuperTextView mStvCheck;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_about_us;
    }

    @Override
    protected void initViews() {
        DownloadManager.setContext(new WeakReference<>(this));
        mBnbTitle.setBackListener(view -> finish());
        setRvAbout();
    }

    private AboutAdapter mAboutAdapter;

    private void setRvAbout() {
        mAboutAdapter = new AboutAdapter(null);
        mAboutAdapter.setOnItemClickListener((adapter, view, position) -> {
            AboutInfo aboutInfo = mAboutAdapter.getData().get(position);
            switch (position) {
                case 0:
                    CommonUtils.startBrowser(getContext(), aboutInfo.getValue());
                    break;
                case 1:
                    CommonUtils.joinQQGroup(getContext(), aboutInfo.getValue());
                    break;
                case 2:
                    CommonUtils.copyWithToast(getContext(), aboutInfo.getValue(), "邮箱已复制");
                    break;
                case 3:
                    checkVersion();
                    break;
            }
        });
        mRvAbout.setAdapter(mAboutAdapter);
        mRvAbout.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtils.setItemDivider(getContext(), mRvAbout);
        List<AboutInfo> aboutInfos = new ArrayList<>();
        aboutInfos.add(new AboutInfo("官方网站", "http://www.6ll.com"));
        aboutInfos.add(new AboutInfo("官方QQ群", "945084787"));
        aboutInfos.add(new AboutInfo("客服邮箱", "1652728207@qq.com"));
        String versionCode = "";
        if (GoagalInfo.get() != null && GoagalInfo.get().getPackageInfo() != null) {
            versionCode = GoagalInfo.get().getPackageInfo().versionName;
        }
        aboutInfos.add(new AboutInfo("当前版本", "v" + versionCode));
        mAboutAdapter.setNewInstance(aboutInfos);
    }

    @Override
    protected void initVars() {
        super.initVars();
        mUpdateEngine = new UpdateEngine(getContext());
    }

    UpdateEngine mUpdateEngine;

    private void checkVersion() {
        mLoadingDialog.show("获取更新中...");
        mUpdateEngine.getUpdateInfo().subscribe(new Observer<ResultInfo<UpgradeInfo>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), "数据获取失败");
            }

            @Override
            public void onNext(ResultInfo<UpgradeInfo> info) {
                if (info != null && info.getCode() == 1) {

                    UpdateInfo updateInfo = CommonUtils.getNeedUpgradeInfo(info.getData().getUpgrade());
                    if (updateInfo != null) {
                        UpdateDialog updateDialog = new UpdateDialog(getContext());
                        updateDialog.show(updateInfo);
                    }

                } else {
                    ToastCompat.show(getContext(), info == null ? "数据获取失败" : info.getMsg());
                }
            }
        });
    }

    @OnClick(R.id.stv_check)
    public void onViewClicked() {
        checkVersion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdateEngine != null) {
            mUpdateEngine.cancel();
        }
    }
}
