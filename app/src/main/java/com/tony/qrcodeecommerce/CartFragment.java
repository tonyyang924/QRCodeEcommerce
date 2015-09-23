package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private ListView listView;
    private ItemDAO itemDAO;
    private MyAdapter adapter;
    private List<Item> lists;
    private Button submit;
    private TextView totalpriceTV;
    private RelativeLayout noItemLayout;

    //顯示文字內容
    private String text = "";

    //ProgressDialog
    private ProgressDialog PD;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //取得MainActivity的方法，將文字放入text字串
        MainActivity mMainActivity = (MainActivity) activity;
        text = mMainActivity.getCartText();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        return inflater.inflate(R.layout.activity_cart, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submit = (Button) getActivity().findViewById(R.id.button);
        submit.setOnClickListener(submitClkLis);
        listView = (ListView) getActivity().findViewById(R.id.listView);
        totalpriceTV = (TextView) getActivity().findViewById(R.id.totalpriceTV);
        noItemLayout = (RelativeLayout) getActivity().findViewById(R.id.noItemLayout);

        itemDAO = new ItemDAO(getActivity());
        lists = itemDAO.getAll();

        adapter = new MyAdapter(getActivity());
        listView.setAdapter(adapter);

        checkItemNumber();
        showNoItemLayout();
    }

    //向Server端確認目前的商品數量
    private void checkItemNumber() {
        //如果購物車內沒商品，就直接return離開此方法。
        if(lists.size()==0)
            return;
        //loading讀取show
        // activity 標題 內容 (true or false)
        PD = ProgressDialog.show(getActivity(), "讀取中","向Server端更新數據中...",true);
        /**
                * 包裝購物車內所有商品為JSONArray送至伺服器
                */
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<lists.size();i++) {
            JSONObject json = new JSONObject();
            try {
                json.put("pid", lists.get(i).getPid());
                json.put("spec", lists.get(i).getSpec());
                jsonArray.put(json);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        final String jsonArrayStr = jsonArray.toString();
        Log.i(TAG,"jsonArrayStr:"+jsonArrayStr);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlstr = "http://163.18.42.145/mobile/mobile_process.php";

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc", "product_search");
                    params.put("json", jsonArrayStr);
                    String responseMsg = Tool.submitPostData(urlstr, params, "utf-8");
                    //如果沒有error就是代表有回傳json格式的資料
                    if (!responseMsg.equals("error")) {
                        String sqlStr = "UPDATE shoppingcart SET limitnumber = CASE ";
                        Log.i(TAG,"responseMsg: "+responseMsg);
                        JSONArray jsonArrayResponse =  new JSONArray(responseMsg);
                        Log.i(TAG,"jsonArrayResponse:"+jsonArrayResponse.toString());
                        for(int i=0;i<jsonArrayResponse.length();i++) {
                            Log.i(TAG,"object ===> "+jsonArrayResponse.getJSONObject(i));
                            // 商品編號、規格、數量
                            String pid = jsonArrayResponse.getJSONObject(i).getString("pid");
                            String spec = jsonArrayResponse.getJSONObject(i).getString("spec");
                            // 該商品尺寸的數量，預設為0個
                            int amount = 0;
                            // 檢查如果會傳的值不是null就更改其尺寸數量
                            Log.i(TAG,"check amount:"+jsonArrayResponse.getJSONObject(i).getString("amount"));
                            if(!jsonArrayResponse.getJSONObject(i).getString("amount").equals("null")) {
                                //強制轉型
                                amount = Integer.valueOf(jsonArrayResponse.getJSONObject(i).getString("amount"));
                            }
                            Log.i(TAG,pid + " " + spec + " | amount:"+amount);
                            // SQL指令加上去
                            sqlStr += " WHEN pid='"+pid+"' AND spec='"+spec+"' THEN "+amount+" ";
                        }
                        sqlStr += " ELSE limitnumber END";
                        Log.i(TAG, "sqlStr:" + sqlStr);
                        // 下SQL指令更新資料庫
                        Cursor cursor = itemDAO.query(sqlStr);
                        cursor.moveToFirst();
                        cursor.close();
                        // List陣列清除
                        lists.clear();
                        // List取得購物車所有資料
                        lists = itemDAO.getAll();
                        //列出SQLite資料庫內的筆數
                        for (int i=0;i<lists.size();i++) {
                            Log.i(TAG,""+lists.get(i).getLimitNumber());
                        }
                        // 更新ListView
                        adapter.notifyDataSetChanged();
                        // 刷新總金額
                        refreshTotalPrice();
                        // 沒商品時顯示的介面
                        showNoItemLayout();
                        // 等購物車商品尺寸數量處理完成後，就關閉ProgressDialog
                        PD.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // BaseAdapter對應的View
    private final class MyView {
        public ImageView itemImg;
        public TextView itemName;
        public TextView numberTv;
        public TextView priceTv;
        public TextView subTotalTV;
        public ImageButton addNumberBtn;
        public ImageButton subNumberBtn;
        public TextView specTV;
        public ImageButton delBtn;
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
            convertView = inflater.inflate(R.layout.listview_cart, null);
            // new一個自訂View的class
            final MyView myviews = new MyView();
            // 指定元件
            myviews.itemImg = (ImageView) convertView.findViewById(R.id.imageView);
            myviews.itemName = (TextView) convertView.findViewById(R.id.textView);
            myviews.numberTv = (TextView) convertView.findViewById(R.id.NumberTv);
            myviews.priceTv = (TextView) convertView.findViewById(R.id.priceTV);
            myviews.subTotalTV = (TextView) convertView.findViewById(R.id.subTotalTV);
            myviews.addNumberBtn = (ImageButton) convertView.findViewById(R.id.addNumberBtn);
            myviews.subNumberBtn = (ImageButton) convertView.findViewById(R.id.subNumberBtn);
            myviews.specTV = (TextView) convertView.findViewById(R.id.specTV);
            myviews.specTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_spec),
                    lists.get(position).getSpec())));
            myviews.delBtn = (ImageButton) convertView.findViewById(R.id.delBtn);
            // set listener
            myviews.addNumberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = lists.get(position).getNumber();
                    Log.i(TAG,"最大數量:"+lists.get(position).getLimitNumber());
                    if (num + 1 <= lists.get(position).getLimitNumber())
