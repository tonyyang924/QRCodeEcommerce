package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;

public class OrderViewActivity extends Activity {
    private static final String TAG = "OrderViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview);
    }
}
