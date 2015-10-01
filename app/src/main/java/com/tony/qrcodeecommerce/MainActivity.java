package com.tony.qrcodeecommerce;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    // reference
    // http://dean-android.blogspot.tw/2015/01/androidfragmenttabactivitytab.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //獲取TabHost控制元件
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //設定Tab頁面的顯示區域，帶入Context、FragmentManager、Container ID
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container);

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

        mTabHost.addTab(mTabHost.newTabSpec("qrcode")
                .setIndicator("掃描QRCode", ContextCompat.getDrawable(this, R.drawable.qrcodereader))
                , ContinuousCaptureFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("detail")
                .setIndicator("詳細資訊", ContextCompat.getDrawable(this, R.drawable.detail))
                , DetailsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("cart")
                .setIndicator("購物車", ContextCompat.getDrawable(this, R.drawable.fullcartlight))
                , CartFragment.class, null);

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
}
