package com.xeta.org.billingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ItemDB extends SQLiteOpenHelper {
    public static final String DB_NAME = "store";

    public ItemDB(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createItem = "CREATE TABLE items(id INTEGER PRIMARY KEY AUTOINCREMENT, barcode VARCHAR(100), name VARCHAR(100), rate VARCHAR(25), ratewhole VARCHAR(25), rateretail VARCHAR(25))";
        sqLiteDatabase.execSQL(createItem);

        String createSpecialItem = "CREATE TABLE items_special(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), rate VARCHAR(100), ratewhole VARCHAR(100), rateretail VARCHAR(100), type VARCHAR(25))";
        sqLiteDatabase.execSQL(createSpecialItem);


        String createShop = "CREATE TABLE shop(id INTEGER PRIMARY KEY AUTOINCREMENT, billno VARCHAR(50), items VARCHAR(200), qty VARCHAR(200), dattime DATETIME, amount VARCHAR(25))";
        sqLiteDatabase.execSQL(createShop);
    }

    public boolean addItemWithBarcode(String barcode, String name, String rate, String ratew, String rater){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("barcode", barcode);
        values.put("name", name);
        values.put("rate", rate);
        values.put("ratewhole", ratew);
        values.put("rateretail", rater);
        long insert = db.insert("items", null, values);
        return insert>0;
    }

    public void deleteItemWithBarcode(String barcode){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM items WHERE barcode = "+barcode);
    }

    public void deleteSpecialItem(String item){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM items_special WHERE name = '"+item+"'");
    }

    public boolean addSpecialItem(String name, String rate, String ratewhole, String rateretail, String type){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("rate", rate);
        values.put("ratewhole", ratewhole);
        values.put("rateretail", rateretail);
        values.put("type", type);
        long insert = db.insert("items_special", null, values);
        return insert>0;
    }
    public boolean updateSpecialItem(String original,  String name, String rate, String ratewhole, String rateretail, String type){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name );
        values.put("rate", rate);
        values.put("ratewhole", ratewhole);
        values.put("rateretail", rateretail);
        values.put("type", type);
        long insert = db.update("items_special",values,"name = ?", new String[]{original});
        return insert>0;
    }

    public boolean updateItemWithBarcode(String barcode, String name, String rate, String ratew, String rater){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("barcode", barcode);
        values.put("name", name);
        values.put("rate", rate);
        values.put("ratewhole", ratew);
        values.put("rateretail", rater);
        long insert = db.update("items",values,"barcode = ?", new String[]{barcode});
        return insert>0;
    }


    public Cursor getItemWithBarcode(String barcode){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM items WHERE barcode = '"+barcode+"'", null);
        return cur;
    }


    public Cursor getSpecialItemName(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT name FROM items_special", null);
        return cur;
    }


    public Cursor getSpecialItemDetails(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM items_special WHERE name = '"+name+"'", null);
        return cur;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
