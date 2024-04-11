package com.xeta.org.billingapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Anonymous on 04/25/18.
 */

public class DailyActivity extends AppCompatActivity {
    CalendarView cv;
    ShopDB s;
    TextView t1,t2;
    String getDate;
    Long datel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        getDate = "";
        s = new ShopDB(this);
        t1 = findViewById(R.id.textView14);
        t2 = findViewById(R.id.textView16);
        cv = (CalendarView) findViewById(R.id.calendarView);
        datel = cv.getDate();
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
                if(cv.getDate() != date) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date datem = new Date(year-1900, month, date);
                    getDate = sdf.format(datem);
                    Log.e("Date : ", getDate);
                    Cursor cr = s.getBillInfoByDate(getDate);
                    int n1Col = cr.getColumnIndex("n1");
                    int n2Col = cr.getColumnIndex("n2");

                    cr.moveToFirst();

                    if (cr != null && (cr.getCount() > 0)) {
                        String name = cr.getString(n1Col);
                        String rate = cr.getString(n2Col);
                        t1.setText(name);
                        t2.setText(rate);
                    } else {
                        t1.setText("0");
                        t2.setText("0.00");
                    }
                }
            }
        });
    }

    public void viewDetails(View view) {
        Intent newIntent = new Intent(DailyActivity.this, ListBillActivity.class);
        newIntent.putExtra("date",getDate );
        startActivity(newIntent);
    }
}
