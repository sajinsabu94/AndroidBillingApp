package com.xeta.org.billingapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anonymous on 04/26/18.
 */

public class UserDB extends SQLiteOpenHelper{
    private static final String DB_NAME = "store";
    SQLiteDatabase db;


    public UserDB(Context context) {
        super(context, DB_NAME, null, 2);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createItem = "CREATE TABLE items(id INTEGER PRIMARY KEY AUTOINCREMENT, barcode VARCHAR(100), name VARCHAR(100), rate VARCHAR(25), ratewhole VARCHAR(25), rateretail VARCHAR(25))";
        sqLiteDatabase.execSQL(createItem);

        String createSpecialItem = "CREATE TABLE items_special(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), rate VARCHAR(100), ratewhole VARCHAR(100), rateretail VARCHAR(100), type VARCHAR(25))";
        sqLiteDatabase.execSQL(createSpecialItem);

        String createShop = "CREATE TABLE shop(id INTEGER PRIMARY KEY AUTOINCREMENT, billno VARCHAR(50), items VARCHAR(200), qty VARCHAR(200), dattime DATETIME, amount VARCHAR(25))";
        sqLiteDatabase.execSQL(createShop);



        String createUser = "CREATE TABLE user(id VARCHAR(50), pass VARCHAR(50))";
        sqLiteDatabase.execSQL(createUser);

        String createExtra = "CREATE TABLE extra(name VARCHAR(50), amount VARCHAR(50))";
        sqLiteDatabase.execSQL(createExtra);

        ContentValues values = new ContentValues();
        values.put("id", "admin");
        values.put("pass", "admin1234");
        long insert = sqLiteDatabase.insert("user", null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String createBackup = "CREATE TABLE backup(id INTEGER PRIMARY KEY, billno VARCHAR(50), items VARCHAR(200), rate VARCHAR(100), qty VARCHAR(200), dattime DATETIME, amount VARCHAR(25))";
        if (i == 1 && i1 == 2)
            sqLiteDatabase.execSQL(createBackup);
    }
    public Cursor readLogin(String pass){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT pass FROM user WHERE pass= '"+pass+"' ", null);
        return cur;
    }
    public boolean updateUser(String original,  String pass){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pass", pass);
        long insert = db.update("user",values,"id = ?", new String[]{original});
        return insert>0;
    }
    public Cursor searchExtras(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT name FROM extra", null);
        return cur;
    }

    public Cursor searchExtrasName(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT sum(amount) as amt FROM extra WHERE name = '"+name+"'", null);
        return cur;
    }

    public boolean updateExtra(String n, String a) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", a);
        long insert = db.update("extra",values,"name = ?", new String[]{n});
        return insert>0;
    }

    public boolean insertExtra(String n, String a) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", n);
        values.put("amount", a);
        long insert = db.insert("extra", null, values);
        return insert>0;
    }
    public void deleteExtra(String name){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM extra WHERE name = '"+name+"'");
    }

}
