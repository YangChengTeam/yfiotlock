package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.utils.PictureUtils;
import com.yc.yfiotlock.view.adapters.FeedBackAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.et_question)
    EditText mEtQuestion;
    @BindView(R.id.ll_et)
    LinearLayout mLlEt;
    @BindView(R.id.rv_pic)
    RecyclerView mRvPic;
    @BindView(R.id.ll_pic)
    LinearLayout mLlPic;
    @BindView(R.id.et_contact)
    EditText mEtContact;
    @BindView(R.id.ll_contact)
    LinearLayout mLlContact;
    @BindView(R.id.et_router)
    EditText mEtRouter;
    @BindView(R.id.ll_router)
    LinearLayout mLlRouter;
    @BindView(R.id.stv_send_with_log)
    SuperTextView mStvSendWithLog;
    @BindView(R.id.stv_send)
    SuperTextView mStvSend;

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_feed_back;
    }

    @Override
    protected void initViews() {
        setRvPic();
    }

    FeedBackAdapter mFeedBackAdapter;

    private void setRvPic() {
        mFeedBackAdapter = new FeedBackAdapter(null);
        mRvPic.setAdapter(mFeedBackAdapter);
        mRvPic.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        List<Uri> strings = new ArrayList<>();
        strings.add(Uri.parse("default"));
        mFeedBackAdapter.setNewInstance(strings);
        mFeedBackAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_cancel:
                    mFeedBackAdapter.removeAt(position);
                    mFeedBackAdapter.notifyItemRemoved(position);
                    if (mFeedBackAdapter.getDefaultCount() == 0) {
                        mFeedBackAdapter.addData(Uri.parse("default"));
                    }
                    break;
                case R.id.iv_pic:
                    if (view.getTag().equals(Uri.parse("default"))) {
                        choosePics();
                    }
                    break;
            }
        });
    }

    private void choosePics() {
        getPermissionHelper().setMustPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {

            private SelectionCreator mSelectionCreator;

            @Override
            public void onRequestPermissionSuccess() {
                if (mSelectionCreator == null) {
                    mSelectionCreator = Matisse.from(FeedBackActivity.this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .theme(R.style.Matisse_Dracula)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .showPreview(true);
                }
                mSelectionCreator.maxSelectable(3 - mFeedBackAdapter.getData().size() + mFeedBackAdapter.getDefaultCount()).forResult(333);
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.show(getContext(), "请先授予存储权限", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333 && resultCode == RESULT_OK) {
            if (data == null) return;
            List<Uri> photoUris = Matisse.obtainResult(data);
            List<Uri> beforeUris = mFeedBackAdapter.getData();
            beforeUris.remove(Uri.parse("default"));
            for (Uri uri : photoUris) {
                if (beforeUris.size() < 3) {
                    beforeUris.add(uri);
                }
            }
            if (beforeUris.size() < 3) {
                beforeUris.add(Uri.parse("default"));
            }
            mFeedBackAdapter.setNewInstance(null);
            mFeedBackAdapter.setNewInstance(beforeUris);
        }
    }

    private void uploadPic() {
        List<Uri> uris = mFeedBackAdapter.getData();
        uris.remove(Uri.parse("default"));
        if (uris.size() == 0) {
            return;
        }
        PictureUtils.zipPic(getContext(), uris, new Callback<List<File>>() {
            @Override
            public void onSuccess(List<File> resultInfo) {
                Log.i("aaaa", "onSuccess: " + resultInfo.size());
            }

            @Override
            public void onFailure(Response response) {
                ToastCompat.show(getContext(), response.body);
            }
        });

    }

    @OnClick({R.id.stv_send_with_log, R.id.stv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_send_with_log:
                break;
            case R.id.stv_send:
                uploadPic();
                break;
        }
    }
}