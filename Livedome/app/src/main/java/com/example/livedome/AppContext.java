package com.example.livedome;

import android.app.Application;

import com.example.livedome.utils.L;
import com.umeng.commonsdk.UMConfigure;

import java.util.Timer;

import cn.tillusory.sdk.TiSDK;

public class AppContext extends Application {
    public static AppContext sInstance;
    private boolean mBeautyInited;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
    }
    public void initBeautySdk(String beautyKey) {
        if(AppConfig.TI_BEAUTY_ENABLE){
            if (!mBeautyInited) {
                mBeautyInited = true;
                TiSDK.init(beautyKey, this);
                L.e("萌颜初始化------->");
            }
        }
    }
}
