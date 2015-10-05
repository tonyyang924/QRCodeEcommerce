package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginActivity extends ActionBarActivity {

    private Button submit;
    private EditText adminid,adminpw;
    private AppSP appSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        appSP = new AppSP(getApplicationContext());
        adminid = (EditText) findViewById(R.id.adminid);
        adminpw = (EditText) findViewById(R.id.adminpw);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("proc", "admin_login");
                            params.put("id", adminid.getText().toString());
                            params.put("pw", adminpw.getText().toString());
                            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                            params.put("token", token);

                            //成功
                            if (Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8").equals("success")) {
                                //設定成為管理員
                                appSP.setIsAdmin(true);
                                //儲存管理員登入帳號
                                appSP.setLoginUserId(adminid.getText().toString());
                                //Toast訊息，需調用runOnUiThread
                                AdminLoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "登入成功！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //進入主畫面
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //Toast訊息，需調用runOnUiThread
                                AdminLoginActivity.this.runOnUiThread(new Runnable() {
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
        });
    }
}
