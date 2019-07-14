package com.tony.qrcodeecommerce;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tony.qrcodeecommerce.utils.AsyncImageLoader;
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
    private ProgressDialog progressDialog;
    private boolean isInit;
    private AsyncImageLoader asyncImageLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asyncImageLoader = new AsyncImageLoader(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);
        submit = view.findViewById(R.id.button);
        submit.setOnClickListener(submitClkLis);
        listView = view.findViewById(R.id.listView);
        totalpriceTV = view.findViewById(R.id.totalpriceTV);
        noItemLayout = view.findViewById(R.id.noItemLayout);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        itemDAO = new ItemDAO(getActivity());
        lists = itemDAO.getAll();

        checkItemNumber();
    }

    //向Server端確認目前的商品數量
    private void checkItemNumber() {
        Log.i(TAG, "checkItemNumber");
        //如果購物車內沒商品，就直接return離開此方法。
        if (lists.size() == 0) {
            // 沒商品時顯示的介面
            showNoItemLayout();
            return;
        }
        progressDialog = ProgressDialog.show(getActivity(), "讀取中", "向Server端更新數據中...", true);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < lists.size(); i++) {
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
        Log.i(TAG, "jsonArrayStr:" + jsonArrayStr);

        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("proc", "product_search");
                params.put("json", jsonArrayStr);
                String responseMsg = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");
                Log.i(TAG, "responseMsg:" + responseMsg);
                //如果沒有error就是代表有回傳json格式的資料
                if (!responseMsg.equals("error")) {
                    StringBuilder sqlStr = new StringBuilder("UPDATE shoppingcart SET limitnumber = CASE ");
                    Log.i(TAG, "responseMsg: " + responseMsg);
                    JSONArray jsonArrayResponse = new JSONArray(responseMsg);
                    Log.i(TAG, "jsonArrayResponse:" + jsonArrayResponse.toString());
                    for (int i = 0; i < jsonArrayResponse.length(); i++) {
                        Log.i(TAG, "object ===> " + jsonArrayResponse.getJSONObject(i));
                        // 商品編號、規格、數量
                        String pid = jsonArrayResponse.getJSONObject(i).getString("pid");
                        String spec = jsonArrayResponse.getJSONObject(i).getString("spec");
                        // 該商品尺寸的數量，預設為0個
                        int amount = 0;
                        // 檢查如果會傳的值不是null就更改其尺寸數量
                        Log.i(TAG, "check amount:" + jsonArrayResponse.getJSONObject(i).getString("amount"));
                        if (!jsonArrayResponse.getJSONObject(i).getString("amount").equals("null")) {
                            //強制轉型
                            amount = Integer.valueOf(jsonArrayResponse.getJSONObject(i).getString("amount"));
                        }
                        Log.i(TAG, pid + " " + spec + " | amount:" + amount);
                        // SQL指令加上去
                        sqlStr.append(" WHEN pid='").append(pid).append("' AND spec='").append(spec).append("' THEN ").append(amount).append(" ");
                    }
                    sqlStr.append(" ELSE limitnumber END");
                    Log.i(TAG, "sqlStr:" + sqlStr);
                    // 下SQL指令更新資料庫
                    Cursor cursor = itemDAO.query(sqlStr.toString());
                    cursor.moveToFirst();
                    cursor.close();
                    // List陣列清除
                    lists.clear();
                    // List取得購物車所有資料
                    lists = itemDAO.getAll();
                    Log.i(TAG, "list size:" + lists.size());
                    //列出SQLite資料庫內的筆數
                    for (int i = 0; i < lists.size(); i++) {
                        Log.i(TAG, "Pid:" + lists.get(i).getPid() + " Number:" + lists.get(i).getNumber() + " LimitNumber:" + lists.get(i).getLimitNumber());
                    }
                    getActivity().runOnUiThread(() -> {
                        // 刷新總金額
                        refreshTotalPrice();
                        // 配置ListView
                        adapter = new MyAdapter(getActivity());
                        listView.setAdapter(adapter);
                        // 等購物車商品尺寸數量處理完成後，就關閉ProgressDialog
                        progressDialog.dismiss();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private final class MyView {
        ImageView itemImg;
        TextView itemName;
        TextView numberTv;
        TextView priceTv;
        TextView subTotalTV;
        ImageButton addNumberBtn;
        ImageButton subNumberBtn;
        TextView specTV;
        ImageButton delBtn;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        MyAdapter(Context context) {
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
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.listview_cart, null);
            final MyView myviews = new MyView();
            myviews.itemImg = convertView.findViewById(R.id.imageView);
            myviews.itemName = convertView.findViewById(R.id.textView);
            myviews.numberTv = convertView.findViewById(R.id.NumberTv);
            myviews.priceTv = convertView.findViewById(R.id.priceTV);
            myviews.subTotalTV = convertView.findViewById(R.id.priceTV);
            myviews.addNumberBtn = convertView.findViewById(R.id.addNumberBtn);
            myviews.subNumberBtn = convertView.findViewById(R.id.subNumberBtn);
            myviews.specTV = convertView.findViewById(R.id.specTV);
            myviews.specTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_spec),
                    lists.get(position).getSpec())));
            myviews.delBtn = convertView.findViewById(R.id.delBtn);
            // set listener
            myviews.addNumberBtn.setOnClickListener(v -> {
                int num = lists.get(position).getNumber();
                if (num + 1 <= lists.get(position).getLimitNumber())
                    num++;
                lists.get(position).setNumber(num);
                refreshItemValue(myviews, position);
                refreshTotalPrice();
            });
            myviews.subNumberBtn.setOnClickListener(v -> {
                int num = lists.get(position).getNumber();
                if (num - 1 > 0)
                    num--;
                lists.get(position).setNumber(num);
                refreshItemValue(myviews, position);
                refreshTotalPrice();
            });
            myviews.delBtn.setOnClickListener(v -> delDialog(position));
            myviews.itemImg.setTag(lists.get(position).getPic_link());
            Bitmap bmp = asyncImageLoader.loadImage(myviews.itemImg, lists.get(position).getPic_link());
            myviews.itemImg.setImageBitmap(bmp);
            myviews.itemName.setText(lists.get(position).getName());
            if (lists.get(position).getLimitNumber() == 0) { // 該商品如果剩餘數量為0
                // gone去除 + , - 的按鈕
                myviews.addNumberBtn.setVisibility(View.GONE);
                myviews.subNumberBtn.setVisibility(View.GONE);
                if (!isInit) {
                    // 將此商品的數量設定為0並Update SQLite資料庫
                    lists.get(position).setNumber(0);
                    itemDAOUpdate(lists.get(position));
                    isInit = true;
                }
            } else { // 該商品不為0
                // 顯示商品數量增加減少的按鈕
                myviews.addNumberBtn.setVisibility(View.VISIBLE);
                myviews.subNumberBtn.setVisibility(View.VISIBLE);
                if (!isInit) {
                    // 將此商品的數量設定為1並Update SQLite資料庫
                    lists.get(position).setNumber(1);
                    itemDAOUpdate(lists.get(position));
                    isInit = true;
                }
            }
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
        if (lists.get(position).getLimitNumber() > 0) {
            myviews.numberTv.setText("" + lists.get(position).getNumber());
        } else {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 0, 0);
            myviews.numberTv.setLayoutParams(llp);
            myviews.numberTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.cart_listview_textsize) / getResources().getDisplayMetrics().density);
            myviews.numberTv.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_limitnumber), 0)));
        }
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
                .setPositiveButton("刪除", (dialog, which) -> {
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
                })
                .setNegativeButton("取消", (dialog, which) -> {
                }).show();
    }

    private void itemDAOUpdate(Item item) {
        if (itemDAO.update(item))
            Log.i(TAG, "商品" + item.getName() + "更新成功");
        else
            Log.i(TAG, "商品" + item.getName() + "更新失敗");
    }

    private void itemDAODelete(Item item) {
        if (itemDAO.delete(item.getId()))
            Log.i(TAG, "商品" + item.getName() + "刪除成功");
        else
            Log.i(TAG, "商品" + item.getName() + "刪除失敗");
    }

    //結帳
    private View.OnClickListener submitClkLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //改變資料庫內每個商品欲購買的數量
            String sqlStr = "UPDATE shoppingcart SET number = CASE ";
            for (int i = 0; i < lists.size(); i++) {
                sqlStr += " WHEN pid = '" + lists.get(i).getPid() + "' AND spec ='" + lists.get(i).getSpec() + "' THEN " + lists.get(i).getNumber();
            }
            sqlStr += " ELSE number END ";
            Cursor cursor = itemDAO.query(sqlStr);
            cursor.moveToFirst();
            cursor.close();
            //進入下個畫面
            Intent intent = new Intent(getActivity(), CartReviewActivity.class);
            getActivity().startActivity(intent);
        }
    };
}