//                    if (num + 1 <= 5)
                        num++;
                    lists.get(position).setNumber(num);
                    itemDAOUpdate(lists.get(position));
                    refreshItemValue(myviews, position);
                    refreshTotalPrice();
                }
            });
            myviews.subNumberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = lists.get(position).getNumber();
                    if (num - 1 > 0)
                        num--;
                    lists.get(position).setNumber(num);
                    itemDAOUpdate(lists.get(position));
                    refreshItemValue(myviews, position);
                    refreshTotalPrice();
                }
            });
            myviews.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(position);
                }
            });
            String imgPath = Tool.QRCodeEcommercePath + "/images/" + lists.get(position).getPic();
            Bitmap bmp = BitmapFactory.decodeFile(imgPath);
            myviews.itemImg.setImageBitmap(bmp);
            myviews.itemName.setText(lists.get(position).getName());
            refreshItemValue(myviews, position);
            refreshTotalPrice();
            return convertView;
        }
    }

    /**
         * 刷新Item的Value
         */
    private void refreshItemValue(MyView myviews, final int position) {
        //數量
        myviews.numberTv.setText("" + lists.get(position).getNumber());
        //價格
        myviews.priceTv.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_price),
                lists.get(position).getPrice())));
        //小計
        myviews.subTotalTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_subtotal),
                lists.get(position).getNumber() * lists.get(position).getPrice())));
    }

    /**
        * 刷新總金額
        * 將每筆Item的price*number後累加
        **/
    private void refreshTotalPrice() {
        //總金額
        int totalPrice = 0;
        for (Item list : lists) {
            totalPrice += list.getPrice() * list.getNumber();
        }
        totalpriceTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_totalprice),
                totalPrice)));
    }

    /**
        * 顯示沒商品的文案
        * 會判斷購物車內是否有商品
        * 當沒商品時會把noItemLayout設定為顯示狀態
        * 而[總金額]與[Submit]隱藏
        */
    private void showNoItemLayout() {
        if (lists.size() <= 0) {
            //顯示沒有項目的文案
            noItemLayout.setVisibility(View.VISIBLE);
            //隱藏總金額及按鈕
            totalpriceTV.setVisibility(View.INVISIBLE);
            submit.setVisibility(View.INVISIBLE);
        } else {
            //隱藏沒有項目的文案
            noItemLayout.setVisibility(View.INVISIBLE);
            //顯示總金額及按鈕
            totalpriceTV.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        }
    }

    /**
        * 刪除dialog
        */
    private void delDialog(final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示訊息")
                .setMessage("確定要將商品從購物車刪除嗎?")
                .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // SQLite資料庫刪除該筆資料
                        itemDAODelete(lists.get(position));
                        // List陣列清除
                        lists.clear();
                        // List取得購物車所有資料
                        lists = itemDAO.getAll();
                        // 更新ListView
                        adapter.notifyDataSetChanged();
                        // 刷新總金額
                        refreshTotalPrice();
                        // 沒商品時顯示的介面
                        showNoItemLayout();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .show();
    }

    private void itemDAOUpdate(Item item) {
        if (itemDAO.update(item))
            Log.i(TAG, "商品"+ item.getName()+"更新成功");
        else
            Log.i(TAG, "商品"+ item.getName()+"更新失敗");
    }

    private void itemDAODelete(Item item) {
        if (itemDAO.delete(item.getId()))
            Log.i(TAG, "商品"+ item.getName()+"刪除成功");
        else
            Log.i(TAG, "商品"+ item.getName()+"刪除失敗");
    }

    //結帳
    private View.OnClickListener submitClkLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CartReviewActivity.class);
            getActivity().startActivity(intent);
        }
    };
}
