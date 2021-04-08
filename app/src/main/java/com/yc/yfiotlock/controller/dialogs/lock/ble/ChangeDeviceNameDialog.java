package com.yc.yfiotlock.controller.dialogs.lock.ble;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.utils.CommonUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/19
 **/
public class ChangeDeviceNameDialog extends Dialog {
    @BindView(R.id.iv_cancel)
    ImageView mIvCancel;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.stv_sure)
    SuperTextView mStvSure;

    public ChangeDeviceNameDialog(@NonNull Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(
                getLayoutId(), null);
        ButterKnife.bind(this, view);
        setContentView(view);
        setCancelable(true);
    }

    /**
     * 设置view的点击事件
     * 最好结合{@link #setClick(int, Runnable)} 使用
     */
    protected void bindClick() {

    }

    /**
     * @param id       view id
     * @param runnable when click to do sth.
     */
    protected void setClick(@IdRes int id, @NonNull Runnable runnable) {
        RxView.clicks(findViewById(id)).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            runnable.run();
        });
    }

    /**
     * @param view     view
     * @param runnable when click to do sth.
     */
    protected void setClick(@NonNull View view, @NonNull Runnable runnable) {
        RxView.clicks(view).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view1 -> {
            runnable.run();
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    protected int getLayoutId() {
        return R.layout.lock_ble_add_dialog_edit_device_name;
    }

    protected void initViews() {
        setClick(mStvSure, () -> {
            if (mOnSureClick != null) {
                mOnSureClick.onClick(mEtName.getText().toString());
                dismiss();
            }
        });
        setClick(mIvCancel, this::dismiss);
        CommonUtil.setEditTextLimit(mEtName,20,true);
    }

    public void show(@NonNull String name) {
        super.show();
        mEtName.setText(name);
        if (mEtName.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            mEtName.setSelection(mEtName.getText().length());
        }
    }

    OnSureClick mOnSureClick;

    public void setOnSureClick(OnSureClick onSureClick) {
        mOnSureClick = onSureClick;
    }

    public interface OnSureClick {
        /**
         * 确定 点击回调
         *
         * @param name 设备名字
         */
        void onClick(String name);
    }
}
