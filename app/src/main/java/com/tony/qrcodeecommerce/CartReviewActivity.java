package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.List;

public class CartReviewActivity extends Activity {
    private static final String TAG = "CartReviewActivity";
    private Button submit;
    private ListView listView;
    private List<Item> lists;
    private ItemDAO itemDAO;
    private MyAdapter adapter;
    private MainApplication m;
    private TextView totalpriceTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartreview);
        m = (MainApplication) getApplication();
        listView = (ListView) findViewById(R.id.listView2);
        totalpriceTV = (TextView)findViewById(R.id.totalpriceTV);
        itemDAO = new ItemDAO(this);
        getItemNumberNot0();
        refreshTotalPrice();
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(submit_clklis);
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

    //取出list後，刪除數量為0的選項，不要讓數量為0的顯示在listview上
    private void getItemNumberNot0() {
        lists = itemDAO.getAll();
        for(int i=0;i<lists.size();i++) {
            if(lists.get(i).getLimitNumber() == 0) {
                lists.remove(i);
            }
        }
    }

    private View.OnClickListener submit_clklis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //移至填寫個人資料頁面
            Intent intent = new Intent(CartReviewActivity.this, CartUserOrderActivity.class);
            startActivity(intent);
        }
    };

    private final class MyView {
        public ImageView itemImg;
        public TextView itemName;
        public TextView numberTv;
        public TextView specTv;
        public TextView priceTv;
        public TextView subTotalTv;
    }

    // 實作一個 Adapter 繼承 BaseAdapter
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
            final MyView myviews = new MyView();
            convertView = inflater.inflate(R.layout.listview_cartreview, null);
            myviews.itemImg = (ImageView) convertView.findViewById(R.id.imageView);
            myviews.itemName = (TextView) convertView.findViewById(R.id.textView);
            myviews.numberTv = (TextView) convertView.findViewById(R.id.numberTv);
            myviews.priceTv = (TextView) convertView.findViewById(R.id.priceTv);
            myviews.subTotalTv = (TextView) convertView.findViewById(R.id.subTotalTv);
            String imgPath = Tool.QRCodeEcommercePath + "/images/" + lists.get(position).getPic();
            Bitmap bmp = BitmapFactory.decodeFile(imgPath);
            myviews.itemImg.setImageBitmap(bmp);
            myviews.itemName.setText(lists.get(position).getName());
            myviews.numberTv.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.cart_number),
                            lists.get(position).getNumber())));
            if(lists.get(position).getPid().indexOf("A") != -1) { //如果有A
                myviews.specTv = (TextView) convertView.findViewById(R.id.specTv);
                myviews.specTv.setVisibility(View.VISIBLE);
                myviews.specTv.setText(Html.fromHtml(
                        String.format(getResources().getString(R.string.cart_spec),
                                lists.get(position).getSpec())));
            }
            myviews.priceTv.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.cart_price),
                            lists.get(position).getPrice())));
            myviews.subTotalTv.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.cart_subtotal),
                            lists.get(position).getPrice() * lists.get(position).getNumber())));
            return convertView;
        }
    }
}
