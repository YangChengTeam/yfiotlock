package com.yc.yfiotlock.controller.activitys.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.user.UpdateIconDialog;
import com.yc.yfiotlock.model.bean.PersonalInfo;
import com.yc.yfiotlock.model.bean.UserInfo;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.adapters.PersonalEditAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalInfoActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_info)
    RecyclerView mRvInfo;
    @BindView(R.id.stv_logout)
    SuperTextView mStvLogout;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_edit_info;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        setRvInfo();
        loadUserInfo();
    }

    private PersonalEditAdapter mAdapter;

    private void setRvInfo() {
        mAdapter = new PersonalEditAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    UpdateIconDialog updateIconDialog = new UpdateIconDialog(this);
                    updateIconDialog.setOnTvClickListener(new UpdateIconDialog.OnTvClickListener() {
                        @Override
                        public void camera() {

                        }

                        @Override
                        public void pics() {

                        }
                    });
                    updateIconDialog.show();
                    break;
                case 2:
                    startActivity(new Intent(getContext(), EditNameActivity.class));
                    break;
            }
        });
        mRvInfo.setAdapter(mAdapter);
        mRvInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtils.setItemDivider(getContext(), mRvInfo);
    }

    private void onUseCamera() {

    }

    private void onUsePic() {

    }

    private void loadUserInfo() {
        UserInfo userInfo = UserInfoCache.getUserInfo();
        if (userInfo == null) {
            finish();
            return;
        }
        List<PersonalInfo> personalInfos = new ArrayList<>();
        personalInfos.add(new PersonalInfo("头像", "", userInfo.getFace(), 0));
        personalInfos.add(new PersonalInfo("账号", userInfo.getAccount(), "", 1).setShowArrow(false));
        personalInfos.add(new PersonalInfo("昵称", userInfo.getName(), "", 1));
        mAdapter.setNewInstance(personalInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(UserInfo userInfo) {
        loadUserInfo();
    }

    @OnClick(R.id.stv_logout)
    public void onViewClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("是否确定退出？")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    UserInfoCache.setUserInfo(null);
                    EventBus.getDefault().post(new UserInfo());
                    finish();
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));

    }
}