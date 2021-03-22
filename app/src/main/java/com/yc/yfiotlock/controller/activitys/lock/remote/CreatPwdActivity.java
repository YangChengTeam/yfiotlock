package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.view.WheelView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.LeftNextTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class CreatPwdActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_creat_pwd_next)
    TextView tvNext;
    @BindView(R.id.tv_pwd_type_the_one)
    TextView tvTypeTheOne;
    @BindView(R.id.tv_pwd_type_custom)
    TextView tvTypeCustom;
    @BindView(R.id.lnt_start_time)
    LeftNextTextView ltvStartDate;
    @BindView(R.id.lnt_end_time)
    LeftNextTextView ltvEndDate;
    @BindView(R.id.lnt_start_date)
    LeftNextTextView ltvStartTime;
    @BindView(R.id.lnt_end_date)
    LeftNextTextView ltvEndTime;
    @BindView(R.id.cl_custom)
    ConstraintLayout clCustom;
    @BindView(R.id.iv_pass_show_status)
    ImageView statusIv;
    @BindView(R.id.et_pwd_number)
    EditText passEt;
    @BindView(R.id.et_pwd_name)
    EditText nameEt;

    private LockEngine lockEngine;

    public static void start(Context context, DeviceInfo deviceInfo) {
        Intent intent = new Intent(context, CreatPwdActivity.class);
        intent.putExtra("device", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
    }

    private boolean isSelTheOne = true;
    long[] timeArrays = new long[]{0, 0, 0, 0};
    LeftNextTextView[] timeViews;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_creat_pwd;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        timeViews = new LeftNextTextView[]{ltvStartDate, ltvEndDate, ltvStartTime, ltvEndTime};

        RxView.clicks(tvNext).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            toSubmit();
        });
        RxView.clicks(tvTypeTheOne).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            chengUi(true);
        });
        RxView.clicks(tvTypeCustom).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            chengUi(false);
        });
        RxView.clicks(ltvStartDate).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            showTimePickerView(0);
        });
        RxView.clicks(ltvEndDate).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            showTimePickerView(1);
        });
        RxView.clicks(ltvStartTime).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            showTimePickerView(2);
        });
        RxView.clicks(ltvEndTime).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            showTimePickerView(3);
        });

        statusIv.setSelected(true);
        RxView.clicks(statusIv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (statusIv.isSelected()) {
                passEt.setTransformationMethod(null);
                statusIv.setImageResource(R.mipmap.secret_see);
                statusIv.setSelected(false);
                passEt.setSelection(passEt.getText().length());
            } else {
                passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                statusIv.setImageResource(R.mipmap.secret_hide);
                statusIv.setSelected(true);
                passEt.setSelection(passEt.getText().length());
            }
        });

        chengUi(true);

        nameEt.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void toSubmit() {
        if (isSelTheOne) {
            String trimName = nameEt.getText().toString().trim();
            if (TextUtils.isEmpty(trimName)) {
                ToastUtil.toast2(CreatPwdActivity.this, "请输入密码名称");
                return;
            }
            String trimPwd = passEt.getText().toString().trim();
            if (TextUtils.isEmpty(trimPwd)) {
                ToastUtil.toast2(CreatPwdActivity.this, "请输入密码");
                return;
            }
            if (trimPwd.length() != 6) {
                ToastUtil.toast2(CreatPwdActivity.this, "密码长度不合规");
                return;
            }
            Serializable device = getIntent().getSerializableExtra("device");
            if (!(device instanceof DeviceInfo)) {
                ToastUtil.toast2(CreatPwdActivity.this, "未连接设备");
                return;
            }
            mLoadingDialog.show("添加中...");
            lockEngine.addOpenLockWay(((DeviceInfo) device).getId(), trimName, "",
                    LockBLEManager.OPEN_LOCK_PASSWORD, String.valueOf(LockBLEManager.GROUP_TYPE_TEMP_PWD),
                    trimPwd).subscribe(new Subscriber<ResultInfo<String>>() {
                @Override
                public void onCompleted() {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onNext(ResultInfo<String> stringResultInfo) {
                    if (stringResultInfo != null) {
                        if (stringResultInfo.getCode() == 1) {
                            PassWordInfo passWordInfo = new PassWordInfo();
                            passWordInfo.setName(trimName);
                            passWordInfo.setPwd(trimPwd);
                            CreatPwdSuccessActivity.start(CreatPwdActivity.this, passWordInfo);

                            mLoadingDialog.dismiss();
                            EventBus.getDefault().post(new OpenLockRefreshEvent());
                            finish();
                        }
                    }
                }
            });
        } else {
            ToastUtil.toast2(CreatPwdActivity.this, "无法创建自定义密码");
        }
    }

    private void chengUi(boolean selTheOne) {
        isSelTheOne = selTheOne;

        if (isSelTheOne) {
            clCustom.setVisibility(View.GONE);

            tvTypeTheOne.setBackground(getResources().getDrawable(R.drawable.shap_stroke_blue));
            tvTypeTheOne.setTextColor(getResources().getColor(R.color.blue_3091F8));
            tvTypeCustom.setBackground(getResources().getDrawable(R.drawable.shap_stroke_gray));
            tvTypeCustom.setTextColor(getResources().getColor(R.color.gray_222));
        } else {
            clCustom.setVisibility(View.VISIBLE);

            tvTypeTheOne.setBackground(getResources().getDrawable(R.drawable.shap_stroke_gray));
            tvTypeTheOne.setTextColor(getResources().getColor(R.color.gray_222));
            tvTypeCustom.setBackground(getResources().getDrawable(R.drawable.shap_stroke_blue));
            tvTypeCustom.setTextColor(getResources().getColor(R.color.blue_3091F8));
        }
    }

    private void showTimePickerView(int selectType) {
        Calendar start = Calendar.getInstance();
        Calendar sel = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.set(2050, 11, 31);
        boolean[] type;

        if (selectType == 0 || selectType == 1) {
            type = new boolean[]{true, true, true, false, false, false};
        } else {
            type = new boolean[]{false, false, false, true, true, true};
        }

        @SuppressLint("SimpleDateFormat")
        TimePickerView mPickerView = new TimePickerBuilder(this, (date, v) -> {
            long time = date.getTime();
            SimpleDateFormat format;
            if (selectType == 0 || selectType == 1) {
                format = new SimpleDateFormat("yyyy/MM/dd");
            } else {
                format = new SimpleDateFormat("HH:mm:ss");
            }
            timeViews[selectType].setTvDes(format.format(time));
            timeArrays[selectType] = time;
        })
                .setType(type)//控制文字是否显示
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setCancelColor(Color.parseColor("#007AFF"))
                .setCancelText("取消")
                .setLineSpacingMultiplier(2f)
                .setSubCalSize(13)
                .setTextColorCenter(Color.parseColor("#ff000000"))
                .setTextColorOut(Color.GRAY)
                .setDividerType(WheelView.DividerType.FILL)
                .setDividerColor(Color.parseColor("#ffcdcdcd"))
                .setSubmitText("确定")//确定按钮
                .setSubmitColor(Color.parseColor("#007AFF"))
                .setDate(sel)
                .setTitleBgColor(Color.parseColor("#fff2f2f2"))
                .setOutSideCancelable(false)
                .setRangDate(start, end)
                .build();
        mPickerView.show();
    }
}
