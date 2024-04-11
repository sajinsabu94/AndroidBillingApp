package com.xeta.org.billingapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anonymous on 04/25/18.
 */

public class MonthlyActivity extends AppCompatActivity {
    CalendarView cv;
    ShopDB s;
    TextView t1,t2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);
        s = new ShopDB(this);
        t1 = findViewById(R.id.textView141);
        t2 = findViewById(R.id.textView161);
        cv = (CalendarView) findViewById(R.id.calendarView1);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                Date datem = new Date(year-1900, month, date);
                String str = sdf.format(datem);
                Log.e("Date : ",str);
                Cursor cr = s.getBillInfoByMonth(str);

                int n1Col = cr.getColumnIndex("n1");
                int n2Col = cr.getColumnIndex("n2");

                cr.moveToFirst();

                if(cr != null && (cr.getCount()> 0)){
                    String name = cr.getString(n1Col);
                    String rate = cr.getString(n2Col);
                    t1.setText(name);
                    t2.setText(rate);
                }
                else{
                    t1.setText("0");
                    t2.setText("0.00");
                }
            }
        });
    }
}
