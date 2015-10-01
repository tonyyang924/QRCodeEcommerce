package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.Profile;
import com.tony.qrcodeecommerce.utils.ProfileSP;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends Activity {
    private static final String TAG = "ProfileActivity";
    private ProfileSP profileSP;
    private AppSP appSP;
    private EditText profileName,profilePhone,profileEmail;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileName = (EditText) findViewById(R.id.profile_name);
        profilePhone = (EditText)findViewById(R.id.profile_phone);
        profileEmail = (EditText)findViewById(R.id.profile_email);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(submitClk);
        profileSP = new ProfileSP(getApplicationContext());
        appSP = new AppSP(getApplicationContext());
        profileName.setText(profileSP.getUserProfile().getStuName());
        profilePhone.setText(profileSP.getUserProfile().getStuPhone());
        profileEmail.setText(profileSP.getUserProfile().getStuEmail());
    }

    private Profile getEditTextProfile() {
        return new Profile(appSP.getLoginUserId(),
                profileName.getText().toString(),
                profilePhone.getText().toString(),
                profileEmail.getText().toString());
    }

    private View.OnClickListener submitClk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileSP.setUserProfile(getEditTextProfile());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**
                         * 使用proc:user_update 更新user table的使用者資料
                         */
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("proc", "user_update");
                        params.put("acc", Tool.getStuNumber(appSP.getLoginUserId()));
                        params.put("phone", profilePhone.getText().toString());
                        params.put("name", profileName.getText().toString());
                        params.put("email", profileEmail.getText().toString());
                        final String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(responseMsg.equals("success")) {
                                    Log.i(TAG,"修改成功");
                                    Toast.makeText(getApplicationContext(),"修改成功！",Toast.LENGTH_SHORT).show();
                                } else if (responseMsg.equals("參數有誤")) {
                                    Log.i(TAG,"參數有誤");
                                    Toast.makeText(getApplicationContext(),"資料未填寫完整！",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
}
