package com.xeta.org.billingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.usbsdk.UsbController;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anonymous on 04/25/18.
 */

public class OldBillInfoActivity extends AppCompatActivity {
    TableLayout tl;
    TextView tv;
    TextView dt;
    EditText et;
    ShopDB shopBill;
    String date;

    static UsbController  usbCtrl = null;
    static UsbDevice dev = null;
    private int[][] u_infor;
    String billnumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_again_old);
        Intent get = getIntent();
        String ext = get.getStringExtra("billno");
        tv = findViewById(R.id.billInfoNoprintShow2);
        tv.setText(ext);
        shopBill = new ShopDB(this);

        tl = findViewById(R.id.myTableBillLayoutprint2);

        tv = findViewById(R.id.GrandTotalPayableprint2);
        dt = findViewById(R.id.billInfoDateprintShow2);

        billnumber = tv.getText().toString();

        Cursor cursor = shopBill.getBillInfo(ext);
        if(cursor != null && (cursor.getCount()> 0)){
            int nameCol = cursor.getColumnIndex("items");
            int qtyCol = cursor.getColumnIndex("qty");
            int dateCol = cursor.getColumnIndex("dattime");
            int totalCol = cursor.getColumnIndex("amount");
            cursor.moveToFirst();

            String name = cursor.getString(nameCol);
            String qt = cursor.getString(qtyCol);
            String date = cursor.getString(dateCol);
            String total = cursor.getString(totalCol);

            List<String> itemList = Arrays.asList(name.split(" , "));
            List<String> qtyList = Arrays.asList(qt.split(" , "));


            for(int i=0,x=0,y=0,z=0; i<itemList.size() ;i++){
                TableRow product = new TableRow(OldBillInfoActivity.this);
                for(int j=0;j<2; j++){
                    TextView temp = new TextView(OldBillInfoActivity.this);
                    if(j==0) temp.setText(itemList.get(x++));
                    if(j==1) temp.setText(qtyList.get(y++));
                    product.addView(temp);
                }
                tl.addView(product);
            }
            tv.setText(total);
            dt.setText(date);

        }

        usbCtrl = new UsbController(this,mHandler);
        u_infor = new int[8][2];
        u_infor[0][0] = 0x1CBE;
        u_infor[0][1] = 0x0003;
        u_infor[1][0] = 0x1CB0;
        u_infor[1][1] = 0x0003;
        u_infor[2][0] = 0x0483;
        u_infor[2][1] = 0x5740;
        u_infor[3][0] = 0x0493;
        u_infor[3][1] = 0x8760;
        u_infor[4][0] = 0x0416;
        u_infor[4][1] = 0x5011;
        u_infor[5][0] = 0x0416;
        u_infor[5][1] = 0xAABB;
        u_infor[6][0] = 0x1659;
        u_infor[6][1] = 0x8965;
        u_infor[7][0] = 0x0483;
        u_infor[7][1] = 0x5741;
    }
    public boolean CheckUsbPermission(){
        if( dev != null ){
            if( usbCtrl.isHasPermission(dev)){
                return true;
            }
        }
        Toast.makeText(getApplicationContext(), getString(R.string.msg_conn_state),
                Toast.LENGTH_SHORT).show();
        return false;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbController.USB_CONNECTED:
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_getpermission),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(OldBillInfoActivity.this, "Make Sure Your USB is connected", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    public void exitBillInfo(View view) {
        finish();
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private String rightpad(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    private void SendDataByte(byte[] data){
        if(data.length>0)
            usbCtrl.sendByte(data, dev);
    }

    private void SendDataString(String data){
        if(data.length()>0)
            usbCtrl.sendMsg(data, "GBK", dev);
    }


}
