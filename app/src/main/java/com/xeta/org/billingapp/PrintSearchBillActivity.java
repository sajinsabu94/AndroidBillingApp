package com.xeta.org.billingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Anonymous on 06/29/18.
 */

public class PrintSearchBillActivity extends AppCompatActivity {
    EditText s;
    Button b;
    ShopDB shopBill;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printbill);
        s = findViewById(R.id.editTextSearchBill);
        b = findViewById(R.id.buttonSearchBill);
        shopBill = new ShopDB(this);
    }

    public void searchBill(View view) {
        String name = s.getText().toString();

        if(name.equals("")){

        }
        else{
            Cursor newTable = shopBill.getBillInfoNew(name);
            newTable.moveToFirst();
            if(newTable == null || (newTable.getCount()<=0)){
                Cursor oldTable = shopBill.getBillInfo(name);
                oldTable.moveToFirst();
                if(oldTable == null || (oldTable.getCount()<=0)){
                    Toast.makeText(this, "Invalid Bill Number", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(this, OldBillInfoActivity.class);
                    intent.putExtra("billno", name);
                    startActivity(intent);
                }
            }
            else{
                Intent intent = new Intent(this, BillInfoActivity.class);
                intent.putExtra("billno", name);
                startActivity(intent);
            }
        }


    }
}
