package com.example.livedome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.livedome.bean.ConfigBean;
import com.example.livedome.http.HttpCallback;
import com.example.livedome.http.HttpUtil;
import com.example.livedome.interfaces.CommonCallback;
import com.example.livedome.utils.SpUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LiveAnchorActivity.class));
            }
        });
//        getConfig();
    }

//    private void getConfig() {
//        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
//            @Override
//            public void callback(ConfigBean bean) {
//                if (bean != null) {
////                    AppContext.sInstance.initBeautySdk(bean.getBeautyKey());
//                    checkUidAndToken();
//                }
//            }
//        });
//    }
    private void checkUidAndToken() {
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(
                new String[]{SpUtil.UID, SpUtil.TOKEN});
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            HttpUtil.ifToken(uid, token, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        AppConfig.getInstance().setLoginInfo(uid, token, false);
//                        getBaseUserInfo();
                    }
                }
            });
        } else {
//            LoginActivity.forward();
            finish();
            //forwardMainActivity();
        }
    }
}
