package com.tony.qrcodeecommerce;

import android.app.Application;

import com.tony.qrcodeecommerce.utils.ProductDAO;
import com.tony.qrcodeecommerce.utils.Tool;

public class MainApplication extends Application {
    //
    private static final String TAG = "MainApplication";

    //Server
    public static final String SERVER_PROC = "http://163.18.42.145/mobile/mobile_process.php";

    @Override
    public void onCreate() {
        super.onCreate();
        //剛進入App，Application會建立tool放置於MainApplication記憶體中
        Tool.CopyAssetsDBToSDCard(getApplicationContext());
        //剛進入App，將SV端的商品資料抓下來
        Tool.DownloadProductInfo(new ProductDAO(getApplicationContext()));
    }

}
