package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.yc.yfiotlock.view.adapters.SignCodeAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


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


    private void setUi() {
        switch (getIntent().getIntExtra("type", 0)) {
            case CHECK_PWD:
                mBnbTitle.setTitle("验证安全密码");
                mTvInputNew.setText("请输入安全密码");
                break;
            case INPUT_NEW_PWD:
                mBnbTitle.setTitle("更改安全密码");
                mTvInputNew.setText("请输入新密码");
                break;
            case INPUT_NEW_PWD_AGAIN:
                mBnbTitle.setTitle("更改安全密码");
                mTvInputNew.setText("请再次输入新密码");
                break;
            case CHECK_ORIGIN_PWD:
                mBnbTitle.setTitle("更改安全密码");
                mTvInputNew.setText("请输入原密码");
                break;
            case CREATE_NEW_PWD:
                mBnbTitle.setTitle("创建安全密码");
                mTvInputNew.setText("请输入密码");
                break;
        }

    }

    public static final int CHECK_PWD = 111;//检验密码
    public static final int INPUT_NEW_PWD = 112;//输入新密码
    public static final int INPUT_NEW_PWD_AGAIN = 113;//再次输入新密码
    public static final int CHECK_ORIGIN_PWD = 114;//检验原密码
    public static final int CREATE_NEW_PWD = 115;//创建新密码


    public static void startCheck(Activity context) {
        Intent intent = new Intent(context, SafePwdCreateActivity.class);
        intent.putExtra("type", CHECK_PWD);
        context.startActivityForResult(intent, CHECK_PWD);
    }

    public static void createNewPwd(Activity context) {
        Intent intent = new Intent(context, SafePwdCreateActivity.class);
        intent.putExtra("type", CREATE_NEW_PWD);
        context.startActivityForResult(intent, CREATE_NEW_PWD);
    }

    public static void checkOrigin(Activity context) {
        Intent intent = new Intent(context, SafePwdCreateActivity.class);
        intent.putExtra("type", CHECK_ORIGIN_PWD);
        context.startActivityForResult(intent, CHECK_ORIGIN_PWD);
    }

    public static void inputNew(Activity context) {
        Intent intent = new Intent(context, SafePwdCreateActivity.class);
        intent.putExtra("type", INPUT_NEW_PWD);
        context.startActivityForResult(intent, INPUT_NEW_PWD);
    }

    public static void inputNewAgain(Activity context) {
        Intent intent = new Intent(context, SafePwdCreateActivity.class);
        intent.putExtra("type", INPUT_NEW_PWD_AGAIN);
        context.startActivityForResult(intent, INPUT_NEW_PWD_AGAIN);
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        mEtPwd.setOnLongClickListener(v -> true);
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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