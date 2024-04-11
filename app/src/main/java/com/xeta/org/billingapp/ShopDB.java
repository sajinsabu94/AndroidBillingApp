package com.xeta.org.billingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Anonymous on 04/24/18.
 */

public class ShopDB extends SQLiteOpenHelper {
    final static String DB_NAME = "store";
    SQLiteDatabase db;
    public ShopDB(Context context) {
        super(context, DB_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean addTransactionToDB(String billno, String items, String qty, String datetime, String total){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("billno", billno);
        values.put("items", items);
        values.put("qty", qty);
        values.put("dattime", datetime);
        values.put("amount", total);
        long insert = db.insert("shop", null, values);

        getWritableDatabase().execSQL("delete from shop where id not in (SELECT MIN(id) FROM shop GROUP BY billno)");
        return insert>0;
    }
    public boolean addTransactionToBackupDB(String billno, String items, String rates, String qty, String datetime, String total){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("billno", billno);
        values.put("items", items);
        values.put("rate", rates);
        values.put("qty", qty);
        values.put("dattime", datetime);
        values.put("amount", total);
        long insert = db.insert("backup", null, values);

        getWritableDatabase().execSQL("delete from backup where id not in (SELECT MIN(id) FROM backup GROUP BY billno)");
        return insert>0;
    }
    public int getMaxBillId(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT billno FROM shop WHERE id=(SELECT MAX(id) from shop)", null);
        int billCol = cursor.getColumnIndex("billno");
        int no = 1000;
        if(cursor != null && cursor.moveToFirst()) {
            String bill = cursor.getString(billCol);
            no = Integer.parseInt(bill);
        }
        cursor.close();
        return no;
    }

    public Cursor getBillInfo(String billno){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT items,qty,dattime,amount FROM shop WHERE billno = '"+billno.trim()+"'",null);
        return c;
    }
    public Cursor getBillInfoNew(String billno){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT items,rate,qty,dattime,amount FROM backup WHERE billno = '"+billno.trim()+"'",null);
        return c;
    }

    public Cursor getBillInfoByDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count(billno) as n1,sum(amount) as n2 FROM shop WHERE date(dattime) = '"+date+"'",null);
        return c;
    }

    public Cursor getFullBillInfoByDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT billno FROM shop WHERE date(dattime) = '"+date+"'",null);
        return c;
    }

    public Cursor getBillInfoByMonth(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count(billno) as n1,sum(amount) as n2 FROM shop WHERE strftime('%Y-%m',dattime) = '"+date+"'",null);
        return c;
    }

}
