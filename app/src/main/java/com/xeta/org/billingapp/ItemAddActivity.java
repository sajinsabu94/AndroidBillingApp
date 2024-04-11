package com.xeta.org.billingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ItemAddActivity extends AppCompatActivity {
    EditText txtQr, txtName, txtRate, txtRateWhole, txtRateRetail;
    SurfaceView itemQR;
    CameraSource cameraSource;
    ItemDB itemDB;
    String updatename;
    boolean isUpdating = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        itemDB = new ItemDB(this);
        txtQr = (EditText) findViewById(R.id.editTextQR);
        txtQr.setEnabled(false);
        txtQr.addTextChangedListener(qrWatcher);
        txtName = (EditText) findViewById(R.id.editTextName);
        txtRate = (EditText) findViewById(R.id.editTextRate);
        txtRateWhole = (EditText) findViewById(R.id.editTextRateWhole);
        txtRateRetail = (EditText) findViewById(R.id.editTextRateRetail);

        txtName.setText("");
        txtRate.setText("");
        txtRateWhole.setText("");
        txtRateRetail.setText("");
        txtQr.setText("");


        itemQR = (SurfaceView) findViewById(R.id.surfaceViewAdd);
        itemQR.setZOrderMediaOverlay(true);
        createCameraSource();
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        if(!barcodeDetector.isOperational()){
            Toast.makeText(this, "Error! Not operational", Toast.LENGTH_SHORT).show();
        }
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(22.0f)
                .build();
        itemQR.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ItemAddActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(itemQR.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if (barcodes.valueAt(0).rawValue.matches("\\d+(?:\\.\\d+)?")) {
                        txtQr.post(new Runnable() {
                            public void run() {
                                txtQr.setText(
                                        barcodes.valueAt(0).rawValue
                                );
                            }
                        });
                    }
                }
            }
        });
    }

    public void addItemToDB(View view) {
        boolean res;
        if (!txtName.getText().toString().isEmpty() && !txtRate.getText().toString().isEmpty()) {
            if(!isUpdating) {
                if (!txtQr.getText().toString().equals("")) {
                    String barcode = txtQr.getText().toString();
                    String name = txtName.getText().toString();
                    name = capitalizeWord(name);
                    String rate = txtRate.getText().toString();
                    String ratew = txtRateWhole.getText().toString();
                    String rater = txtRateRetail.getText().toString();
                    res = itemDB.addItemWithBarcode(barcode, name, rate, ratew, rater);
                    if(res) {
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
                    }
                    else{
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Please add the product in Special category")
                            .setTitle("Message")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                    finish();
                }

            }else {
                updateItem();
            }
            clearFields();
        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Enter Name and Rate")
                    .setTitle("Error")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    private void clearFields() {
        isUpdating = false;
        txtQr.setText("");
        txtName.setText("");
        txtRate.setText("");
        updatename = "";
    }

    private void updateItem() {
        boolean  res;
        if (!txtQr.getText().toString().equals("")) {
            String barcode = txtQr.getText().toString();
            String name = txtName.getText().toString();
            name = capitalizeWord(name);
            String rate = txtRate.getText().toString();
            String ratew = txtRateWhole.getText().toString();
            String rater = txtRateRetail.getText().toString();
            res = itemDB.updateItemWithBarcode(barcode, name, rate, ratew, rater);
            if(res) {
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
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Error updating item")
                        .setTitle("Message")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();

                            }
                        });
                builder.show();
            }

        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please add the product in Special category")
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

    private final TextWatcher qrWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String barcode= txtQr.getText().toString();

            Cursor cursor = itemDB.getItemWithBarcode(barcode);

            int nameCol = cursor.getColumnIndex("name");
            int rateCol = cursor.getColumnIndex("rate");
            int rateCol1 = cursor.getColumnIndex("ratewhole");
            int rateCol2 = cursor.getColumnIndex("rateretail");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String name = cursor.getString(nameCol);
                String rate = cursor.getString(rateCol);
                String ratew = cursor.getString(rateCol1);
                String rater = cursor.getString(rateCol2);
                txtName.setText(name);
                txtRate.setText(rate);
                txtRateWhole.setText(ratew);
                txtRateRetail.setText(rater);
                updatename = name;
                isUpdating = true;
                cameraSource.stop();
            }
        }
    };
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.stop();
    }

    public void removeItemFromDB(View view) {
        final String ids = txtQr.getText().toString();
        if(!ids.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Confirm Deletion")
                    .setTitle("Message")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            itemDB.deleteItemWithBarcode(ids);
                            Toast.makeText(ItemAddActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
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
