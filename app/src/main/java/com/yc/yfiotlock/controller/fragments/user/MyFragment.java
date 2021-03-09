package com.yc.yfiotlock.controller.fragments.user;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.user.AboutUsActivity;
import com.yc.yfiotlock.controller.activitys.user.PersonalInfoActivity;
import com.yc.yfiotlock.controller.activitys.user.SuggestActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.user.PersonalInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.adapters.PersonalAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class MyFragment extends BaseFragment {
    @BindView(R.id.stv_face)
    SuperTextView mStvFace;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_device_number)
    TextView mTvDeviceNumber;
    @BindView(R.id.rv_my)
    RecyclerView mRvMy;
    @BindView(R.id.iv_face)
    ImageView mIvFace;

    @Override
    protected int getLayoutId() {
        return R.layout.user_my_fragment;
    }

    @Override
    protected void initViews() {
        setRvMy();
        loadUserInfo();
    }

    private void loadUserInfo() {
        UserInfo userInfo = UserInfoCache.getUserInfo();
        if (userInfo == null) {
            Glide.with(getContext())
                    .load("")
                    .placeholder(R.mipmap.head_default)
                    .error(R.mipmap.head_default)
                    .circleCrop()
                    .into(mIvFace);
            mTvUserName.setText("");
            mTvDeviceNumber.setText("");
            return;
        }
        Glide.with(getContext())
                .load(userInfo.getFace())
                .placeholder(R.mipmap.head_default)
                .error(R.mipmap.head_default)
                .circleCrop()
                .into(mIvFace);
        mTvUserName.setText(userInfo.getName());
        mTvDeviceNumber.setText(userInfo.getDeviceNumber().concat("个智能设备"));
    }

    private PersonalAdapter mPersonalAdapter;

    private void setRvMy() {
        mPersonalAdapter = new PersonalAdapter(null);
        mRvMy.setAdapter(mPersonalAdapter);
        mRvMy.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtils.setItemDivider(getContext(), mRvMy);
        mPersonalAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getContext(), SuggestActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getContext(), AboutUsActivity.class));
                    break;
            }
        });
        List<PersonalInfo> personalInfos = new ArrayList<>();
        personalInfos.add(new PersonalInfo("投诉及建议", R.mipmap.icon_suggest));
        personalInfos.add(new PersonalInfo("关于我们", R.mipmap.icon_us));
        mPersonalAdapter.setNewInstance(personalInfos);
    }

    @OnClick({R.id.stv_face, R.id.tv_user_name, R.id.tv_device_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_face:
            case R.id.tv_user_name:
            case R.id.tv_device_number:
                startActivity(new Intent(getContext(), PersonalInfoActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(UserInfo userInfo) {
        loadUserInfo();
    }
}
