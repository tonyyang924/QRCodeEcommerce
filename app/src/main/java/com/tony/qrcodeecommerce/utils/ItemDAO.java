package com.tony.qrcodeecommerce.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/* reference
 * https://github.com/macdidi5/AndroidTutorial
 * http://www.codedata.com.tw/mobile/android-tutorial-the-3rd-class-3-sqlite/
 */

public class ItemDAO {
    // 表格名稱
    public static final String TABLE_NAME = "shoppingcart";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其他表格欄位名稱
    public static final String PID_COLUMN = "pid";
    public static final String NAME_COLUMN = "name";
    public static final String PRICE_COLUMN = "price";
    public static final String PIC_COLUMN = "pic";
    public static final String PIC_LINK_COLUMN = "pic_link";
    public static final String LINK_COLUMN = "link";
    public static final String DATETIME_COLUMN = "datetime";
    public static final String NUMBER_COLUMN = "number";
    public static final String SPEC_COLUMN = "spec";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +   //primary key
                    PID_COLUMN + " TEXT NOT NULL, " +                   //string
                    NAME_COLUMN + " TEXT NOT NULL, " +                  //string
                    PRICE_COLUMN + " INTEGER NOT NULL, " +              //int
                    PIC_COLUMN + " TEXT NOT NULL, " +                   //string
                    PIC_LINK_COLUMN + " TEXT NOT NULL, " +              //string
                    LINK_COLUMN + " TEXT NOT NULL, " +                  //string
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +           //int
                    NUMBER_COLUMN + " INTEGER NOT NULL, " +             //int
                    SPEC_COLUMN + " TEXT NOT NULL)";                    //string

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public ItemDAO(Context context) {
        db = SQLiteDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Item insert(Item item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = CreateCV(item);
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        item.setId(id);
        // 回傳結果
        return item;
    }

    // 修改參數指定的物件
    public boolean update(Item item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = CreateCV(item);
        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    private ContentValues CreateCV(Item item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(PID_COLUMN, item.getPid());
        cv.put(NAME_COLUMN, item.getName());
        cv.put(PRICE_COLUMN, item.getPrice());
        cv.put(PIC_COLUMN, item.getPic());
        cv.put(PIC_LINK_COLUMN, item.getPic_link());
        cv.put(LINK_COLUMN, item.getLink());
        cv.put(DATETIME_COLUMN, item.getDatetime());
        cv.put(NUMBER_COLUMN, item.getNumber());
        cv.put(SPEC_COLUMN, item.getSpec());
        return cv;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // 刪除所有購物車資料
    public boolean deleteAll() {
        return db.delete(TABLE_NAME,null,null) > 0;
    }

    // 讀取所有購物車資料
    public List<Item> getAll() {
        List<Item> result = new ArrayList<Item>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public Item get(long id) {
        // 準備回傳結果用的物件
        Item item = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }

    // 把Cursor目前的資料包裝為物件
    public Item getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Item result = new Item();
        result.setId(cursor.getLong(0));
        result.setPid(cursor.getString(1));
        result.setName(cursor.getString(2));
        result.setPrice(cursor.getInt(3));
        result.setPic(cursor.getString(4));
        result.setPic_link(cursor.getString(5));
        result.setLink(cursor.getString(6));
        result.setDatetime(cursor.getLong(7));
        result.setNumber(cursor.getInt(8));
        result.setSpec(cursor.getString(9));
        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }

    // 確認是否有相同pid
    public boolean checkPid(Item item) {
        // 使用pid與spec為檢察條件
        String where = PID_COLUMN + "='" + item.getPid() + "' AND " + SPEC_COLUMN + "='" + item.getSpec() + "'";
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
        if (result.getCount() > 0)
            return true;
        return false;
    }
}
