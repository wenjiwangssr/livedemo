package com.example.livedome.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livedome.AppConfig;
import com.example.livedome.Constants;
import com.example.livedome.LiveActivity;
import com.example.livedome.LiveAnchorActivity;
import com.example.livedome.R;
import com.example.livedome.bean.LiveRoomTypeBean;
import com.example.livedome.http.HttpCallback;
import com.example.livedome.http.HttpConsts;
import com.example.livedome.http.HttpUtil;
import com.example.livedome.interfaces.ActivityResultCallback;
import com.example.livedome.interfaces.CommonCallback;
import com.example.livedome.interfaces.ImageResultCallback;
import com.example.livedome.interfaces.LifeCycleAdapter;
import com.example.livedome.utils.DialogUitl;
import com.example.livedome.utils.ImgLoader;
import com.example.livedome.utils.L;
import com.example.livedome.utils.ProcessImageUtil;
import com.example.livedome.utils.StringUtil;
import com.example.livedome.utils.ToastUtil;
import com.example.livedome.utils.WordUtil;

import java.io.File;

/**
 * Created by cxf on 2018/10/7.
 * 开播前准备
 */

public class LiveReadyViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar;
    private TextView mCoverText;
    private EditText mEditTitle;
    private RecyclerView mLiveShareRecyclerView;
//    private LiveReadyShareAdapter mLiveShareAdapter;
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;
    private TextView mLocation;
    private TextView mLiveClass;
    private TextView mLiveTypeTextView;//房间类型TextView
    private int mLiveClassID;//直播频道id
    private int mLiveType;//房间类型
    private int mLiveTypeVal;//房间密码，门票收费金额
    private int mLiveTimeCoin;//计时收费金额
    private ActivityResultCallback mActivityResultCallback;
    private CommonCallback<LiveRoomTypeBean> mLiveTypeCallback;

    public LiveReadyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_ready;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mCoverText = (TextView) findViewById(R.id.cover_text);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mLocation = (TextView) findViewById(R.id.location);
//        mLocation.setText(AppConfig.getInstance().getCity());
        mLiveClass = (TextView) findViewById(R.id.live_class);
        mLiveTypeTextView = (TextView) findViewById(R.id.btn_room_type);
        mLiveShareRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLiveShareRecyclerView.setHasFixedSize(true);
        mLiveShareRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//        mLiveShareAdapter = new LiveReadyShareAdapter(mContext);
//        mLiveShareRecyclerView.setAdapter(mLiveShareAdapter);
        mImageUtil = ((LiveActivity) mContext).getProcessImageUtil();
        mImageUtil.setImageResultCallback(new ImageResultCallback() {

            @Override
            public void beforeCamera() {
                ((LiveAnchorActivity) mContext).beforeCamera();
            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(file, mAvatar);
                    if (mAvatarFile == null) {
                        mCoverText.setText(WordUtil.getString(R.string.live_cover_2));
                        mCoverText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_live_cover));
                    }
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mLiveClass.setOnClickListener(this);
        findViewById(R.id.avatar_group).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_beauty).setOnClickListener(this);
        findViewById(R.id.btn_start_live).setOnClickListener(this);
        mLiveTypeTextView.setOnClickListener(this);
        mActivityResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                mLiveClassID = intent.getIntExtra(Constants.CLASS_ID, 0);
                mLiveClass.setText(intent.getStringExtra(Constants.CLASS_NAME));
            }
        };
        mLiveTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                switch (bean.getId()) {
                    case Constants.LIVE_TYPE_NORMAL:
                        onLiveTypeNormal(bean);
                        break;
//                    case Constants.LIVE_TYPE_PWD:
//                        onLiveTypePwd(bean);
//                        break;
//                    case Constants.LIVE_TYPE_PAY:
//                        onLiveTypePay(bean);
//                        break;
//                    case Constants.LIVE_TYPE_TIME:
//                        onLiveTypeTime(bean);
//                        break;
                }
            }
        };
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConsts.CREATE_ROOM);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.avatar_group:
                setAvatar();
                break;
            case R.id.btn_camera:
                toggleCamera();
                break;
            case R.id.btn_close:
                close();
                break;
//            case R.id.live_class:
//                chooseLiveClass();
//                break;
            case R.id.btn_beauty:
                beauty();
                break;
//            case R.id.btn_room_type:
//                chooseLiveType();
//                break;
            case R.id.btn_start_live:
                startLive();
                break;
        }
    }

    /**
     * 设置头像
     */
    private void setAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.camera}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    /**
     * 切换镜头
     */
    private void toggleCamera() {
        ((LiveAnchorActivity) mContext).toggleCamera();
    }

    /**
     * 关闭
     */
    private void close() {
        ((LiveAnchorActivity) mContext).onBackPressed();
    }

    /**
     * 选择直播频道
     */
