package com.xeta.org.billingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Anonymous on 04/28/18.
 */

public class ChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_whole_retail);
    }

    public void chooseWholesale(View view) {
        Intent wholesale = new Intent(ChooseActivity.this, CartActivity.class);
        wholesale.putExtra("category", "wholesale");
        startActivity(wholesale);
    }

    public void chooseRetail(View view) {
        Intent retail = new Intent(ChooseActivity.this, CartActivity.class);
        retail.putExtra("category", "retail");
        startActivity(retail);
    }
}
