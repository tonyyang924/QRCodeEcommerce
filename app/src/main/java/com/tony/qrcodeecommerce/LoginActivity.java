package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.tony.qrcodeecommerce.gcm.RegistrationIntentService;
import com.tony.qrcodeecommerce.utils.ProfileSP;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private EditText userId, userPw;
    private Button submit;
    private ImageButton adminLoginButton;
    private ProfileSP profileSP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        profileSP = new ProfileSP(getApplicationContext());
        userId = (EditText) findViewById(R.id.userid);
        userPw = (EditText) findViewById(R.id.userpw);
        adminLoginButton = (ImageButton) findViewById(R.id.adminLoginButton);
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,AdminLoginActivity.class);
                startActivity(intent);
            }
        });
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainApplication.DEBUG) {
                    //進入主畫面
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("status", "login_check");
                                params.put("acc", userId.getText().toString());
                                params.put("pwd", userPw.getText().toString());

                                String urlstr = "http://163.18.42.145/login/index.php";

                                //成功
                                if (Tool.submitPostData(urlstr, params, "utf-8").equals("success")) {
                                    //設定不是管理員
                                    MainApplication.setIsAdmin(false);
                                    //儲存登入帳號
                                    String userloginId = "";
                                    if(userId.getText().toString().indexOf("u") == -1) { //如果沒有u
                                        userloginId = "u"+userId.getText().toString();
                                    } else {
                                        userloginId = userId.getText().toString();
                                    }
                                    MainApplication.setLoginUserId(userloginId);
                                    profileSP.setUserId(userloginId);
                                    if(profileSP.getUserProfile().getStuEmail().equals("")) {
                                        Log.i(TAG,"email == \"\"");
                                        profileSP.setUserEmail(String.format(getString(R.string.email_nkfust),userloginId));
                                    }
                                    //Toast訊息，需調用runOnUiThread
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "登入成功！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //進入主畫面
                                    enterNextPage();
                                }
                                //失敗
                                else if (Tool.submitPostData(urlstr, params, "utf-8").equals("fails")) {
                                    //Toast訊息，需調用runOnUiThread
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "帳號或密碼錯誤！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        if (checkPlayServices()) {
            Log.i(TAG, "have playservices");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Log.i(TAG,"no playservices");
        }

        //取得device token (要在背景作業取得)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(TAG,"token: "+token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void enterNextPage() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
