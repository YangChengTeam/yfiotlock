package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MyFamilyNameActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.et_family_name)
    EditText editText;
    @BindView(R.id.tv_family_name_sure)
    TextView tvSure;

    private FamilyInfo familyInfo = new FamilyInfo();

    public static void start(Context context, FamilyInfo familyInfo) {
        Intent intent = new Intent(context, MyFamilyNameActivity.class);
        intent.putExtra("family_info", familyInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_family_name;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        Serializable serializable = getIntent().getSerializableExtra("family_info");
        if (serializable instanceof FamilyInfo) {
            this.familyInfo = (FamilyInfo) serializable;
            editText.setText(familyInfo.getName());
        }

        RxView.clicks(tvSure).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            String trim = editText.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                ToastUtil.toast2(this, "请输入家庭名称");
                return;
            }
            familyInfo.setName(trim);
            EventBus.getDefault().post(familyInfo);
            finish();
        });
    }
}
