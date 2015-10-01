package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class OrderViewActivity extends Activity {
    private static final String TAG = "OrderViewActivity";

    private AppSP appSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview);
        appSP = new AppSP(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc", "GetOrder");
                    params.put("acc", Tool.getStuNumber(appSP.getLoginUserId()));
                    String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                    Log.i(TAG,"responseMsg:"+responseMsg);
                    JSONArray jsonArray = new JSONArray(responseMsg);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONArray jsonArray2 = new JSONArray(jsonArray.getJSONObject(i).getString("oid"));
                        Log.i(TAG,""+jsonArray2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
