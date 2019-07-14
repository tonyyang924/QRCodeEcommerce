package com.tony.qrcodeecommerce;

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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// http://stackoverflow.com/questions/15261088/gridview-with-two-columns-and-auto-resized-images
public class HomeActivity extends AppCompatActivity {
    int maxWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ListView listView = findViewById(R.id.main_list);
        maxWidth = listView.getWidth();
        Log.e("maxWidth", String.valueOf(maxWidth));
        listView.setAdapter(new MyAdapter(this));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            switch(position) {
                case 0:
                    intent.setClass(getApplicationContext(),ProfileActivity.class);
                    break;
                case 1:
                    intent.setClass(getApplicationContext(),MainActivity.class);
                    break;
                case 2:
                    intent.setClass(getApplicationContext(),OrderViewActivity.class);
                    break;
                case 3:
                    intent.setClass(getApplicationContext(),TeamActivity.class);
                    break;
            }
            startActivity(intent);
        });
    }

    class MyAdapter extends BaseAdapter {
        private final List<Item> mItems = new ArrayList<>();
        private final LayoutInflater mInflater;

        MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mItems.add(new Item("個人資料", R.drawable.b1));
            mItems.add(new Item("掃描商品", R.drawable.b2));
            mItems.add(new Item("訂單查看", R.drawable.b3));
            mItems.add(new Item("製作團隊", R.drawable.b4));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Item getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mItems.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;

            if (v == null) {
                v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);


            Item item = getItem(i);
            Log.e("item",String.valueOf(item.name));
            Log.e("item",String.valueOf(item.drawableId));
            picture.setImageResource(item.drawableId);
            Log.e("maxWidth", String.valueOf(maxWidth));
            Log.e("setMinimumHeight", String.valueOf(picture.getHeight()));
            picture.setMinimumHeight(maxWidth / 16 * 9);
            Log.e("setMinimumHeight", String.valueOf(picture.getHeight()));
            name.setText(item.name);

            return v;
        }

        private class Item {
            public final String name;
            final int drawableId;

            Item(String name, int drawableId) {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }

}
