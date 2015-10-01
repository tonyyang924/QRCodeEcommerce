package com.tony.qrcodeecommerce.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* reference
 * https://github.com/macdidi5/AndroidTutorial
 * http://www.codedata.com.tw/mobile/android-tutorial-the-3rd-class-3-sqlite/
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteDBHelper";

    public static final String DATABASE_NAME = "database.db";

    /**
     * version1:
     * 只有購物車資料表
     * version2更改:
     * 1.不使用assets內的item.db與images
     * 2.新增一個名為product表格用來存放SV端的product
     * version3更改:
     * 增加spec(規格)與amount(剩餘數量)
     */
    public static final int VERSION = 3;

    //
    private static SQLiteDatabase database;

    //
    public static SQLiteDatabase getDatabase(Context context) {
        Log.i(TAG,"getDatabase");
        if (database == null || !database.isOpen()) {
            database = new SQLiteDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"onCreate");
        //建立用來儲存購物車的table
        db.execSQL(ItemDAO.CREATE_TABLE);
        //建立用來儲存商品的table
        db.execSQL(ProductDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"onUpgrade");
        //每當有更新時一律刪除資料表
        db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProductDAO.TABLE_NAME);
        //再create table
        onCreate(db);
    }
}
