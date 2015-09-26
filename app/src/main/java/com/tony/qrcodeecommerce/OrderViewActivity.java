package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class OrderViewActivity extends Activity {
    private static final String TAG = "OrderViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc", "GetProduct");
                    String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                    JSONArray jsonArrayResponse =  new JSONArray(responseMsg);
                    Log.i(TAG, "jsonArrayResponse:" + jsonArrayResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
