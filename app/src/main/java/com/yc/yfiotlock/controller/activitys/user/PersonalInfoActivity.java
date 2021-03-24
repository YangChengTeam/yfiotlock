package com.yc.yfiotlock.controller.activitys.user;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.user.UpdateIconDialog;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.user.PersonalInfo;
import com.yc.yfiotlock.model.bean.user.PicInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.UploadFileEngine;
import com.yc.yfiotlock.model.engin.UserEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.utils.PathUtil;
import com.yc.yfiotlock.utils.PictureUtil;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.adapters.PersonalEditAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PersonalInfoActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_info)
    RecyclerView mRvInfo;
    @BindView(R.id.stv_logout)
    SuperTextView mStvLogout;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_edit_info;
    }

    public static final int USE_CAMERA = 101;
    public static final int USE_PIC = 102;
    public static final int USE_CROP = 103;
    private Uri mImageUri;
    private String mFilePath;
    private String mFileName = "userIcon.png";
    public static final String CROP_ICON_NAME = "cropIcon.png";

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        setRvInfo();
        loadUserInfo();
        mFilePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    private PersonalEditAdapter mAdapter;

    private void setRvInfo() {
        mAdapter = new PersonalEditAdapter(null);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    UpdateIconDialog updateIconDialog = new UpdateIconDialog(this);
                    updateIconDialog.setOnTvClickListener(new UpdateIconDialog.OnTvClickListener() {
                        @Override
                        public void camera() {
                            onUseCamera();
                        }

                        @Override
                        public void pics() {
                            onUsePic();
                        }
                    });
                    File file = new File(mFilePath, mFileName);
                    if (file.exists() && file.delete()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    updateIconDialog.show();
                    break;
                case 2:
                    startActivity(new Intent(getContext(), EditNameActivity.class));
                    break;
                default:
                    break;
            }
        });
        mRvInfo.setAdapter(mAdapter);
        mRvInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvInfo);
    }

    private void onUseCamera() {
        getPermissionHelper().setMustPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE});
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//启动相机
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
                    mImageUri = FileProvider.getUriForFile(getContext(),
                            getApplicationContext().getPackageName() + ".DownloadProvider", new File(mFilePath, mFileName));
                    takePhoto.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//允许跳转到的目标对uri执行读写权限。
                    takePhoto.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    //7.0以下使用这种方式创建一个Uri
                    mImageUri = Uri.fromFile(new File(mFilePath, mFileName));
                }
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);//根据uri保存图片或视频 mediaStore
                try {
                    startActivityForResult(takePhoto, USE_CAMERA);//设置带返回值的跳转
                } catch (ActivityNotFoundException e) {
                    ToastCompat.show(getContext(), "没有找到相机程序", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.showCenter(getContext(), "请授予权限");
            }
        });
    }

    private void onUsePic() {
        getPermissionHelper().setMustPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE});
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                Intent pic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {
                    startActivityForResult(pic, USE_PIC);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Intent pic1 = new Intent(Intent.ACTION_GET_CONTENT);
                    pic1.setType("image/*");
                    pic1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try {
                        startActivityForResult(pic1, USE_PIC);
                    } catch (ActivityNotFoundException e1) {
                        ToastCompat.show(getContext(), "未找到资源管理器", Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.showCenter(getContext(), "请授予权限");
            }
        });
    }

    private void onCrop(Uri imageUri) {
        Intent cut = new Intent("com.android.camera.action.CROP");
        cut.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cut.setDataAndType(imageUri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        cut.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        cut.putExtra("aspectX", 1);
        cut.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片尺寸
        cut.putExtra("outputX", 480);
        cut.putExtra("outputY", 480);
        // in Android 11  cropApp cannot use app's private dir ,so we need use public dir
        String publicPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File file = new File(publicPath, CROP_ICON_NAME);
        if (file.exists() && file.delete()) {
            Log.d("aaaa", "onCrop: file.delete" + file.getAbsolutePath());
        }
        cut.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//保存到uri指定的位置
        try {
            startActivityForResult(cut, USE_CROP);
        } catch (Exception e) {
            e.printStackTrace();
            ToastCompat.show(this, "未找到剪切程序，自动剪切", Toast.LENGTH_SHORT);
            cropPicByGlide();
        }
    }

    private void cropPicByGlide() {
        /* get() need runOnBackGroundThread see{@link com.bumptech.glide.util.assertBackgroundThread} */
        File needCropFile = new File(mFilePath, mFileName);
        Observable.just("").observeOn(Schedulers.newThread()).map((Func1<Object, File>) o -> {
            try {
                Bitmap bitmap = Glide.with(getBaseContext())
                        .asBitmap()
                        .load(mFilePath + mFileName)
                        .apply(RequestOptions.centerCropTransform())
                        .submit(480, 480).get();
                FileOutputStream fos = new FileOutputStream(needCropFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return needCropFile;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    if (file == null) {
                        ToastCompat.show(getContext(), "剪切失败", Toast.LENGTH_SHORT);
                    } else {
                        upLoadUserIcon(file);
                    }
                });
    }

    @Override
    protected void initVars() {
        super.initVars();
        mUploadFileEngine = new UploadFileEngine();
        mUserEngine = new UserEngine(getContext());
    }

    private UploadFileEngine mUploadFileEngine;


    /**
     * 上传图片的逻辑
     * 先选择是相机{@link #onUseCamera()}
     * 还是相册{@link #onUsePic()}}
     * 均指定目录 然后再剪切 {@link #onCrop(Uri)} 剪切是同一地址 #mFilePath + mCropPath
     * 最后压缩{@link #zipPic(File)}}
     * 压缩完成后上传
     */
    private void upLoadUserIcon(File file) {
        mLoadingDialog.show("上传头像中...");
        mUploadFileEngine.uploadWithFile(Config.UPLOAD_PIC_URL, new HashMap<>(), "file", file, new Callback<String>() {
                    @Override
                    public void onSuccess(String resultInfo) {
                        try {
                            ResultInfo<PicInfo> info = JSONObject.parseObject(resultInfo, new TypeReference<ResultInfo<PicInfo>>() {
                            }.getType());
                            if (info.getCode() == 1) {
                                Log.d("aaaa", "onSuccess: " + info.getData().getPath());
                                changeUserFace(info.getData().getUrl(), info.getData().getPath());
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
                }
        );
    }

    private UserEngine mUserEngine;

    private void changeUserFace(String face, String faceNoHost) {
        mLoadingDialog.show("提交中...");
        mUserEngine.changeFace(faceNoHost).subscribe(new Observer<ResultInfo<String>>() {
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
                    UserInfo userInfo = UserInfoCache.getUserInfo();
                    userInfo.setFace(face);
                    UserInfoCache.setUserInfo(userInfo);
                    EventBus.getDefault().post(userInfo);
                    ToastCompat.showCenter(getContext(), "修改成功");
                } else {
                    ToastCompat.showCenter(getContext(), info == null ? "修改失败" : info.getMsg());
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USE_CAMERA && resultCode == RESULT_OK) {
            onCrop(mImageUri);
        }
        if (requestCode == USE_PIC && resultCode == RESULT_OK) {
            File file = PathUtil.copyFileToPath(getContext(), data.getData(), mFilePath + CROP_ICON_NAME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
                mImageUri = FileProvider.getUriForFile(getContext(),
                        getApplicationContext().getPackageName() + ".DownloadProvider", file);
            } else {
                //7.0以下使用这种方式创建一个Uri
                mImageUri = Uri.fromFile(file);
            }
            onCrop(mImageUri);
        }
        if (requestCode == USE_CROP && resultCode == RESULT_OK) {
            zipPic(new File(mFilePath, CROP_ICON_NAME));
        }

    }

    private void zipPic(File file) {
        mLoadingDialog.show("压缩中...");
        PictureUtil.zipPic(getContext(), file, new Callback<File>() {
            @Override
            public void onSuccess(File resultInfo) {
                mLoadingDialog.dismiss();
                upLoadUserIcon(resultInfo);
            }

            @Override
            public void onFailure(Response response) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), response.body);
            }
        });
    }

    private void loadUserInfo() {
        UserInfo userInfo = UserInfoCache.getUserInfo();
        if (userInfo == null) {
            finish();
            return;
        }
        List<PersonalInfo> personalInfos = new ArrayList<>();
        personalInfos.add(new PersonalInfo("头像", "", userInfo.getFace(), 0));
        personalInfos.add(new PersonalInfo("账号", userInfo.getMobile(), "", 1).setShowArrow(false));
        personalInfos.add(new PersonalInfo("昵称", userInfo.getNickName(), "", 1));
        mAdapter.setNewInstance(personalInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(UserInfo userInfo) {
        loadUserInfo();
    }

    @Override
    protected void bindClick() {
        setClick(R.id.stv_logout, this::showLogoutDialog);
    }

    private void showLogoutDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("是否确定退出？")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    mLoadingDialog.show("退出登录中...");
                    VUiKit.postDelayed(1000,() -> {
                        mLoadingDialog.dismiss();
                        UserInfoCache.setUserInfo(null);
                        EventBus.getDefault().post(new UserInfo());
                        finish();
                    });
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserEngine != null) {
            mUserEngine.cancel();
        }
        if (mUploadFileEngine != null) {
            mUploadFileEngine.cancel();
        }
    }
}