package com.yc.yfiotlock.controller.activitys.user;

import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
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
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
        checkVersion(false);
    }

    private AboutAdapter mAboutAdapter;

    private void setRvAbout() {
        mAboutAdapter = new AboutAdapter(null);
        mAboutAdapter.setOnItemClickListener((adapter, view, position) -> {
            AboutInfo aboutInfo = mAboutAdapter.getData().get(position);
            switch (position) {
                case 0:
                    CommonUtil.startBrowser(getContext(), aboutInfo.getValue());
                    break;
                case 1:
                    CommonUtil.joinQQGroup(getContext(), aboutInfo.getValue());
                    break;
                case 2:
                    CommonUtil.copyWithToast(getContext(), aboutInfo.getValue(), "邮箱已复制");
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        });
        mRvAbout.setAdapter(mAboutAdapter);
        mRvAbout.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvAbout);
        List<AboutInfo> aboutInfos = new ArrayList<>();
        aboutInfos.add(new AboutInfo("官方网站", ""));
        aboutInfos.add(new AboutInfo("官方QQ群", ""));
        aboutInfos.add(new AboutInfo("客服邮箱", ""));
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
        setClick(R.id.stv_check, () -> checkVersion(true));
    }

    private boolean loadCache(boolean showDialog) {
        ResultInfo<UpgradeInfo> info = CacheUtil.getCache(mUpdateEngine.getUrl(), new TypeReference<ResultInfo<UpgradeInfo>>() {
        }.getType());
        if (info != null && info.getData() != null) {
            onSuccess(info, showDialog);
            return true;
        }
        return false;
    }

    private UpdateEngine mUpdateEngine;

    private void checkVersion(boolean showDialog) {
        if (!loadCache(showDialog)) {
            mLoadingDialog.show("获取更新中...");
        }
        mUpdateEngine.getUpdateInfo().subscribe(new Observer<ResultInfo<UpgradeInfo>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<UpgradeInfo> info) {
                if (info != null && info.getCode() == 1 && info.getData() != null) {
                    onSuccess(info, showDialog);
                    CacheUtil.setCache(mUpdateEngine.getUrl(), info);
                } else {
                    if (info != null && info.getMsg() != null) {
                        ToastCompat.show(getContext(), info.getMsg());
                    }
                }
            }
        });
    }

    UpdateDialog updateDialog;

    private void onSuccess(ResultInfo<UpgradeInfo> info, boolean showDialog) {
        UpgradeInfo upgradeInfo = info.getData();

        mAboutAdapter.getData().get(0).setValue(upgradeInfo.getOfficialWeb());
        mAboutAdapter.getData().get(1).setValue(upgradeInfo.getKfQqQun());
        mAboutAdapter.getData().get(2).setValue(upgradeInfo.getKfEmail());
        mAboutAdapter.notifyDataSetChanged();

        UpdateInfo updateInfo = CommonUtil.getNeedUpgradeInfo(info.getData().getUpgrade());
        if (updateInfo != null) {
            if (showDialog) {
                if (updateDialog == null) {
                    updateDialog = new UpdateDialog(getContext());
                }
                updateDialog.show(updateInfo);
            }
            mStvCheck.setShaderEnable(true);
            mStvCheck.setShaderStartColor(0xff34A2FF);
            mStvCheck.setShaderEndColor(0xff338DFC);
            mStvCheck.setShaderMode(SuperTextView.ShaderMode.TOP_TO_BOTTOM);
            mStvCheck.setClickable(true);
            mStvCheck.setSolid(Color.TRANSPARENT);
            mStvCheck.setText("有新版本可以更新");
        } else {
            ToastCompat.showCenter(getContext(), "已是最新版本");
            mStvCheck.setSolid(getResources().getColor(R.color.blue_no_input));
            mStvCheck.setClickable(false);
            mStvCheck.setPressBgColor(Color.TRANSPARENT);
            mStvCheck.setShaderEnable(false);
            mStvCheck.setText("已是最新版本");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdateEngine != null) {
            mUpdateEngine.cancel();
        }
    }

    private class AboutAdapter extends BaseExtendAdapter<AboutInfo> {
        public AboutAdapter(@Nullable List<AboutInfo> data) {
            super(R.layout.item_about_us, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, AboutInfo aboutInfo) {
            holder.setText(R.id.tv_name, aboutInfo.getName());
            holder.setText(R.id.tv_value, aboutInfo.getValue());
        }
    }
}
