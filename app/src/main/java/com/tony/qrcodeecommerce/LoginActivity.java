package com.tony.qrcodeecommerce;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tony.qrcodeecommerce.gcm.RegistrationIntentService;
import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.ProfileSP;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private EditText userId, userPw;
    private ProfileSP profileSP;
    private AppSP appSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);

        profileSP = new ProfileSP(getApplicationContext());
        appSP = new AppSP(getApplicationContext());
        userId = findViewById(R.id.userid);
        userPw = findViewById(R.id.userpw);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            if (!checkNetworkConnected()) {
                Toast.makeText(this, "請檢查網路狀態", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    try {
                        String token = null;
                        /**TODO: just workaround
                        try {
                            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                         **/

                        Map<String, String> params = new HashMap<>();
                        params.put("proc", "login_check");
                        params.put("acc", userId.getText().toString());
                        params.put("pwd", userPw.getText().toString());
                        params.put("devicetoken", token);
                        //回傳訊息
                        String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");

                        //成功
                        if (responseMsg.equals("success")) {
                            //設定不是管理員
                            appSP.setIsAdmin(false);
                            //儲存登入帳號
                            String userloginId;
                            if (!userId.getText().toString().contains("u")) { //如果沒有u
                                userloginId = "u" + userId.getText().toString();
                            } else {
                                userloginId = userId.getText().toString();
                            }
                            appSP.setLoginUserId(userloginId);
                            profileSP.setUserId(userloginId);
                            if (profileSP.getUserProfile().getStuEmail().equals("")) {
                                Log.i(TAG, "email == \"\"");
                                profileSP.setUserEmail(String.format(getString(R.string.email_nkfust), userloginId));
                            }
                            //Toast訊息，需調用runOnUiThread
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "登入成功！", Toast.LENGTH_SHORT).show());
                            //進入主畫面
                            enterNextPage();
                        }
                        //帳號密碼錯誤
                        else if (responseMsg.equals("fails")) {
                            //Toast訊息，需調用runOnUiThread
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "帳號或密碼錯誤！", Toast.LENGTH_SHORT).show());
                        }
                        //系統發生問題
                        else if (responseMsg.equals("error")) {
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "系統發生問題！", Toast.LENGTH_SHORT).show());
                        }
                        //此帳號被禁止進入
                        else if (responseMsg.equals("forbidden")) {
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "此帳號被禁止進入！", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        if (checkPlayServices()) {
            Log.i(TAG, "have playservices");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Log.i(TAG, "no playservices");
        }

        //取得device token (要在背景作業取得)
        /**TODO:JUST workaround for now
        new Thread(() -> {
            try {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "token: " + token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        **/

    }

    //偵測是否有連到網路上
    private boolean checkNetworkConnected() {
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (CM != null) {
            NetworkInfo info = CM.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                Log.d(TAG, "[目前連線方式]" + info.getTypeName());
                Log.d(TAG, "[目前連線狀態]" + info.getState());
                Log.d(TAG, "[目前網路是否可使用]" + info.isAvailable());
                Log.d(TAG, "[網路是否已連接]" + info.isConnected());
                Log.d(TAG, "[網路是否已連接 或 連線中]" + info.isConnectedOrConnecting());
                Log.d(TAG, "[網路目前是否有問題 ]" + info.isFailover());
                Log.d(TAG, "[網路目前是否在漫遊中]" + info.isRoaming());
                return info.isAvailable();
            }
        }
        return false;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_admin) {
            Log.e(TAG, String.valueOf(item.getItemId()));
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
}
