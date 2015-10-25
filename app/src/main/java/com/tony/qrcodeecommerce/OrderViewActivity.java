package com.tony.qrcodeecommerce;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.MyOrder;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class OrderViewActivity extends ActionBarActivity {
    private static final String TAG = "OrderViewActivity";

    private AppSP appSP;

    private List<MyOrder> lists = new ArrayList<>();

    private ListView listView;
    private MyAdapter adapter;
    private Date today = new Date();
    //ProgressDialog
    private ProgressDialog PD;
    private ArrayList<MyDate> date = new ArrayList<>();

    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview);
        appSP = new AppSP(getApplicationContext());
        listView = (ListView)findViewById(R.id.listview);
        PD = ProgressDialog.show(this, "讀取中","向Server端更新數據中...",true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);

        date.add(new MyDate("一年以上", 9999));
        date.add(new MyDate("最近一年內", 365));
        date.add(new MyDate("最近三個月內",90));
        date.add(new MyDate("最近一個月內",30));
        date.add(new MyDate("最近一周內",7));
        date.add(new MyDate("最近三日內", 3));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc", "GetOrder");
                    params.put("acc", Tool.getStuNumber(appSP.getLoginUserId()));
                    String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                    JSONArray jsonArray = new JSONArray(responseMsg);
                    Log.i(TAG, "共有" + jsonArray.length() + "個訂單。");
                    for(int i=0;i<jsonArray.length();i++) {
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
                        for(int j=0;j<orderItemArr.length();j++) {
                            // 規格、數量、商品編號
                            Log.i(TAG,""+orderItemArr.getJSONObject(j).getString("spec"));
                            Log.i(TAG,""+orderItemArr.getJSONObject(j).getString("num"));
                            Log.i(TAG,""+orderItemArr.getJSONObject(j).getString("pid"));
                        }
                        String  header = null;
                        try {
                            Date bdd = sdf.parse(tupdate);
                            header = get_DateHeader((int) (datetime(bdd, today) / (60 * 60 * 24)));
                            Log.e(TAG,bdd.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (header!=null){
                            lists.add(new MyOrder(oid, oprice, orderItemArr, rname, rphone, remail, tplace, ttime, tupdate, situation,header));
                            Log.e(TAG,header);
                        }else{
                            lists.add(new MyOrder(oid, oprice, orderItemArr, rname, rphone, remail, tplace, ttime, tupdate, situation));
                        }

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new MyAdapter(getApplicationContext());
                            listView.setAdapter(adapter);
                            PD.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final class MyView {
        public TextView id;
        public TextView rname;
        public TextView oprice;
        public TextView tdate;
        public ImageView gonextbtn;
        public TextView header;
    }
    public class MyDate {
        private String date_name;
        private int day;
        private boolean used;

        public MyDate(String date_name, int day) {
            this.date_name = date_name;
            this.day = day;
            this.used = false;
        }


        public String getDate_name() {
            return date_name;
        }

        public void setDate_name(String date_name) {
            this.date_name = date_name;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }
    }
    public String  get_DateHeader(int day){
        int max =0;

        for(int i =0 ;i< date.size();i++){
           if(day < date.get(i).getDay() ){
              max = i;
           }
        }
        Log.e(TAG,String.valueOf(day) + "天");
        Log.e(TAG,String.valueOf(max));

        if(!date.get(max).isUsed()){
            date.get(max).setUsed(true);
            return date.get(max).getDate_name();
        }

        return null;
    }


    /**
     * 實作一個 Adapter 繼承 BaseAdapter
     */
    public class MyAdapter extends BaseAdapter  {
        private LayoutInflater inflater;

        final Long td=new Date().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //回傳這個 List 有幾個 item
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            // MyAdapter配置使用listview_cart的layout介面
            convertView = inflater.inflate(R.layout.listview_order, null);
            // new一個自訂View的class
            final MyView myviews = new MyView();
            // 指定元件
            myviews.id = (TextView) convertView.findViewById(R.id.id);
            myviews.rname = (TextView) convertView.findViewById(R.id.rname);
            myviews.oprice = (TextView) convertView.findViewById(R.id.oprice);
            myviews.tdate = (TextView) convertView.findViewById(R.id.tdate);
//            myviews.gonextbtn = (ImageView) convertView.findViewById(R.id.gonextbtn);
            myviews.header = (TextView)convertView.findViewById(R.id.header);
            // set listener
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainApplication.setMyOrder(lists.get(position));
                    Intent intent = new Intent(getApplicationContext(),OrderViewDetailActivity.class);
                    startActivity(intent);
                }
            });

            //set text
            myviews.id.setText(String.format(getResources().getString(R.string.orderview_oid), position + 1));
            myviews.rname.setText(String.format(getResources().getString(R.string.orderview_rname),lists.get(position).getRname()));
            myviews.oprice.setText(String.format(getResources().getString(R.string.orderview_oprice),lists.get(position).getOprice()));
            myviews.tdate.setText(String.format(getResources().getString(R.string.orderview_listview_date),lists.get(position).getTupdate().split(" ")[0]));

            if (lists.get(position).getDateGroup()!=null){
                myviews.header.setText(lists.get(position).getDateGroup());
                myviews.header.setVisibility(View.VISIBLE);
            }else{
                myviews.header.setVisibility(View.GONE);
            }



            Log.i(TAG,"oid:"+lists.get(position).getOid());
            return convertView;
        }



    }
    public static Long datetime(Date d1,Date d2)
    {
        Date dt1 = d1;
        Date dt2 = d2;
        Calendar calendar1 = new GregorianCalendar();
        Calendar calendar2 = new GregorianCalendar();
        calendar1.setTime(dt1);
        calendar2.setTime(dt2);
        Long time  =  (calendar2.getTimeInMillis()-calendar1.getTimeInMillis())/1000;
        return time;



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

    public class DateHighToLowComparator implements Comparator<MyOrder> {

        @Override
        public int compare(MyOrder lhs, MyOrder rhs) {

            Long Date1 = null;
            Long Date2 = null;

            if(lhs.getTupdate() != null && !"".equals(lhs.getTupdate())){

                try {
                    Date1 = sdf.parse(lhs.getTupdate()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            if(rhs.getTupdate() != null && !"".equals(rhs.getTupdate())){

                try {
                    Date2 = sdf.parse(rhs.getTupdate()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            if(Date1 > Date2){
                return -1;
            }
            else if(Date1 < Date2){
                return 1;
            }
            else{
                return 0;
            }
        }


    }
}
