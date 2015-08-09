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

    public static final String DATABASE_NAME = "thedb.db";
    //
    public static final int VERSION = 1;
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
        //
        db.execSQL(ItemDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"onUpgrade");
        //
        db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
        //
        onCreate(db);
    }
}
