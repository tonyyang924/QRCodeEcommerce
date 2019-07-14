package com.tony.qrcodeecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText adminIdEt;
    private EditText adminPwEt;
    private AppSP appSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        appSP = new AppSP(this);
        adminIdEt = findViewById(R.id.adminid);
        adminPwEt = findViewById(R.id.adminpw);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("proc", "admin_login");
                params.put("id", adminIdEt.getText().toString());
                params.put("pw", adminPwEt.getText().toString());
                /**TODO: just workaround
                 InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                 String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                 GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                 params.put("token", token);
                 **/

                //成功
                if (Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8").equals("success")) {
                    //設定成為管理員
                    appSP.setIsAdmin(true);
                    //儲存管理員登入帳號
                    appSP.setLoginUserId(adminIdEt.getText().toString());
                    //Toast訊息，需調用runOnUiThread
                    AdminLoginActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "登入成功！", Toast.LENGTH_SHORT).show());
                    //進入主畫面
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //Toast訊息，需調用runOnUiThread
                    AdminLoginActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "帳號或密碼錯誤！", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start());
    }
}