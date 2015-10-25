package com.tony.qrcodeecommerce.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppSP {

    private static final String TAG = "AppSP";

    private Context context;

    //APP相關資訊
    private static SharedPreferences SP;
    private static final String APPDATA = "appdata";
    private static final String SCANPID_FIELD = "scanpid";
    private static final String LOGINUSERID_FIELD = "loginuserid";
    private static final String ISADMIN_FIELD = "isadmin";

    public AppSP(Context context) {
        this.context = context;
    }

    //取得剛剛掃描的商品編號
    public String getScanPid() {
        SP = context.getSharedPreferences(APPDATA, 0);
        return SP.getString(SCANPID_FIELD,"");
    }

    public void setScanPid(String pid) {
        SP = context.getSharedPreferences(APPDATA, 0);
        SP.edit()
                .putString(SCANPID_FIELD,pid).commit();
        Log.i(TAG,"剛掃描的商品已經更新");
    }

    //取得登入時的UserID
    public String getLoginUserId() {
        SP = context.getSharedPreferences(APPDATA, 0);
        return SP.getString(LOGINUSERID_FIELD,"");
    }

    public void setLoginUserId(String userid) {
        SP = context.getSharedPreferences(APPDATA, 0);
        SP.edit()
                .putString(LOGINUSERID_FIELD,userid).commit();
        Log.i(TAG, "已經儲存登入ID");
    }

    //取得是否為管理員
    public boolean getIsAdmin() {
        SP = context.getSharedPreferences(APPDATA, 0);
        return SP.getBoolean(ISADMIN_FIELD,false); //如果取得不到就是false否
    }

    public void setIsAdmin(boolean isadmin) {
        SP = context.getSharedPreferences(APPDATA, 0);
        SP.edit()
                .putBoolean(ISADMIN_FIELD,isadmin).commit();
        Log.i(TAG,"已經設定是否為管理員");
    }
}
