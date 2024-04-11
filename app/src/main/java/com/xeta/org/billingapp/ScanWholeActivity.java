package com.xeta.org.billingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ScanWholeActivity extends AppCompatActivity {
    EditText txtQr, txtRate, txtRatew, txtQty;
    AutoCompleteTextView txtName;
    TextView textTotal;
    SurfaceView itemQR;
    CameraSource cameraSource;
    ItemDB itemDB;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_shop_wholesale);
        itemDB = new ItemDB(this);

        txtQr = (EditText) findViewById(R.id.editBillQRw);
        txtQr.setEnabled(false);
        txtQr.addTextChangedListener(qrWatcher);
        txtName = (AutoCompleteTextView) findViewById(R.id.editBillNamew);
        txtName.setEnabled(false);

        txtQty = (EditText) findViewById(R.id.editBillQtyw);
        txtQty.addTextChangedListener(qtyWatcher);

        txtRate = (EditText) findViewById(R.id.editBillRatew);
        txtRate.setEnabled(false);

        txtRatew = (EditText) findViewById(R.id.editBillRateWholsalew);
        //txtRatew.setEnabled(false);

        itemQR = (SurfaceView) findViewById(R.id.surfaceVieww);
        itemQR.setZOrderMediaOverlay(true);
        textTotal = (TextView) findViewById(R.id.textTotalw);
        textTotal.setText("0.00");
        context = getApplicationContext();
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
                    if (ActivityCompat.checkSelfPermission(ScanWholeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
            int rateColw = cursor.getColumnIndex("ratewhole");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String name = cursor.getString(nameCol);
                String rate = cursor.getString(rateCol);
                String ratew = cursor.getString(rateColw);
                txtName.setText(name);
                txtRate.setText(rate);
                txtRatew.setText(ratew);
                Log.e("Values : ",name+rate+ratew);
                cameraSource.stop();
                txtQty.setFocusable(true);
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
            if(!txtName.getText().toString().isEmpty() && !txtRate.getText().toString().isEmpty()) {
                String ratew = txtRatew.getText().toString();
                String qnty = txtQty.getText().toString();
                if(!qnty.substring(qnty.length()-1).equals('.')) {
                    if (ratew.matches("\\d+(?:\\.\\d+)?") && qnty.matches("\\d+(?:\\.\\d+)?")) {
                        float qt = Float.parseFloat(qnty);
                        float rt = Float.parseFloat(ratew);
                        float total = (float) qt * rt;
                        textTotal.setText(String.valueOf(total));
                    }
                }
            }
        }
    };

    public void addCartProceed(View view) {
        String name = txtName.getText().toString();
        String rate = txtRatew.getText().toString();
        String test = txtRate.getText().toString();
        String qty = txtQty.getText().toString();
        String total = textTotal.getText().toString();
        if(!rate.isEmpty()&&!test.isEmpty()&&!qty.equals("")) {
            Intent back = new Intent();
            back.putExtra("name", name);
            back.putExtra("rate", rate);
            back.putExtra("qty", qty);
            back.putExtra("total", total);
            setResult(RESULT_OK, back);
            finish();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Item not available or Invalid entry")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.stop();
    }

    public void cancelScan(View view) {
        finish();
    }
}
