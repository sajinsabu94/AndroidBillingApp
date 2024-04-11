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
 * Created by Anonymous on 04/26/18.
 */

public class LoginActivity extends AppCompatActivity {
    UserDB userDB;
    EditText pass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userDB = new UserDB(this);
        pass = findViewById(R.id.editTextPassword);
    }

    public void doLogin(View view) {
        String password = pass.getText().toString();
        pass.setText("");
        if(!password.equals("123456")){
            Cursor cs = userDB.readLogin(password);
            cs.moveToFirst();
            if(cs != null && (cs.getCount()> 0)){
                Intent home = new Intent(this, MainActivity.class);
                startActivity(home);
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Invalid Pin")
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
        else if(password.equals("123456")){
            userDB.updateUser("admin", "123456");
        }
    }
}
