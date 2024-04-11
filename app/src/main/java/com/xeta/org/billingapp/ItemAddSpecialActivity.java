package com.xeta.org.billingapp;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.Toast;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ItemAddSpecialActivity extends AppCompatActivity {
    Spinner spinner;
    AutoCompleteTextView name;
    EditText qty, rate, ratewhole, rateretail;
    ItemDB itemdb;
    String updatename;
    boolean isUpdating = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_special_item);
        name = (AutoCompleteTextView) findViewById(R.id.editTextSpecialName);
        qty = (EditText) findViewById(R.id.editTextSpecialQty);
        rate = (EditText) findViewById(R.id.editTextSpecialRate);
        ratewhole = (EditText) findViewById(R.id.editTextSpecialRateWholesale);
        rateretail = (EditText) findViewById(R.id.editTextSpecialRateRetail);
        itemdb = new ItemDB(this);
        name.setText("");
        qty.setText("1");

        String[] arrayQuantity = new String[] {
                "Kg", "g", "L", "ml", "piece"
        };

        spinner = (Spinner) findViewById(R.id.spinnerSpecialQty);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayQuantity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> itemadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, special_items());
        name.setAdapter(itemadapter);
        name.addTextChangedListener(nameWatcher);
    }

    public void addSpecialItemDB(View view) {
        boolean res;

        String item = name.getText().toString();
        item = capitalizeWord(item);
        String itemqty = qty.getText().toString();
        String itemrate = rate.getText().toString();
        String itemratewhole = ratewhole.getText().toString();
        String itemrateretail = rateretail.getText().toString();
        String itemtype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

        if (!item.isEmpty() && !itemqty.isEmpty() && !itemrate.isEmpty() && !itemratewhole.isEmpty() && !itemrateretail.isEmpty()) {

            switch (itemtype) {
                case "g":
                    float qt = (float) 1000 / Float.parseFloat(itemqty);
                    itemtype = "Kg";
                    float rt = Float.parseFloat(itemrate) * qt;
                    float rtw = Float.parseFloat(itemratewhole) * qt;
                    float rtr = Float.parseFloat(itemrateretail) * qt;

                    itemrate = Float.toString(rt);
                    itemratewhole = Float.toString(rtw);
                    itemrateretail = Float.toString(rtr);
                    break;
                case "ml":
                    float qt2 = 1000 / Float.parseFloat(itemqty);
                    itemtype = "L";
                    float rt2 = Float.parseFloat(itemrate) * qt2;
                    float rtw2 = Float.parseFloat(itemratewhole) * qt2;
                    float rtr2 = Float.parseFloat(itemrateretail) * qt2;
                    itemrate = Float.toString(rt2);
                    itemratewhole = Float.toString(rtw2);
                    itemrateretail = Float.toString(rtr2);
                    break;
            }
            if(!isUpdating) {

                res = itemdb.addSpecialItem(item, itemrate, itemratewhole, itemrateretail, itemtype);
                if (res) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Item Added Successfully")
                            .setTitle("Message")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Error Adding Item")
                            .setTitle("Message")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                }
                clearFields();
            } else {
                updateSpecial(item, itemqty, itemrate, itemratewhole, itemrateretail, itemtype);
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Incomplete Fields")
                    .setTitle("Error")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    private void updateSpecial(String item, String itemqty, String itemrate, String itemratewhole, String itemrateretail, String itemtype) {
        boolean res;
        res = itemdb.updateSpecialItem(updatename, item, itemrate, itemratewhole, itemrateretail, itemtype);
        if (res) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Item Updated Successfully")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error Updating Item")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            builder.show();
        }
        clearFields();

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
        return  items;
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
            int rateWholeCol = cursor.getColumnIndex("ratewhole");
            int rateRetailCol = cursor.getColumnIndex("rateretail");
            int typeCol = cursor.getColumnIndex("type");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String srate = cursor.getString(rateCol);
                String swrate = cursor.getString(rateWholeCol);
                String srrate = cursor.getString(rateRetailCol);
                String stype = cursor.getString(typeCol);
                rate.setText(srate);
                ratewhole.setText(swrate);
                rateretail.setText(srrate);

                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(stype);
                spinner.setSelection(spinnerPosition);
                updatename = itemname;
                isUpdating = true;
            }
        }
    };

    private void clearFields() {
        isUpdating = false;
        name.setText("");
        updatename = "";
        rate.setText("0.00");
        qty.setText("1");
    }
    public static String capitalizeWord(String str){
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }

    public void removeSpecialItemDB(View view) {
        final String ids = name.getText().toString();
        if(!ids.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Confirm Deletion")
                    .setTitle("Message")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            itemdb.deleteSpecialItem(ids);
                            Toast.makeText(ItemAddSpecialActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                            clearFields();
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
        else {
            Toast.makeText(this, "No Item", Toast.LENGTH_SHORT).show();
        }
    }
}
