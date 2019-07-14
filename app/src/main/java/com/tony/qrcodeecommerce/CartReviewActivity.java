package com.tony.qrcodeecommerce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tony.qrcodeecommerce.utils.AsyncImageLoader;
import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;

import java.util.List;

public class CartReviewActivity extends AppCompatActivity {
    private List<Item> lists;
    private ItemDAO itemDAO;
    private TextView totalpriceTV;
    private AsyncImageLoader asyncImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartreview);
        asyncImageLoader = new AsyncImageLoader(getApplicationContext());
        ListView listView = findViewById(R.id.listView2);
        totalpriceTV = findViewById(R.id.totalpriceTV);
        itemDAO = new ItemDAO(this);
        getItemNumberNot0();
        refreshTotalPrice();
        MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        Button submit = findViewById(R.id.button);
        submit.setOnClickListener(submitClick);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);
        }
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
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getLimitNumber() == 0) {
                lists.remove(i);
            }
        }
    }

    private View.OnClickListener submitClick = v -> {
        //移至填寫個人資料頁面
        Intent intent = new Intent(CartReviewActivity.this, CartUserOrderActivity.class);
        startActivity(intent);
    };

    private final class MyView {
        ImageView itemImg;
        TextView itemName;
        TextView numberTv;
        TextView specTv;
        TextView priceTv;
        TextView subTotalTv;
    }

    // 實作一個 Adapter 繼承 BaseAdapter
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        MyAdapter(Context context) {
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
            myviews.itemImg = convertView.findViewById(R.id.imageView);
            myviews.itemName = convertView.findViewById(R.id.textView);
            myviews.numberTv = convertView.findViewById(R.id.numberTv);
            myviews.priceTv = convertView.findViewById(R.id.priceTv);
            myviews.subTotalTv = convertView.findViewById(R.id.subTotalTv);
            myviews.itemImg.setTag(lists.get(position).getPic_link());
            Bitmap bmp = asyncImageLoader.loadImage(myviews.itemImg, lists.get(position).getPic_link());
            myviews.itemImg.setImageBitmap(bmp);
            myviews.itemName.setText(lists.get(position).getName());
            myviews.numberTv.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.cart_number),
                            lists.get(position).getNumber())));
            if (lists.get(position).getPid().contains("A")) {
                myviews.specTv = convertView.findViewById(R.id.specTv);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                // do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
