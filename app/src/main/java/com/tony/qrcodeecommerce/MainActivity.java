package com.tony.qrcodeecommerce;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private ViewPager pager;
    private TabPagerAdapter pagerAdapter;
    public CartFragment cartFragment;
    public ContinuousCaptureFragment continuousCaptureFragment;
    public  DetailsFragment detailsFragment;
    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;


    // reference
    // http://dean-android.blogspot.tw/2015/01/androidfragmenttabactivitytab.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dm = getResources().getDisplayMetrics();

        pager =(ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        pagerAdapter = new TabPagerAdapter(this.getSupportFragmentManager());
        Log.i(TAG,String.valueOf(pagerAdapter.getCount()));
        pager.setAdapter(pagerAdapter);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("page selected", "" + position);
                tabs.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setTabsValue();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp);
//        //獲取TabHost控制元件
//        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//        //設定Tab頁面的顯示區域，帶入Context、FragmentManager、Container ID
//        mTabHost.setup(this, getSupportFragmentManager(), R.id.container);

        /**
         新增Tab結構說明 :
         首先帶入Tab分頁標籤的Tag資訊並可設定Tab標籤上顯示的文字與圖片，
         再來帶入Tab頁面要顯示連結的Fragment Class，最後可帶入Bundle資訊。
         **/

        //小黑人建立一個Tab，這個Tab的Tag設定為one，
        //並設定Tab上顯示的文字為第一堂課與icon圖片，Tab連結切換至
        //LessonOneFragment class，無夾帶Bundle資訊。

//        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabhost_menu, mTabHost.getTabWidget(), false);
//        ((TextView) tabIndicator.findViewById(R.id.text)).setText("掃描QRCode");
//        ((ImageView) tabIndicator.findViewById(R.id.img)).setImageResource(R.drawable.qrcodereader);

//        mTabHost.addTab(mTabHost.newTabSpec("qrcode")
//                .setIndicator("掃描QRCode", ContextCompat.getDrawable(this, R.drawable.qrcodereader))
//                , ContinuousCaptureFragment.class, null);
//
//        mTabHost.addTab(mTabHost.newTabSpec("detail")
//                .setIndicator("詳細資訊", ContextCompat.getDrawable(this, R.drawable.detail))
//                , DetailsFragment.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec("cart")
//                .setIndicator("購物車", ContextCompat.getDrawable(this, R.drawable.fullcartlight))
//                , CartFragment.class, null);

        /*
        for(int i=0;i<mTabHost.getTabWidget().getTabCount();i++) {
            TextView x = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            x.setTextSize(getResources().getDimension(R.dimen.main_tabhost_textsize));
        }
        */
    }

    public String getQRCodeText() {
        return "QR Code掃描頁面";
    }

    public String getDetailsText() {
        return "商品的詳細資訊頁面";
    }

    public String getCartText() {
        return "購物車頁面";
    }

    public class TabPagerAdapter extends FragmentPagerAdapter  {

        private final String[] TITLES = { "掃描QRCode", "詳細資訊", "購物車"};
        public TabPagerAdapter(FragmentManager fm) {
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
    private void setTabsValue() {
        //設置Tab的分隔線是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        //設置Tab底部線的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        //設置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        //設置Tab Indicator的顏色
        tabs.setIndicatorColor(Color.parseColor("#2a86ff"));
        tabs.setUnderlineColor(Color.parseColor("#CCCCCC"));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        tabs.setMinimumWidth(metrics.widthPixels/pagerAdapter.getCount());
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
