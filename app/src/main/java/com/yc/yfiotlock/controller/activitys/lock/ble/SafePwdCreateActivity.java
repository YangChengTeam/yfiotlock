package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceSafeSettingInfo;
import com.yc.yfiotlock.view.adapters.SignCodeAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yc.yfiotlock.controller.fragments.lock.ble.IndexFragment.CHECK_PWD;

/**
 * @author Dullyoung
 */
public class SafePwdCreateActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_input_new)
    TextView mTvInputNew;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.rv_pwd)
    RecyclerView mRvPwd;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_safe_pwd_create;
    }


    /**
     * 是否是验证密码
     */
    private boolean isForCheck = false;

    private void setUi() {
        if (isForCheck) {
            mBnbTitle.setTitle("验证安全密码");
            mTvInputNew.setText("请输入密码");
        } else {
            mBnbTitle.setTitle("创建安全密码");
            mTvInputNew.setText("输入新密码");
        }
    }

    /**
     * @param context 启动验证密码界面
     */
    public static void startCheck(Activity context) {
        Intent intent = new Intent(context, SafePwdCreateActivity.class);
        intent.putExtra("check", true);
        context.startActivityForResult(intent,CHECK_PWD);
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        mEtPwd.setOnLongClickListener(v -> true);
        isForCheck = getIntent().getBooleanExtra("check", false);
        setRvPwd();
        showInput();
        setUi();
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                char[] chars = s.toString().toCharArray();
                List<String> stringList = mSignCodeAdapter.getData();
                for (int i = 0; i < Config.SMS_CODE_LENGTH; i++) {
                    if (i < chars.length) {
                        stringList.set(i, chars[i] + "");
                    } else {
                        stringList.set(i, "");
                    }
                    mSignCodeAdapter.notifyItemChanged(i, "");
                }
                if (s.toString().length() == 6) {
                    onInputResult(s.toString());
                }
                Log.i("aaaa", "afterTextChanged: " + stringList.toString());
            }
        });
    }

    private void onInputResult(String s) {
        Intent intent = new Intent();
        intent.putExtra("pwd", s);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void showInput() {
        mEtPwd.setFocusable(true);
        mEtPwd.setFocusableInTouchMode(true);
        mEtPwd.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEtPwd, InputMethodManager.SHOW_IMPLICIT);
    }

    SignCodeAdapter mSignCodeAdapter;

    private void setRvPwd() {
        mSignCodeAdapter = new SignCodeAdapter(R.layout.item_pwd, null);
        mSignCodeAdapter.setShowText(false);
        mRvPwd.setAdapter(mSignCodeAdapter);
        mSignCodeAdapter.setOnItemClickListener((adapter, view, position) -> {
            showInput();
        });
        mRvPwd.setLayoutManager(new GridLayoutManager(getContext(), Config.SMS_CODE_LENGTH, LinearLayoutManager.VERTICAL, false));
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            strings.add("");
        }
        mSignCodeAdapter.setNewInstance(strings);
    }

}