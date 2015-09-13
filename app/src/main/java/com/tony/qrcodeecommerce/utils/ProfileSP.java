package com.tony.qrcodeecommerce.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileSP {
    private static final String TAG = "ProfileSP";

    //context
    private Context context;

    //SharedPreference 儲存使用者個人資料
    private SharedPreferences SP;
    private static final String DATA = "userdata";
    private static final String ID_FIELD = "stu_id";
    private static final String NAME_FIELD = "stu_name";
    private static final String PHONE_FIELD = "stu_phone";
    private static final String EMAIL_FIELD = "stu_email";

    //建構元
    public ProfileSP(Context context){
        this.context = context;
    }

    //取得使用者資料Data
    public Profile getUserProfile() {
        SP = context.getSharedPreferences(DATA, 0);
        return new Profile(SP.getString(ID_FIELD,""),SP.getString(NAME_FIELD,""),SP.getString(PHONE_FIELD,""),
                SP.getString(EMAIL_FIELD,""));
    }
    //設定使用者資料Data
    public void setUserProfile(Profile profile) {
        SP = context.getSharedPreferences(DATA, 0);
        SP.edit()
                .putString(ID_FIELD,profile.getStuId())
                .putString(NAME_FIELD,profile.getStuName())
                .putString(PHONE_FIELD,profile.getStuPhone())
                .putString(EMAIL_FIELD,profile.getStuEmail())
                .commit();
    }
    //設定使用者ID
    public void setUserId(String id) {
        SP = context.getSharedPreferences(DATA, 0);
        SP.edit()
                .putString(ID_FIELD, id).commit();
    }
}
