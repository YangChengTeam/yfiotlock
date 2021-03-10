package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.user.PersonalInfo;
import com.yc.yfiotlock.model.bean.user.PicInfo;
import com.yc.yfiotlock.model.engin.FeedBackEngine;
import com.yc.yfiotlock.model.engin.UploadFileEngine;
import com.yc.yfiotlock.utils.PictureUtils;
import com.yc.yfiotlock.view.adapters.FeedBackAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;

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
        mBnbTitle.setBackListener(view -> finish());
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

    private void zipPic() {
        List<Uri> uris = mFeedBackAdapter.getData();
        uris.remove(Uri.parse("default"));
        if (uris.size() == 0) {
            return;
        }
        PictureUtils.zipPic(getContext(), uris, new Callback<List<File>>() {
            @Override
            public void onSuccess(List<File> resultInfo) {
                Log.i("aaaa", "onSuccess: " + resultInfo.size());
                uploadPic(resultInfo);
            }

            @Override
            public void onFailure(Response response) {
                ToastCompat.show(getContext(), response.body);
            }
        });
    }

    private List<String> backImgs = new ArrayList<>();

    private void uploadPic(List<File> files) {
        if (backImgs.size() < files.size()) {
            mLoadingDialog.show("上传第" + (backImgs.size() + 1) + "张图片");
            mUploadFileEngine.uploadWithFile(Config.UPLOAD_PIC_URL, new HashMap<>(), "file",
                    files.get(backImgs.size()), new Callback<String>() {
                        public void onSuccess(String resultInfo) {
                            try {
                                ResultInfo<PicInfo> info = JSONObject.parseObject(resultInfo, new TypeReference<ResultInfo<PicInfo>>() {
                                }.getType());
                                if (info.getCode() == 1) {
                                    Log.i("aaaa", "onSuccess: " + info.getData().getPath());
                                    backImgs.add(info.getData().getUrl());
                                    uploadPic(files);
                                } else {
                                    ToastCompat.show(getContext(), info.getMsg());
                                }
                            } catch (Exception e) {
                                this.onFailure(new Response("" + e));
                            }
                        }

                        @Override
                        public void onFailure(Response response) {
                            mLoadingDialog.dismiss();
                            ToastCompat.show(getContext(), response.body);
                        }
                    });
        } else {
            mLoadingDialog.show("提交中...");
            commit();
        }
    }

    private String getImgString(List<String> strings) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            s.append(strings.get(i));
            if (i != strings.size() - 1) {
                s.append(",");
            }
        }
        return s.toString();
    }

    private void commit() {
        mFeedBackEngine.addInfo(mEtContact.getText().toString(),
                mEtQuestion.getText().toString(),
                mEtRouter.getText().toString(),
                getImgString(backImgs)).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    ToastCompat.showCenter(getContext(), "提交成功");
                    finish();
                } else {
                    ToastCompat.showCenter(getContext(), info == null ? "提交失败" : info.getMsg());
                }
            }
        });
    }

    private UploadFileEngine mUploadFileEngine;
    private FeedBackEngine mFeedBackEngine;

    @Override
    protected void initVars() {
        super.initVars();
        mUploadFileEngine = new UploadFileEngine();
        mFeedBackEngine = new FeedBackEngine(getContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFeedBackEngine != null) mFeedBackEngine.cancel();
        if (mUploadFileEngine != null) mUploadFileEngine.cancel();
    }

    @OnClick({R.id.stv_send_with_log, R.id.stv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_send_with_log:
                break;
            case R.id.stv_send:
                zipPic();
                break;
        }
    }
}