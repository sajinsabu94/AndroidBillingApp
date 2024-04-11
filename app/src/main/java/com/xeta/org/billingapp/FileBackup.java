package com.xeta.org.billingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Anonymous on 04/27/18.
 */

public class FileBackup extends SQLiteOpenHelper {
    public static String DB_FILEPATH = "/data/data/com.xeta.org.billingapp/databases/store.db";
    public FileBackup(Context context) {
        super(context, "store", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void importDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "com.xeta.org.billingapp"
                        + "//databases//" + "store";
                String backupDBPath  = "/store/store";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.e("Status",backupDB.toString());

            }
        } catch (Exception e) {

            Log.e("Status",e.toString());

        }
    }
    //exporting database
    public void exportDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "com.xeta.org.billingapp"
                        + "//databases//" + "store";
                String backupDBPath  = "/store/store";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.e("Status",backupDB.toString());

            }
        } catch (Exception e) {

            Log.e("Status",e.toString());

        }
    }
}