package com.tony.qrcodeecommerce;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.AsyncImageLoader;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserOrderDetailsActivity extends ActionBarActivity {
    private static final String TAG = "UserOrderDetailsActivity";
    private String oid;
    private TextView receiveNameTV, receivePhoneTV, receiveEmailTV,tplaceTV,ttimeTV, totalPriceTv;
    private ListView listView;
    private AsyncImageLoader asyncImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userorderdetails);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);

        asyncImageLoader = new AsyncImageLoader(getApplicationContext());
        SetView();
        Bundle bundle = getIntent().getExtras();
        oid = bundle.getString("oid");
//        oid = "20151021171302unYq795";
        DoThread();
    }
    private void SetView() {
        receiveNameTV = (TextView) findViewById(R.id.receivename_tv);
        receivePhoneTV = (TextView) findViewById(R.id.receivephone_tv);
        receiveEmailTV = (TextView) findViewById(R.id.receiveemail_tv);
        tplaceTV = (TextView) findViewById(R.id.tplace_tv);
        ttimeTV = (TextView) findViewById(R.id.ttime_tv);
        totalPriceTv = (TextView)findViewById(R.id.totalprice_tv);
        listView = (ListView) findViewById(R.id.listView);
    }
    private void DoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc","GetOrderInfo");
                    params.put("oid", oid);
                    String resultData = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                    JSONObject resultJSONObject = new JSONObject(resultData);
                    final String receive_name = resultJSONObject.getString("receive_name");
                    final String receive_phone = resultJSONObject.getString("receive_phone");
                    final String receive_email = resultJSONObject.getString("receive_email");
                    final String tplace = resultJSONObject.getString("tplace");
                    final String ttime = resultJSONObject.getString("ttime");
                    final int totalPrice = resultJSONObject.getInt("price");
                    final JSONArray itemJSONArray = new JSONArray(resultJSONObject.getString("order_item"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            receiveNameTV.setText(String.format(getString(R.string.details_username), receive_name));
                            receivePhoneTV.setText(String.format(getString(R.string.details_userphone), receive_phone));
                            receiveEmailTV.setText(String.format(getString(R.string.details_useremail), receive_email));
                            tplaceTV.setText(String.format(getString(R.string.details_tplace), tplace));
                            ttimeTV.setText(String.format(getString(R.string.details_ttime), ttime));
                            totalPriceTv.setText(String.format(getString(R.string.details_totalprice), totalPrice));
                            MyAdapter myAdapter = new MyAdapter(getApplicationContext(), itemJSONArray);
                            listView.setAdapter(myAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final class MyView {
        public TextView pid,spec,price,number;
        public ImageView img;
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private JSONArray jsonArray;
        public MyAdapter(Context context,JSONArray jsonArray) {
            this.context = context;
            this.jsonArray = jsonArray;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //回傳這個 List 有幾個 item
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            try {
                return jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            MyView myviews = null;

            int price = 0;
            String pid = null, spec = null, num = null, pic = null, pic_link = null;

            if(convertView == null) {
                myviews = new MyView();

                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.listview_orderdetail, null);
                myviews.pid = (TextView) convertView.findViewById(R.id.pid);
                myviews.spec = (TextView) convertView.findViewById(R.id.spec);
                myviews.price = (TextView) convertView.findViewById(R.id.price);
                myviews.number = (TextView) convertView.findViewById(R.id.number);
                myviews.img = (ImageView) convertView.findViewById(R.id.img);

                convertView.setTag(myviews);
            } else {
                myviews = (MyView) convertView.getTag();
            }


            // 解析jsonArray
            try {
                pid = jsonArray.getJSONObject(position).getString("pid");
                spec = jsonArray.getJSONObject(position).getString("spec");
                num = jsonArray.getJSONObject(position).getString("num");
                price = jsonArray.getJSONObject(position).getInt("price");
                pic = jsonArray.getJSONObject(position).getString("pic");
                pic_link = jsonArray.getJSONObject(position).getString("pic_link");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 設定文字顏色(因為不設定時會呈現白色)
            myviews.pid.setTextColor(Color.BLACK);
            myviews.spec.setTextColor(Color.BLACK);
            myviews.price.setTextColor(Color.BLACK);
            myviews.number.setTextColor(Color.BLACK);

            // 設定文字訊息
            myviews.pid.setText(String.format(getString(R.string.orderview_listview_pid),pid));
            myviews.spec.setText(String.format(getString(R.string.orderview_listview_spec),spec));
            myviews.price.setText(String.format(getString(R.string.orderview_listview_price),price));
            myviews.number.setText(String.format(getString(R.string.orderview_listview_number),num));

            if(pic_link != null && !pic_link.equals("")) {
//                myviews.img.setDefaultImageResId(R.drawable.qrcodereader);
//                myviews.img.setErrorImageResId(R.drawable.qrcodereader);
//                myviews.img.setImageUrl(pic_link, imageLoader);
                myviews.img.setTag(pic_link);
                myviews.img.setImageBitmap(asyncImageLoader.loadImage(myviews.img,pic_link));
            }

            return convertView;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
