package com.xeta.org.billingapp;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Anonymous on 04/27/18.
 */

public class BackupActivity extends AppCompatActivity {
    private static final String DB_NAME = "store";
    FileBackup fileBackup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        fileBackup = new FileBackup(this);

    }

    public void backupDB(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This will rewrite database. Continue?")
                .setTitle("Message")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileBackup.importDB();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();

    }

    public void restoreDB(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Backup your data")
                .setTitle("Message")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileBackup.exportDB();
                        finish();
                    }
                });
        builder.show();
    }
}
