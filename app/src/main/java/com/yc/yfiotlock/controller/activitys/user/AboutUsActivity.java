package com.yc.yfiotlock.controller.activitys.user;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.GoagalInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.AboutInfo;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.view.adapters.AboutAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private void checkVersion() {
        ToastCompat.show(getContext(), "已是最新版本！");
    }

    @OnClick(R.id.stv_check)
    public void onViewClicked() {
        checkVersion();
    }
}
