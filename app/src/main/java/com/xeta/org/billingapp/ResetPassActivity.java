package com.xeta.org.billingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Anonymous on 04/27/18.
 */

public class ResetPassActivity extends AppCompatActivity {
    EditText p1,p2,p3;
    UserDB udb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        p1 = findViewById(R.id.editTextPass1);
        p2 = findViewById(R.id.editTextPass2);
        p3 = findViewById(R.id.editTextPass3);
        udb = new UserDB(this);

    }

    public void resetPass(View view) {
        String t1 = p1.getText().toString();
        String t2 = p2.getText().toString();
        String t3 = p3.getText().toString();

        Cursor cs = udb.readLogin(t1);
        cs.moveToFirst();
        if(cs != null && (cs.getCount()> 0)){
            if(t2.equals(t3)) {
                if(udb.updateUser("admin", t2)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Password updated Successfully")
                            .setTitle("Info")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Error updating password")
                            .setTitle("Error")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                }

            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Password mismatch")
                        .setTitle("Warning")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                builder.show();
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Enter Password Correctly")
                    .setTitle("Error")
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }
}
