package com.xeta.org.billingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.usbsdk.UsbController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Anonymous on 04/25/18.
 */

public class BillInfoActivity extends AppCompatActivity implements Runnable{
    TableLayout tl;
    TextView tv;
    TextView dt;
    EditText et;
    ShopDB shopBill;
    String date;
    String total;
    int n;
    String billnumber;
    String items[];
    String ext;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_again);
        Intent get = getIntent();
        ext = get.getStringExtra("billno");

        //Toast.makeText(this, ext+" "+ext.length(), Toast.LENGTH_SHORT).show();

        tv = findViewById(R.id.billInfoNoprintShow);
        tv.setText(ext);
        shopBill = new ShopDB(this);

        tl = findViewById(R.id.myTableBillLayoutprint);

        tv = findViewById(R.id.GrandTotalPayableprint);
        dt = findViewById(R.id.billInfoDateprintShow);

        billnumber = tv.getText().toString();
        Cursor cursor2 = shopBill.getBillInfoNew(ext);

        //Toast.makeText(this, cursor2.getCount(), Toast.LENGTH_SHORT).show();


        if(cursor2 != null && (cursor2.getCount()> 0)){
            int nameCol = cursor2.getColumnIndex("items");
            int rateCol = cursor2.getColumnIndex("rate");
            int qtyCol = cursor2.getColumnIndex("qty");
            int dateCol = cursor2.getColumnIndex("dattime");
            int totalCol = cursor2.getColumnIndex("amount");
            cursor2.moveToFirst();
            //Toast.makeText(this, cursor2.getColumnCount() + " "+ cursor2.getCount(), Toast.LENGTH_SHORT).show();

            String name = cursor2.getString(nameCol);
            String rate = cursor2.getString(rateCol);
            String qt = cursor2.getString(qtyCol);
            date = cursor2.getString(dateCol);
            total = cursor2.getString(totalCol);

            List<String> itemList = Arrays.asList(name.split(" , "));
            List<String> rateList = Arrays.asList(rate.split(" , "));
            List<String> qtyList = Arrays.asList(qt.split(" , "));
            ArrayList<Double> totalList = new ArrayList<>();
            for(int ab=0;ab<itemList.size();ab++){
                String str1 = rateList.get(ab);
                String str2 = qtyList.get(ab).replaceAll("[^\\d.]", "");
                double tot = Double.parseDouble(str1) * Double.parseDouble(str2);
                totalList.add(tot);
            }

            for(int i=0,x=0,y=0,z=0,t=0; i<itemList.size() ;i++){
                TableRow product = new TableRow(BillInfoActivity.this);
                for(int j=0;j<4; j++){
                    TextView temp = new TextView(BillInfoActivity.this);
                    if(j==0) temp.setText(itemList.get(x++));
                    if(j==1) temp.setText(rateList.get(y++));
                    if(j==2) temp.setText(qtyList.get(z++));
                    if(j==3) temp.setText(Double.toString(totalList.get(t++)));
                    product.addView(temp);
                }
                tl.addView(product);
            }
            tv.setText(total);
            dt.setText(date);
        }

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
                    Toast.makeText(BillInfoActivity.this, "Make Sure Your USB is connected", Toast.LENGTH_SHORT).show();
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


    public void printAction(View view) {

        if(mBluetoothSocket != null) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        OutputStream opstream = null;
                        try {
                            opstream = mBluetoothSocket.getOutputStream();
                            outputStream = opstream;


                            byte[] printformat = new byte[]{0x1B,0x21,0x03};
                            outputStream.write(printformat);

                            printCustom("M A STORE",3,1);
                            printCustom("MAYYANAD, Ph: 7012165255",1,1);
                            printCustom("CASH INVOICE",0,1);
                            printCustom("Bill Number :  "+String.valueOf(ext),0,0);
                            printCustom("Time :  "+date,0,0);
                            printCustom(new String(new char[64]).replace("\0", "-"),0,0);
                            //printCustom("Name                   Rate           Qty          Total        ",0,0);
                            printCustom(String.format("%1$-22s %2$-14s %3$-11s %4$-12s", "Name", "Rate", "Qty", "Total"),0,0);
                            printCustom(new String(new char[64]).replace("\0", "-"),0,0);
                            for(int q=1,w=0; q<n ;q++,w+=4){
                                //String tt = rightpad(items[w],22) +" "+ rightpad(items[w+1],14) +" "+rightpad(items[w+2],12)+" "+rightpad(items[w+3],13);
                                String prnt = String.format("%1$-22s %2$-14s %3$-12s %4$-12s", items[w].substring(0, Math.min(items[w].length(), 22)), items[w+1], items[w+2], items[w+3]);
                                printCustom(prnt,0,0);
                            }
                            printCustom(new String(new char[64]).replace("\0", "-"),0,0);


                            printText(String.format("                    %1$-20s %2$-23s", "Total : ", "Rs."+" "+total+" "));
                            printText(String.format("                    %1$-20s %2$-23s", "Amount Payable : ", "Rs."+" "+total+" "));
                            //printText(leftRightAlign("Total : ",total+" "));
                            //printText(leftRightAlign("Amount Payable : ",totalpayable+" "));
                            printCustom(new String(new char[64]).replace("\0", "-"),0,0);
                            printNewLine();
                            printCustom("Thank You",1,1);
                            printCustom("Come Again..!",1,1);
                            printCustom(new String(new char[64]).replace("\0", "-"),0,0);
                            printNewLine();
                            printNewLine();
                            printNewLine();


                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BillInfoActivity.this);
                        builder.setMessage("Printer not connected")
                                .setTitle("Error")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.show();
                    }
                }
            };
            t.start();
        }
        else {
            items = new String[(tl.getChildCount() - 1) * 4];
            for (int i = 1; i < tl.getChildCount(); i++) {
                View child = tl.getChildAt(i);

                if (child instanceof TableRow) {
                    TableRow row = (TableRow) child;
                    for (int x = 0; x < 4; x++) {
                        TextView temp = (TextView) row.getChildAt(x);
                        items[n++] = temp.getText().toString();
                    }
                }
            }
            n = tl.getChildCount();

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth Error", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BillInfoActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);

                }
            }
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandlerBluetooth.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandlerBluetooth = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BillInfoActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BillInfoActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(BillInfoActivity.this, "Bluetooth Cannot Start", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void goBack(View view) {

    }
}
