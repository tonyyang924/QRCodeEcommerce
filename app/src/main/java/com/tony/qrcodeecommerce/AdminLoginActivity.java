package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginActivity extends Activity {

    private Button submit;
    private EditText adminid,adminpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);
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
                            params.put("status", "login_check");
                            params.put("id", adminid.getText().toString());
                            params.put("pw", adminpw.getText().toString());
                            String urlstr = "http://mobile.dennychen.tw/mobile_admin_login.php";

                            //成功
                            if (Tool.submitPostData(urlstr, params, "utf-8").equals("success")) {
                                MainApplication.setIsAdmin(true);
                                MainApplication.setLoginUserId("qrcodeadmin");
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
