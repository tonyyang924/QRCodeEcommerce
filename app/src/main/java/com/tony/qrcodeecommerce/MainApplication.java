package com.tony.qrcodeecommerce;

import android.app.Application;

import com.tony.qrcodeecommerce.utils.Tool;

public class MainApplication extends Application {
    //
    private static final String TAG = "MainApplication";

    //debug (測試時選true)
    public static final boolean DEBUG = true;

    //device token
    public static String token = "";

    //tool
    private Tool tool = null;

    //商品編號
    private String pid;

    //使用者帳號
    private String loginUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        //剛進入App，Application會建立tool放置於MainApplication記憶體中
        tool = new Tool();
        tool.CopyAssetsDBToSDCard(getApplicationContext());

        tool.getOrderId();
    }

    public Tool getTool() {
        return tool;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getLoginUserId() {
        return loginUserId;
    }

}
