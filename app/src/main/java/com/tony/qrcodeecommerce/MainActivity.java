package com.tony.qrcodeecommerce;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public CartFragment cartFragment;
    public ContinuousCaptureFragment continuousCaptureFragment;
    public  DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);
        }

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(this.getSupportFragmentManager());

        ViewPager viewpager = findViewById(R.id.viewpager);
        viewpager.setAdapter(pagerAdapter);
        viewpager.setCurrentItem(0);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewpager);
    }

    public String getQRCodeText() {
        return "QR Code掃描頁面";
    }

    public String getDetailsText() {
        return "商品的詳細資訊頁面";
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "掃描QRCode", "詳細資訊", "購物車"};
        TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }


        @Override
        public Fragment getItem(int position) {
            Log.e(TAG, "getItem=" + position);
            switch (position) {
                case 0:
                    if (continuousCaptureFragment == null) {
                        continuousCaptureFragment = new ContinuousCaptureFragment();
                    }

                    return continuousCaptureFragment;
                case 1:
                    if (detailsFragment == null) {
                        detailsFragment = new DetailsFragment();
                    }
                    return detailsFragment;
                case 2:
                    if (cartFragment == null) {
                        cartFragment = new CartFragment();
                    }
                    return cartFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public DetailsFragment getDetailsFragment(){
        return detailsFragment;
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
