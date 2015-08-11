package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.Tool;

import java.util.HashMap;
import java.util.Map;

public class UserOrderDetails extends Activity {
    private static final String TAG = "UserOrderDetails";
    private String oid;
    private TextView usernameTV,userphoneTV,usermailTV,tplaceTV,ttimeTV,itemTV,sumTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userorderdetails);
        SetView();
//        Bundle bundle = getIntent().getExtras();
//        oid = bundle.getString("oid");
        oid="20150811171118MaJt100";
        DoThread();
    }
    private void SetView() {
        usernameTV = (TextView) findViewById(R.id.username_tv);
        userphoneTV = (TextView) findViewById(R.id.userphone_tv);
        usermailTV = (TextView) findViewById(R.id.usermail_tv);
        tplaceTV = (TextView) findViewById(R.id.tplace_tv);
        ttimeTV = (TextView) findViewById(R.id.ttime_tv);
        itemTV = (TextView) findViewById(R.id.item_tv);
        sumTv = (TextView)findViewById(R.id.sum);
    }
    private void DoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("oid", oid);
                    String resultData = Tool.submitPostData("http://mobile.dennychen.tw/mobile_details_select.php", params, "utf-8");
                    String[] result = resultData.split("\n");
                    String[] userArr = result[0].split(",");
                    String itemHtml = result[1];
                    String sum = result[2];
                    Log.i(TAG, "resultData:"+resultData);
                    Log.i(TAG, "userArr[0]:"+userArr[0]+"userArr[1]:"+userArr[1]);
                    Log.i(TAG, "itemHtml:"+itemHtml);

                    usernameTV.setText(String.format(getString(R.string.details_username), userArr[0]));
                    userphoneTV.setText(String.format(getString(R.string.details_userphone), userArr[1]));
                    usermailTV.setText(String.format(getString(R.string.details_usermail), userArr[2]));
                    tplaceTV.setText(String.format(getString(R.string.details_tplace),userArr[3]));
                    ttimeTV.setText(String.format(getString(R.string.details_ttime),userArr[4]));
                    itemTV.setText(Html.fromHtml(itemHtml));
                    sumTv.setText("總金額："+sum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
