package com.tony.qrcodeecommerce;

import android.app.Application;

import com.tony.qrcodeecommerce.utils.Tool;

public class MainApplication extends Application {
    //
    private static final String TAG = "MainApplication";

    //debug (測試時選true)
    public static final boolean DEBUG = false;

    //商品編號
    private static String pid;

    //使用者帳號
    private static String loginUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        //剛進入App，Application會建立tool放置於MainApplication記憶體中
        Tool.CopyAssetsDBToSDCard(getApplicationContext());
    }

    public static void setPid(String pid1) {
        pid = pid1;
    }

    public static String getPid() {
        return pid;
    }

    public static void setLoginUserId(String loginUserId1) {
        loginUserId = loginUserId1;
    }

    public static String getLoginUserId() {
        return loginUserId;
    }

}
