package com.yc.yfiotlock.controller.dialogs.lock.share;

import android.content.Context;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.dialogs.BaseBottomSheetDialog;
import com.yc.yfiotlock.controller.dialogs.BaseDialog;

import butterknife.BindView;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/29
 **/
public class ReceiveDeviceDialog extends BaseBottomSheetDialog {
    @BindView(R.id.tv_from)
    TextView mTvFrom;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_sure)
    TextView mTvSure;

    public ReceiveDeviceDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_receive_device;
    }

    @Override
    protected void initViews() {
        setCanceledOnTouchOutside(false);
    }


    /**
     * @param desp 门锁分享来自的用户名
     */
    public void show(String desp,String positiveBtnString) {
        super.show();
        mTvFrom.setText(desp);
        mTvSure.setText(positiveBtnString);
    }

    @Override
    public void bindClick() {
        setClick(R.id.tv_cancel, this::dismiss);
        setClick(R.id.tv_sure, () -> {
            if (mOnBtnClick != null) {
                mOnBtnClick.onClick();
            }
            dismiss();
        });
    }

    OnBtnClick mOnBtnClick;

    public void setOnBtnClick(OnBtnClick onBtnClick) {
        mOnBtnClick = onBtnClick;
    }

    public interface OnBtnClick {
        /**
         * 点击确定按钮的回调
         */
        void onClick();
    }
}
