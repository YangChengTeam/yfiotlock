package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MyFamilyNameActivity extends BaseBackActivity {

    @BindView(R.id.et_family_name)
    EditText editText;
    @BindView(R.id.tv_family_name_sure)
    TextView tvSure;

    private FamilyInfo familyInfo;

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
    protected void initVars() {
        super.initVars();
        familyInfo = (FamilyInfo) getIntent().getSerializableExtra("family_info");
        if(familyInfo == null){
            familyInfo = new FamilyInfo();
        }
    }

    @Override
    protected void initViews() {
        super.initViews();

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

        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
