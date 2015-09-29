package com.tony.qrcodeecommerce.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.tony.qrcodeecommerce.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Tool {
    //TAG
    private static final String TAG = "QRCodeEcommerce::Tool";
    //專案於sd card的path
    public static final String QRCodeEcommercePath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/QRCodeEcommerce";
    //
    private static SQLiteDatabase sqLiteDB = null;

    public static SQLiteDatabase getSQLiteDB() {
        sqLiteDB = SQLiteDatabase.openOrCreateDatabase(getSQLiteDatabaseFile(), null);
        return sqLiteDB;
    }

    public Cursor SQLQuery(String query) {
        SQLiteDatabase db = getSQLiteDB();
        return db.rawQuery(query, null);
    }

    //複製assets內的資料到SD Card
    public static void CopyAssetsDBToSDCard(Context context) {
        File myDataPath = new File(QRCodeEcommercePath);

        //如果資料夾不存在，就建立資料夾
        if (!myDataPath.exists())
            myDataPath.mkdir();

        //如果資料夾存在，才進行以下動作
        if (myDataPath.exists()) {
            Log.i(TAG, "如果資料夾存在，才進行以下動作");
            try {
                //取得AssetManager
                AssetManager assetManager = context.getAssets();
                InputStream fis;

                //1.複製assets內的SQLite到外部的儲存空間
                //  假如沒有外部儲存空間，那就複製到手機App底下的database
                fis = assetManager.open("item.db");
                fileStreamWrite(fis, getSQLiteDatabaseFile());

                //2.複製assets中images資料夾內的所有商品圖片
                if (!new File(QRCodeEcommercePath + "/images").exists())
                    new File(QRCodeEcommercePath + "/images").mkdir();
                for (String fileName : assetManager.list("images")) {
                    if (fileName.endsWith(".jpg")) {
                        Log.i(TAG, "fileName:" + fileName);

                        fis = assetManager.open("images/" + fileName);
                        fileStreamWrite(fis, new File(QRCodeEcommercePath + "/images/" + fileName));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "CopyAssetsDBToSDCard===結束===");
    }

    //儲存檔案，輸入InputStream & target save file
    private static void fileStreamWrite(InputStream fis, File file) {
        try {
            FileOutputStream fos;
            BufferedOutputStream dest;
            byte[] buffer = new byte[8192];
            int count;

            fos = new FileOutputStream(file);
            dest = new BufferedOutputStream(fos, 8192);
            while ((count = fis.read(buffer, 0, 8192)) >= 0)
                dest.write(buffer, 0, count);

            dest.flush();
            dest.close();
            fos.close();
        } catch (Exception e) {
        }
    }

    //取得SQLiteDatabase的位置
    public static File getSQLiteDatabaseFile() {
        File dbfile = null;
        File myDataPath = new File(QRCodeEcommercePath);
        if (!myDataPath.exists()) myDataPath.mkdirs();
        if (!myDataPath.exists()) {
            (new File("/data/data/com.tony.qrcodeecommerce/databases")).mkdirs();
            dbfile = new File("/data/data/com.tony.qrcodeecommerce/databases/main.db");
        } else
            dbfile = new File(myDataPath.getAbsolutePath() + "/main.db");
        return dbfile;
    }

    /*
    * Function  :   發送Post請求到伺服器
    * Param     :   params請求體內容，encode編碼格式
    * Author    :   博客園-依舊淡然
    */
    public static String submitPostData(String urlstr,Map<String, String> params, String encode) throws Exception{
        URL url = new URL(urlstr);
        byte[] data = getRequestData(params, encode).toString().getBytes(); //獲得請求體
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);          //設置連接逾時時間
            httpURLConnection.setDoInput(true);                 //打開輸入流，以便從伺服器獲取資料
            httpURLConnection.setDoOutput(true);                //打開輸出流，以便向伺服器提交資料
            httpURLConnection.setRequestMethod("POST");         //設置以Post方式提交資料
            httpURLConnection.setUseCaches(false);              //使用Post方式不能使用緩存
            //設置請求體的類型是文本類型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //設置請求體的長度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //獲得輸出流，向伺服器寫入資料
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //獲得伺服器的回應碼
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //處理伺服器的回應結果
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
     * Function  :   封裝請求體資訊
     * Param     :   params請求體內容，encode編碼格式
     * Author    :   博客園-依舊淡然
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存儲封裝好的請求體資訊
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //刪除最後的一個"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   處理伺服器的回應結果（將輸入流轉化成字串）
     * Param     :   inputStream伺服器的回應輸入流
     * Author    :   博客園-依舊淡然
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存儲處理結果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    //產生訂單編號 (年月日驗證碼亂數)
    public static String getOrderId(Context context,String veritycode) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        String oid = String.format(context.getResources().getString(R.string.oid_text),str,veritycode,(int)Math.random()*900+100);
        return oid;
    }

    // 檢查學生學號，去除學號的"u"後回傳數字學號
    public static String getStuNumber(String loginId) {
        String uNumber;
        /**
         * 假設學號為u0324813
         * 在這的判斷只判斷登入id是否有u
         * 有u就把u去除
         */
        if(loginId.indexOf("u") != -1) { //如果登入字串中有"u"
            uNumber = loginId.substring(1); //從char[1]開始
        } else { //如果沒有
            uNumber = loginId;
        }
        return uNumber;
    }
}