//    private void chooseLiveClass() {
//        Intent intent = new Intent(mContext, LiveChooseClassActivity.class);
//        intent.putExtra(Constants.CLASS_ID, mLiveClassID);
//        mImageUtil.startActivityForResult(intent, mActivityResultCallback);
//    }

    /**
     * 设置美颜
     */
    private void beauty() {
        ((LiveAnchorActivity) mContext).beauty();
    }

    /**
     * 选择直播类型
     */
//    private void chooseLiveType() {
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.CHECKED_ID, mLiveType);
//        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
//        fragment.setArguments(bundle);
//        fragment.setCallback(mLiveTypeCallback);
//        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
//    }

    /**
     * 普通房间
     */
    private void onLiveTypeNormal(LiveRoomTypeBean bean) {
        mLiveType = bean.getId();
        mLiveTypeTextView.setText(bean.getName());
        mLiveTypeVal = 0;
        mLiveTimeCoin = 0;
    }

//    /**
//     * 密码房间
//     */
//    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
//        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8, new DialogUitl.SimpleCallback() {
//            @Override
//            public void onConfirmClick(Dialog dialog, String content) {
//                if (TextUtils.isEmpty(content)) {
//                    ToastUtil.show(R.string.live_set_pwd_empty);
//                } else {
//                    mLiveType = bean.getId();
//                    mLiveTypeTextView.setText(bean.getName());
//                    if (StringUtil.isInt(content)) {
//                        mLiveTypeVal = Integer.parseInt(content);
//                    }
//                    mLiveTimeCoin = 0;
//                    dialog.dismiss();
//                }
//            }
//        });
//    }
//
//    /**
//     * 付费房间
//     */
//    private void onLiveTypePay(final LiveRoomTypeBean bean) {
//        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUitl.INPUT_TYPE_NUMBER, 8, new DialogUitl.SimpleCallback() {
//            @Override
//            public void onConfirmClick(Dialog dialog, String content) {
//                if (TextUtils.isEmpty(content)) {
//                    ToastUtil.show(R.string.live_set_fee_empty);
//                } else {
//                    mLiveType = bean.getId();
//                    mLiveTypeTextView.setText(bean.getName());
//                    if (StringUtil.isInt(content)) {
//                        mLiveTypeVal = Integer.parseInt(content);
//                    }
//                    mLiveTimeCoin = 0;
//                    dialog.dismiss();
//                }
//            }
//        });
//    }
//
//    /**
//     * 计时房间
//     */
//    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
//        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
//        fragment.setArguments(bundle);
//        fragment.setCommonCallback(new CommonCallback<Integer>() {
//            @Override
//            public void callback(Integer coin) {
//                mLiveType = bean.getId();
//                mLiveTypeTextView.setText(bean.getName());
//                mLiveTypeVal = coin;
//                mLiveTimeCoin = coin;
//            }
//        });
//        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveTimeDialogFragment");
//    }

    public void hide() {
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }


    public void show() {
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击开始直播按钮
     */
    private void startLive() {
        boolean startPreview = ((LiveAnchorActivity) mContext).isStartPreview();
        if (!startPreview) {
            ToastUtil.show("请稍等");
            return;
        }
        if (mLiveClassID == 0) {
            ToastUtil.show("请选择频道");
            return;
        }
//        if (mLiveShareAdapter != null) {
//            String type = mLiveShareAdapter.getShareType();
//            if (!TextUtils.isEmpty(type)) {
//                ((LiveActivity) mContext).shareLive(type, new MobCallback() {
//                    @Override
//                    public void onSuccess(Object data) {
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        createRoom();
//                    }
//                });
//            } else {
//                createRoom();
//            }
//        } else {
//            createRoom();
//        }
        createRoom();
    }

    /**
     * 请求创建直播间接口，开始直播
     */
    private void createRoom() {
        if (mLiveClassID == 0) {
            ToastUtil.show("请选择频道");
            return;
        }
        String title = mEditTitle.getText().toString().trim();
        HttpUtil.createRoom(title, mLiveClassID, mLiveType, mLiveTypeVal, mAvatarFile, new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    L.e("开播", "createRoom------->" + info[0]);
                    ((LiveAnchorActivity) mContext).startLiveSuccess(info[0], mLiveType, mLiveTypeVal);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void release() {
        mImageUtil = null;
        mActivityResultCallback = null;
        mLiveTypeCallback = null;
    }
}
