package com.xeta.org.billingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ScanSpecialActivity extends AppCompatActivity {
    Spinner spinner;
    AutoCompleteTextView name;
    EditText rate,qty, rater;
    TextView total;
    ItemDB itemdb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_special);
        itemdb = new ItemDB(this);

        name = findViewById(R.id.nameSS);
        qty = (EditText) findViewById(R.id.qtySS);
        qty.addTextChangedListener(qtyWatcher);
        rate = (EditText) findViewById(R.id.rateSS);
        rate.setEnabled(false);
        rater = (EditText) findViewById(R.id.rateSSRetail);
        //rater.setEnabled(false);
        total = (TextView) findViewById(R.id.totalSS);

        String[] arrayQuantity = new String[] {
                "Kg", "g", "L", "ml", "piece"
        };
        spinner = (Spinner) findViewById(R.id.spinnerQty);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayQuantity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> itemadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, special_items());

        name.setAdapter(itemadapter);
        name.addTextChangedListener(nameWatcher);

    }

    private  String[] special_items () {

        int i=0;
        Cursor cursor = itemdb.getSpecialItemName();
        int nameCol = cursor.getColumnIndex("name");
        cursor.moveToFirst();
        String[] items = new String[cursor.getCount()];
        if(cursor != null && (cursor.getCount()> 0)){
            do {
                String id = cursor.getString(nameCol);
                items[i++] = id;
            }while (cursor.moveToNext());
        }
        return items;
    }

    private final TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            String itemname = name.getText().toString();

            Cursor cursor = itemdb.getSpecialItemDetails(itemname);

            int rateCol = cursor.getColumnIndex("rate");
            int rateColR = cursor.getColumnIndex("rateretail");
            int typeCol = cursor.getColumnIndex("type");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String srate = cursor.getString(rateCol);
                String srater = cursor.getString(rateColR);
                String stype = cursor.getString(typeCol);
                rate.setText(srate);
                rater.setText(srater);

                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(stype);
                spinner.setSelection(spinnerPosition);
                name.setEnabled(false);
                name.setFocusable(false);
                name.clearFocus();
                qty.setFocusable(true);
            }
        }
    };

    private final TextWatcher qtyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!name.getText().toString().isEmpty() && !rate.getText().toString().isEmpty() && !qty.getText().toString().isEmpty()) {
                String srater = rater.getText().toString();
                String sqnty = qty.getText().toString();
                String itemtype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                if(!sqnty.substring(sqnty.length()-1).equals('.')) {
                    if (srater.matches("\\d+(?:\\.\\d+)?") && sqnty.matches("\\d+(?:\\.\\d+)?")) {
                        float qt = Float.parseFloat(sqnty);
                        float rt = Float.parseFloat(srater);
                    /*
                    float total = (float) qt*rt;
                    textTotal.setText(String.valueOf(total));
                    */
                        float crate;
                        switch (itemtype) {
                            case "g":
                                crate = (float) (qt * rt) / 1000;
                                break;
                            case "ml":
                                crate = (float) (qt * rt) / 1000;
                                break;
                            default:
                                crate = (float) (qt * rt);
                        }
                        total.setText(String.valueOf(crate));
                    }
                }
            }
        }
    };

    public void addSpecialItem(View view) {
        String sname = name.getText().toString();
        String srate = rater.getText().toString();
        String test = rate.getText().toString();
        String sqty = qty.getText().toString();
        String stype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
        String stotal = total.getText().toString();

        if(!srate.isEmpty()&& !test.isEmpty() && !sqty.equals("")) {
            Intent back = new Intent();
            back.putExtra("name", sname);
            back.putExtra("rate", srate);
            back.putExtra("qty", sqty);
            back.putExtra("type", stype);
            back.putExtra("total", stotal);
            setResult(RESULT_OK, back);
            finish();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Item not available or Invald entry")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    public void cancelScanSpecial(View view) {
        finish();
    }
}
