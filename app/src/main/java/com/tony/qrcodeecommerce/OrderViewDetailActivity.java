package com.tony.qrcodeecommerce;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tony.qrcodeecommerce.utils.AsyncImageLoader;
import com.tony.qrcodeecommerce.utils.MyOrder;

import org.json.JSONArray;
import org.json.JSONException;

public class OrderViewDetailActivity extends AppCompatActivity {

    private JSONArray jsonArray;
    private AsyncImageLoader asyncImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview_detail);

        MyOrder myOrder = MainApplication.getMyOrder();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);
            actionBar.setTitle(String.format(getResources().getString(R.string.orderview_oid), myOrder.getOid()));
        }

        asyncImageLoader = new AsyncImageLoader(getApplicationContext());

        TextView rname = findViewById(R.id.rname);
        TextView rphone = findViewById(R.id.rphone);
        TextView remail = findViewById(R.id.remail);
        TextView tplace = findViewById(R.id.tplace);
        TextView ttime = findViewById(R.id.ttime);
        TextView oprice = findViewById(R.id.oprice);
        TextView tupdate = findViewById(R.id.tupdate);
        ListView listView = findViewById(R.id.listview);

        jsonArray = myOrder.getOrderItemArr();
        rname.setText(String.format(getResources().getString(R.string.orderview_rname), myOrder.getRname()));
        rphone.setText(String.format(getResources().getString(R.string.orderview_rphone), myOrder.getRphone()));
        remail.setText(String.format(getResources().getString(R.string.orderview_remail), myOrder.getRemail()));
        tplace.setText(String.format(getResources().getString(R.string.orderview_tplace), myOrder.getTplace()));
        ttime.setText(String.format(getResources().getString(R.string.orderview_ttime), myOrder.getTtime()));
        oprice.setText(String.format(getResources().getString(R.string.orderview_oprice), "" + myOrder.getOprice()));
        tupdate.setText(String.format(getResources().getString(R.string.orderview_tupdate), myOrder.getTupdate()));

        MyAdapter myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
    }

    private final class MyView {
        TextView pid, spec, price, number;
        ImageView img;
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;

        MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyView myviews;

            int price = 0;
            String pid = null, spec = null, num = null, pic = null, pic_link = null;

            if (convertView == null) {
                myviews = new MyView();

                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.listview_orderdetail, null);
                myviews.pid = convertView.findViewById(R.id.pid);
                myviews.spec = convertView.findViewById(R.id.spec);
                myviews.price = convertView.findViewById(R.id.price);
                myviews.number = convertView.findViewById(R.id.number);
                myviews.img = convertView.findViewById(R.id.img);

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

            // 設定文字訊息
            myviews.pid.setText(String.format(getString(R.string.orderview_listview_pid), pid));
            myviews.spec.setText(String.format(getString(R.string.orderview_listview_spec), spec));
            myviews.price.setText(String.format(getString(R.string.orderview_listview_price), price));
            myviews.number.setText(String.format(getString(R.string.orderview_listview_number), num));

            if (pic_link != null && !pic_link.equals("")) {
                myviews.img.setTag(pic_link);
                myviews.img.setImageBitmap(asyncImageLoader.loadImage(myviews.img, pic_link));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
