package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.MyOrder;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderViewActivity extends Activity {
    private static final String TAG = "OrderViewActivity";

    private AppSP appSP;

    private List<MyOrder> lists = new ArrayList<>();

    private ListView listView;
    private MyAdapter adapter;

    //ProgressDialog
    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview);
        appSP = new AppSP(getApplicationContext());
        listView = (ListView)findViewById(R.id.listview);
        PD = ProgressDialog.show(this, "讀取中","向Server端更新數據中...",true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc", "GetOrder");
                    params.put("acc", Tool.getStuNumber(appSP.getLoginUserId()));
                    String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                    JSONArray jsonArray = new JSONArray(responseMsg);
                    Log.i(TAG,"共有"+jsonArray.length()+"個訂單。");
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
                        lists.add(new MyOrder(oid,oprice,orderItemArr,rname,rphone,remail,tplace,ttime,tupdate,situation));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new MyAdapter(getApplicationContext());
                                listView.setAdapter(adapter);
                                PD.dismiss();
                            }
                        });
                    }
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
        public ImageView gonextbtn;
    }

    /**
     * 實作一個 Adapter 繼承 BaseAdapter
     */
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

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
            myviews.gonextbtn = (ImageView) convertView.findViewById(R.id.gonextbtn);

            // set listener
            myviews.gonextbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainApplication.setMyOrder(lists.get(position)); //放到orderList中
                    Intent intent = new Intent(getApplicationContext(),OrderViewDetailActivity.class);
                    startActivity(intent);
                }
            });

            //set text
            myviews.id.setText(String.format(getResources().getString(R.string.orderview_id), position + 1));
            myviews.rname.setText(String.format(getResources().getString(R.string.orderview_rname),lists.get(position).getRname()));
            myviews.oprice.setText(String.format(getResources().getString(R.string.orderview_oprice),lists.get(position).getOprice()));

            Log.i(TAG,"oid:"+lists.get(position).getOid());
            return convertView;
        }
    }
}
