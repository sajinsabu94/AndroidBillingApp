package com.xeta.org.billingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Anonymous on 04/28/18.
 */

public class ListBillActivity extends AppCompatActivity {
    //ShopDB s;
    ListView lv;
    ShopDB shopBill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bill);
        shopBill = new ShopDB(this);
        //s = new ShopDB(this);
        lv = findViewById(R.id.listBill);
        Intent getI = getIntent();
        String dt = getI.getStringExtra("date");
        ArrayAdapter<String> list = new ArrayAdapter<String>(this, android.R.layout.activity_list_item, android.R.id.text1, generateBill(dt));
        lv.setAdapter(list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item  = (String) lv.getItemAtPosition(i);

                Cursor newTable = shopBill.getBillInfoNew(item);
                newTable.moveToFirst();
                if(newTable == null || (newTable.getCount()<=0)){
                    Cursor oldTable = shopBill.getBillInfo(item);
                    oldTable.moveToFirst();
                    if(oldTable == null || (oldTable.getCount()<=0)){
                        Toast.makeText(ListBillActivity.this, "Invalid Bill Number", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(ListBillActivity.this, OldBillInfoActivity.class);
                        intent.putExtra("billno", item);
                        startActivity(intent);
                    }
                }
                else{
                    Intent intent = new Intent(ListBillActivity.this, BillInfoActivity.class);
                    intent.putExtra("billno", item);
                    startActivity(intent);
                }
            }
        });
    }

    private ArrayList<String> generateBill(String getDate) {
        ArrayList<String> mylist = new ArrayList<String>();
        Cursor cr = shopBill.getFullBillInfoByDate(getDate);
        int n1Col = cr.getColumnIndex("billno");

        cr.moveToFirst();
        int i=0;
        if(cr != null && (cr.getCount()> 0)){
            do {
                String name = cr.getString(n1Col);
                mylist.add(name);
            }while(cr.moveToNext());
        }
        return mylist;
    }
}
