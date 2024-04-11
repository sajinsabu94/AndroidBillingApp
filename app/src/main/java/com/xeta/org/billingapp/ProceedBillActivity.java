package com.xeta.org.billingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.usbsdk.UsbController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Anonymous on 04/24/18.
 */

public class ProceedBillActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    static UsbController  usbCtrl = null;
    static UsbDevice dev = null;
    private int[][] u_infor;
    String[] items;
    int n;
    TableLayout tl;
    TextView gt;
    TextView gtp;
    ShopDB shopdb;
    String stringItem;
    String qtyItem;
    String rateItem;
    String total;
    String totalpayable;
    TextView billNoInfo;
    List<String> itemlist;
    List<String> qtylist;
    List<String> ratelist;
    boolean canPrint;
    int billno;
    Button btnPrint;

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
        setContentView(R.layout.activity_cart);
        shopdb = new ShopDB(this);
        shopdb.getReadableDatabase();
        canPrint = false;

        stringItem = null;
        qtyItem = null;
        billNoInfo = findViewById(R.id.billInfoNo);
        tl = findViewById(R.id.myTableBillLayout);
        gt = findViewById(R.id.GrandTotal);
        gtp = findViewById(R.id.GrandTotalPayable);
        Intent intent = getIntent();
        items = intent.getStringArrayExtra("values");
        total = intent.getStringExtra("billamnt");
        totalpayable = intent.getStringExtra("billamntpay");
        gt.setText(total);
        gtp.setText(totalpayable);
        n = intent.getIntExtra("count", 0);
        generateList();

        billno = shopdb.getMaxBillId()+1;
        billNoInfo.setText("Bill No: "+Integer.toString(billno));


        usbCtrl = new UsbController(this,mHandler);
        btnPrint = (Button) this.findViewById(R.id.ButtonCheckOut);
        btnPrint.setOnClickListener(this);
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
        btnPrint.setEnabled(false);
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
                    btnPrint.setEnabled(true);
                    canPrint = true;
                    break;
                default:
                    Toast.makeText(ProceedBillActivity.this, "Make Sure Your USB is connected", Toast.LENGTH_SHORT).show();
                    finalBill();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {

        usbCtrl.close();
        int  i = 0;
        for( i = 0 ; i < 8 ; i++ ){
            dev = usbCtrl.getDev(u_infor[i][0],u_infor[i][1]);
            if(dev != null){
                break;
            }
        }

        if( dev != null ){
            if( !(usbCtrl.isHasPermission(dev))){
                usbCtrl.getPermission(dev);
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.msg_getpermission),
                        Toast.LENGTH_SHORT).show();

                try {
                    Command.ESC_Align[2] = 0x01;
                    SendDataByte(Command.ESC_Align);
                    Command.GS_ExclamationMark[2] = 0x11;
                    SendDataByte(Command.GS_ExclamationMark);
                    SendDataByte("MA STORE\n".getBytes("GBK"));
                    Command.GS_ExclamationMark[2] = 0x00;
                    SendDataByte(Command.GS_ExclamationMark);
                    SendDataByte("    MAYYANAD, Ph: 7012165255    \n".getBytes());
                    Command.GS_ExclamationMark[2] = 0x00;
                    SendDataByte(Command.GS_ExclamationMark);
                    Command.ESC_Align[2] = 0x01;
                    SendDataByte(Command.ESC_Align);
                    SendDataString("CASH INVOICE");
                    Command.ESC_Align[2] = 0x00;
                    SendDataByte(Command.ESC_Align);
                    Command.GS_ExclamationMark[2] = 0x00;
                    SendDataByte(Command.GS_ExclamationMark);
                    SendDataByte(("\nBill Number :  "+String.valueOf(billno)+"\nTime :"+getDateTime()).getBytes("GBK"));
                    SendDataByte("\nName       Rate    Qty   Total  \n".getBytes("GBK"));
                    SendDataByte("--------------------------------".getBytes("GBK"));
                    for(int q=1,w=0; q<n ;q++,w+=4){
                        String tt = rightpad(items[w],10) +" "+ rightpad(items[w+1],7) +" "+rightpad(items[w+2],5)+" "+rightpad(items[w+3],7);
                        SendDataByte(tt.getBytes("GBK"));
                    }
                    SendDataByte("\n================================".getBytes("GBK"));

                    SendDataByte(("Total :             "+total+'\n').getBytes("GBK"));
                    SendDataByte(("Amount Payable :    "+totalpayable+'\n').getBytes("GBK"));
                    Command.ESC_Align[2] = 0x01;
                    SendDataByte(Command.ESC_Align);
                    SendDataByte("         --------------         ".getBytes("GBK"));
                    Command.GS_ExclamationMark[2] = 0x6;
                    SendDataByte(Command.GS_ExclamationMark);
                    SendDataByte("Thank You\n".getBytes("GBK"));
                    Command.GS_ExclamationMark[2] = 0x6;
                    SendDataByte(Command.GS_ExclamationMark);
                    SendDataByte("Come again!\n".getBytes("GBK"));
                    Command.GS_ExclamationMark[2] = 0x00;
                    SendDataByte(Command.GS_ExclamationMark);
                    SendDataByte("\n\n\n".getBytes("GBK"));
                    SendDataByte(Command.GS_i);
                    finalBill();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                finalBill();
            }
        }
        else{
            printBill();
        }
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

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    private void generateList() {
        itemlist = new ArrayList<String>();
        qtylist = new ArrayList<String>();
        ratelist = new ArrayList<String>();

        for(int i=1,x=0; i<n ;i++){
            TableRow product = new TableRow(this);
            for(int j=0;j<4; j++){
                TextView temp = new TextView(this);
                temp.setText(items[x++]);
                if(j==0) itemlist.add(items[x-1]);
                if(j==1) ratelist.add(items[x-1]);
                if(j==2) qtylist.add(items[x-1]);
                product.addView(temp);
            }
            tl.addView(product);
        }
        stringItem = android.text.TextUtils.join(" , ", itemlist);
        rateItem = android.text.TextUtils.join(" , ", ratelist);
        qtyItem = android.text.TextUtils.join(" , ", qtylist);
    }

    public void finalBill() {


        String dated = getDateTime() + "\n\n\n\n\n\n";

            boolean res = shopdb.addTransactionToDB(String.valueOf(billno), stringItem, qtyItem, getDateTime(), total);
            boolean res2 = shopdb.addTransactionToBackupDB(String.valueOf(billno), stringItem, rateItem, qtyItem, getDateTime(), total);
            if (res && res2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Transaction Successful")
                        .setTitle("Message")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                Intent home = new Intent(ProceedBillActivity.this, MainActivity.class);
                                startActivity(home);
                            }
                        });
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Errors occured in transaction")
                        .setTitle("Error")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
            }
        Intent intent = new Intent(ProceedBillActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ProceedBillActivity.this.finish();
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected void printBill() {

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
                Intent connectIntent = new Intent(ProceedBillActivity.this,
                        DeviceListActivity.class);
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);
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
                                    printCustom("Bill Number :  "+String.valueOf(billno),0,0);
                                    printCustom("Time :  "+getDateTime(),0,0);
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
                                    printText(String.format("                    %1$-20s %2$-23s", "Amount Payable : ", "Rs."+" "+totalpayable+" "));
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProceedBillActivity.this);
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
                    finalBill();
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
            Toast.makeText(ProceedBillActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };



    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

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
                    Intent connectIntent = new Intent(ProceedBillActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(ProceedBillActivity.this, "Bluetooth Cannot Start", Toast.LENGTH_SHORT).show();
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

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String leftRightAlign(String str1, String str2) {
        String ans = str1+str2;
        if(ans.length() <64){
            int n = (64 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
