package com.tony.qrcodeecommerce;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.MyOrder;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderViewActivity extends AppCompatActivity {
    private static final String TAG = OrderViewActivity.class.getSimpleName();

    private AppSP appSP;

    private List<MyOrder> lists = new ArrayList<>();

    private ListView listView;
    private MyAdapter adapter;
    private Date today = new Date();
    //ProgressDialog
    private ProgressDialog PD;
    private ArrayList<MyDate> date = new ArrayList<>();

    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview);
        appSP = new AppSP(getApplicationContext());
        listView = findViewById(R.id.listview);
        PD = ProgressDialog.show(this, "讀取中", "向Server端更新數據中...", true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);
        }

        date.add(new MyDate("一年以上", 9999));
        date.add(new MyDate("最近一年內", 365));
        date.add(new MyDate("最近三個月內", 90));
        date.add(new MyDate("最近一個月內", 30));
        date.add(new MyDate("最近一周內", 7));
        date.add(new MyDate("最近三日內", 3));

        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("proc", "GetOrder");
                params.put("acc", Tool.getStuNumber(appSP.getLoginUserId()));
                String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                JSONArray jsonArray = new JSONArray(responseMsg);
                Log.i(TAG, "共有" + jsonArray.length() + "個訂單。");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String oid = jsonArray.getJSONObject(i).getString("oid");
                    int oprice = Integer.valueOf(jsonArray.getJSONObject(i).getString("order_price"));
                    JSONArray orderItemArr = new JSONArray(jsonArray.getJSONObject(i).getString("order_item"));
                    String rname = jsonArray.getJSONObject(i).getString("receive_name");
                    String rphone = jsonArray.getJSONObject(i).getString("receive_phone");
                    String remail = jsonArray.getJSONObject(i).getString("receive_email");
                    String tplace = jsonArray.getJSONObject(i).getString("tplace");
                    String ttime = jsonArray.getJSONObject(i).getString("ttime");
                    String tupdate = jsonArray.getJSONObject(i).getString("tupdate");


                    int situation = Integer.valueOf(jsonArray.getJSONObject(i).getString("situation"));
                    for (int j = 0; j < orderItemArr.length(); j++) {
                        // 規格、數量、商品編號
                        Log.i(TAG, "" + orderItemArr.getJSONObject(j).getString("spec"));
                        Log.i(TAG, "" + orderItemArr.getJSONObject(j).getString("num"));
                        Log.i(TAG, "" + orderItemArr.getJSONObject(j).getString("pid"));
                    }
                    String header = null;
                    try {
                        Date bdd = sdf.parse(tupdate);
                        header = get_DateHeader((int) (datetime(bdd, today) / (60 * 60 * 24)));
                        Log.e(TAG, bdd.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (header != null) {
                        lists.add(new MyOrder(oid, oprice, orderItemArr, rname, rphone, remail, tplace, ttime, tupdate, situation, header));
                        Log.e(TAG, header);
                    } else {
                        lists.add(new MyOrder(oid, oprice, orderItemArr, rname, rphone, remail, tplace, ttime, tupdate, situation));
                    }

                }
                runOnUiThread(() -> {
                    adapter = new MyAdapter(getApplicationContext());
                    listView.setAdapter(adapter);
                    PD.dismiss();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private final class MyView {
        TextView id;
        TextView rname;
        TextView oprice;
        TextView tdate;
        TextView header;
    }

    public class MyDate {
        private String dateName;
        private int day;
        private boolean used;

        MyDate(String dateName, int day) {
            this.dateName = dateName;
            this.day = day;
            this.used = false;
        }


        String getDateName() {
            return dateName;
        }

        public void setDateName(String dateName) {
            this.dateName = dateName;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        boolean isUsed() {
            return used;
        }

        void setUsed(boolean used) {
            this.used = used;
        }
    }

    public String get_DateHeader(int day) {
        int max = 0;

        for (int i = 0; i < date.size(); i++) {
            if (day < date.get(i).getDay()) {
                max = i;
            }
        }
        Log.e(TAG, day + "天");
        Log.e(TAG, String.valueOf(max));

        if (!date.get(max).isUsed()) {
            date.get(max).setUsed(true);
            return date.get(max).getDateName();
        }

        return null;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.listview_order, null);
            final MyView myviews = new MyView();
            myviews.id = convertView.findViewById(R.id.id);
            myviews.rname = convertView.findViewById(R.id.rname);
            myviews.oprice = convertView.findViewById(R.id.oprice);
            myviews.tdate = convertView.findViewById(R.id.tdate);
            myviews.header = convertView.findViewById(R.id.header);
            convertView.setOnClickListener(v -> {
                MainApplication.setMyOrder(lists.get(position));
                Intent intent = new Intent(getApplicationContext(), OrderViewDetailActivity.class);
                startActivity(intent);
            });

            myviews.id.setText(String.format(getResources().getString(R.string.orderview_oid), "" + position + 1));
            myviews.rname.setText(String.format(getResources().getString(R.string.orderview_rname), "" + lists.get(position).getRname()));
            myviews.oprice.setText(String.format(getResources().getString(R.string.orderview_oprice), "" + lists.get(position).getOprice()));
            myviews.tdate.setText(String.format(getResources().getString(R.string.orderview_listview_date), lists.get(position).getTupdate().split(" ")[0]));

            if (lists.get(position).getDateGroup() != null) {
                myviews.header.setText(lists.get(position).getDateGroup());
                myviews.header.setVisibility(View.VISIBLE);
            } else {
                myviews.header.setVisibility(View.GONE);
            }

            Log.i(TAG, "oid:" + lists.get(position).getOid());
            return convertView;
        }
    }

    public static Long datetime(Date d1, Date d2) {
        Calendar calendar1 = new GregorianCalendar();
        Calendar calendar2 = new GregorianCalendar();
        calendar1.setTime(d1);
        calendar2.setTime(d2);
        return (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
