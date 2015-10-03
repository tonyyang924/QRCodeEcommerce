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
        Bundle bundle = getIntent().getExtras();
        oid = bundle.getString("oid");
//        oid="20150811171118MaJt100";
//        DoThread();
    }
    private void SetView() {
        usernameTV = (TextView) findViewById(R.id.username_tv);
        userphoneTV = (TextView) findViewById(R.id.userphone_tv);
        usermailTV = (TextView) findViewById(R.id.usermail_tv);
        tplaceTV = (TextView) findViewById(R.id.tplace_tv);
        ttimeTV = (TextView) findViewById(R.id.ttime_tv);
        itemTV = (TextView) findViewById(R.id.item_tv);
        sumTv = (TextView)findViewById(R.id.sum);

        usernameTV.setText("收貨人姓名：Tony");
        userphoneTV.setText("收貨人電話：0912345678");
        usermailTV.setText("收貨人電子信箱：u0324813@nkfust.edu.tw");
        tplaceTV.setText("交易地點：管理學院（Ｃ棟）");
        ttimeTV.setText("交易時間：2015 年 10 月 3 日 17 點 01 分");
        itemTV.setText(Html.fromHtml("<table>" +
                "<tr><td>編號</td><td>尺寸</td><td>價格</td><td>數量</td></tr>" +
                "<tr><td>A01</td><td>S</td><td>$350</td><td>1</td></tr>" +
                "<tr><td>A07</td><td>S</td><td>$399</td><td>1</td></tr>" +
                "</table>"));
        sumTv.setText("總金額：749元");
    }
    private void DoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc","details_select");
                    params.put("oid", oid);
                    String resultData = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                    String[] result = resultData.split("\n");
                    final String[] userArr = result[0].split(",");
                    final String itemHtml = result[1];
                    final String sum = result[2];
                    Log.i(TAG, "resultData:" + resultData);
                    Log.i(TAG, "userArr[0]:" + userArr[0] + "userArr[1]:" + userArr[1]);
                    Log.i(TAG, "itemHtml:" + itemHtml);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            usernameTV.setText(String.format(getString(R.string.details_username), userArr[0]));
//                            userphoneTV.setText(String.format(getString(R.string.details_userphone), userArr[1]));
//                            usermailTV.setText(String.format(getString(R.string.details_usermail), userArr[2]));
//                            tplaceTV.setText(String.format(getString(R.string.details_tplace), userArr[3]));
//                            ttimeTV.setText(String.format(getString(R.string.details_ttime), userArr[4]));
//                            itemTV.setText(Html.fromHtml(itemHtml));
//                            sumTv.setText("總金額：" + sum);

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
