package com.xeta.org.billingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Anonymous on 04/23/18.
 */

public class CartActivity extends AppCompatActivity {
    TableLayout productTable;
    TextView bill;
    String category;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        Intent getVal = getIntent();
        category = getVal.getStringExtra("category");
        bill = (TextView) findViewById(R.id.BillTotal);
        bill.setText("0.00");
        productTable = (TableLayout) findViewById(R.id.myTableLayout);
        Log.e("Category ",category);
    }

    public void scanItem(View view) {
        if(category.equals("retail")) {
            Intent second = new Intent(this, ScanActivity.class);
            startActivityForResult(second, 0);
        }
        else{
            Intent second = new Intent(this, ScanWholeActivity.class);
            startActivityForResult(second, 0);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String qty = data.getStringExtra("qty");
                String rate = data.getStringExtra("rate");
                String total = data.getStringExtra("total");

                TableRow product = new TableRow(this);

                TextView tname = new TextView(this);
                TextView tqty = new TextView(this);
                TextView trate = new TextView(this);
                TextView ttotal = new TextView(this);
                ImageView del = new ImageView(this);
                del.setClickable(true);
                del.setImageResource(R.drawable.ic_delete_black_18dp);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final TableRow row = (TableRow) view.getParent();
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setMessage("Do you want to remove the item?")
                                .setTitle("Warning")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        productTable.removeView(row);
                                        generateTotal();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.show();
                    }
                });

                tname.setText(name.substring(0, Math.min(name.length(), 22)));
                tqty.setText(qty);
                trate.setText(rate);
                ttotal.setText(total);

                product.addView(tname);
                product.addView(trate);
                product.addView(tqty);
                product.addView(ttotal);
                product.addView(del);
                product.setPadding(0,10,0,10);

                productTable.addView(product);
                generateTotal();
            }
        }
        if(requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String qty = data.getStringExtra("qty");
                String rate = data.getStringExtra("rate");
                String type = data.getStringExtra("type");
                String total = data.getStringExtra("total");

                TableRow product = new TableRow(this);

                TextView tname = new TextView(this);
                TextView tqty = new TextView(this);
                TextView trate = new TextView(this);
                TextView ttotal = new TextView(this);
                ImageView del = new ImageView(this);
                del.setClickable(true);
                del.setImageResource(R.drawable.ic_delete_black_18dp);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final TableRow row = (TableRow) view.getParent();
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setMessage("Do you want to remove the item?")
                                .setTitle("Warning")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        productTable.removeView(row);
                                        generateTotal();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.show();
                    }
                });

                tname.setText(name.substring(0, Math.min(name.length(), 22)));
                tqty.setText(qty+type);

                float f2 = Float.parseFloat(rate);
                String s2 = String.format("%.2f",f2);
                trate.setText(s2);

                float f = Float.parseFloat(total);
                String s = String.format("%.2f",f);
                ttotal.setText(s);



                product.addView(tname);
                product.addView(trate);
                product.addView(tqty);
                product.addView(ttotal);
                product.addView(del);
                product.setPadding(0,10,0,10);
                productTable.addView(product);
                generateTotal();
            }
        }
    }

    public void addClickSpecial(View view) {
        if(category.equals("retail")) {
            Intent second = new Intent(this, ScanSpecialActivity.class);
            startActivityForResult(second, 1);
        }
        else {
            Intent second = new Intent(this, ScanSpecialWholeActivity.class);
            startActivityForResult(second, 1);
        }
    }

    public void proceedPay(View view) {
        String amt  = bill.getText().toString();
        float rf = Float.valueOf(amt);
        int r = Math.round(rf);
        float pay = r/(float)1.0;
        if(Float.valueOf(amt)==0.0){
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setMessage("Cart is empty")
                    .setTitle("Warning")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            builder.show();
        }
        else {
            Intent proceed = new Intent(this, ProceedBillActivity.class);
            String items[] = new String[(productTable.getChildCount() - 1) * 4];
            int n = 0;
            for (int i = 1; i < productTable.getChildCount(); i++) {
                View child = productTable.getChildAt(i);

                if (child instanceof TableRow) {
                    TableRow row = (TableRow) child;
                    for (int x = 0; x < 4; x++) {
                        TextView temp = (TextView) row.getChildAt(x);
                        items[n++] = temp.getText().toString();
                    }
                }
            }
            proceed.putExtra("values", items);
            proceed.putExtra("billamnt", amt);
            proceed.putExtra("billamntpay", Float.toString(pay));
            proceed.putExtra("count", productTable.getChildCount());
            startActivity(proceed);
        }
    }
    private void generateTotal(){
        float sum=0;
        for (int i = 1; i < productTable.getChildCount(); i++) {
            View child = productTable.getChildAt(i);
            TableRow row = (TableRow) child;
            TextView temp = (TextView)row.getChildAt(3);
            float t = Float.parseFloat(temp.getText().toString());
            sum+=t;
        }
        bill.setText(String.valueOf(sum));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
