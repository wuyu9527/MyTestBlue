package com.example.android.myapplication;

import com.example.android.myapplication.DeviceListActivity;

import android.content.Intent;

import com.example.android.myapplication.util.ESC_POS_EPSON_ANDROID;

import com.zj.btsdk.PrintPic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class PrintDemo extends Activity {
    Button btnSearch;
    Button btnSendDraw;
    Button btnSend;
    Button btnClose;
    EditText edtContext;
    EditText edtPrint;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;  //获取设备消息
    ESC_POS_EPSON_ANDROID mEscPos;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mService = new BluetoothService(this, mHandler);
        //蓝牙不可用退出程序
        if (mService.isAvailable() == false) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //蓝牙未打开，打开蓝牙
        if (mService.isBTopen() == false) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        try {
            btnSendDraw = (Button) this.findViewById(R.id.btn_test);
            btnSendDraw.setOnClickListener(new ClickEvent());
            btnSearch = (Button) this.findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new ClickEvent());
            btnSend = (Button) this.findViewById(R.id.btnSend);
            btnSend.setOnClickListener(new ClickEvent());
            btnClose = (Button) this.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new ClickEvent());
            edtContext = (EditText) findViewById(R.id.txt_content);
            btnClose.setEnabled(false);
            btnSend.setEnabled(false);
            btnSendDraw.setEnabled(false);
        } catch (Exception ex) {
            Log.e("出错信息", ex.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == btnSearch) {
                Intent serverIntent = new Intent(PrintDemo.this, DeviceListActivity.class);      //运行另外一个类的活动
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            } else if (v == btnSend) {
                String msg = edtContext.getText().toString();
                //210mm×297mm A4  8.3*11.7
                PrintUtil printUtil = new PrintUtil(mService,2,PrintDemo.this);
                pinTypeTestPrinterType(printUtil);
            } else if (v == btnClose) {
                mService.stop();
            } else if (v == btnSendDraw) {
                String msg = "";
                String lang = getString(R.string.strLang);
                //printImage();
                byte[] cmd = new byte[3];
                cmd[0] = 0x1b;
                cmd[1] = 0x21;
                if ((lang.compareTo("en")) == 0) {
                    cmd[2] |= 0x10;
                    mService.write(cmd);           //倍宽、倍高模式
                    mService.sendMessage("Congratulations!\n", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);           //取消倍高、倍宽模式
                    msg = "  You have sucessfully created communications between your device and our bluetooth printer.\n\n"
                            + "  the company is a high-tech enterprise which specializes" +
                            " in R&D,manufacturing,marketing of thermal printers and barcode scanners.\n\n";


                    mService.sendMessage(msg, "GBK");
                } else if ((lang.compareTo("ch")) == 0) {
                    cmd[2] |= 0x10;
                    mService.write(cmd);           //倍宽、倍高模式
                    mService.sendMessage("恭喜您！\n", "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);           //取消倍高、倍宽模式
                    msg = "  您已经成功的连接上了我们的蓝牙打印机！\n\n"
                            + "  本公司是一家专业从事研发，生产，销售商用票据打印机和条码扫描设备于一体的高科技企业.\n\n";

                    mService.sendMessage(msg, "GBK");
                    printImage();
                }
            }
        }
    }

    /**
     * 创建一个Handler实例，用于接收BluetoothService类返回回来的消息
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            Toast.makeText(getApplicationContext(), "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            btnClose.setEnabled(true);
                            btnSend.setEnabled(true);
                            btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:  //正在连接
                            Log.d("蓝牙调试", "正在连接.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //监听连接的到来
                        case BluetoothService.STATE_NONE:
                            Log.d("蓝牙调试", "等待连接.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    btnClose.setEnabled(false);
                    btnSend.setEnabled(false);
                    btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:      //请求打开蓝牙
                if (resultCode == Activity.RESULT_OK) {   //蓝牙已经打开
                    Toast.makeText(this, "Bluetooth open successful", Toast.LENGTH_LONG).show();
                } else {                 //用户不允许打开蓝牙
                    finish();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //请求连接某一蓝牙设备
                if (resultCode == Activity.RESULT_OK) {   //已点击搜索列表中的某个设备项
                    Log.i("whx", "mService.isBTopen():" + mService.isBTopen());
                    Log.i("whx", "mService.isAvailable():" + mService.isAvailable());
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //获取列表项中设备的mac地址
                    con_dev = mService.getDevByMac(address);
                    mService.connect(con_dev);
                }
                break;
        }
    }

    //打印图形
    @SuppressLint("SdCardPath")
    private void printImage() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(384);
        pg.initPaint();
        String url = Environment.getExternalStorageDirectory().getPath();
        pg.drawImage(0, 0, url + "/android.jpg");
        sendData = pg.printDraw();
        String url1 = url + "/0.png";
        if (new File(url1).exists()) {
            mService.write(image2byte(url1));   //打印byte流数据
        }

    }

    //图片到byte数组
    public byte[] image2byte(String path) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(String.valueOf(new FileWriter(new File(path))));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    //byte数组到图片
    public void byte2image(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) return;
        try {
            FileOutputStream imageOutput = new FileOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }

    //byte数组到16进制字符串
    public String byte2string(byte[] data) {
        if (data == null || data.length <= 1) return "0x";
        if (data.length > 200000) return "0x";
        StringBuffer sb = new StringBuffer();
        int buf[] = new int[data.length];
        //byte数组转化成十进制
        for (int k = 0; k < data.length; k++) {
            buf[k] = data[k] < 0 ? (data[k] + 256) : (data[k]);
        }
        //十进制转化成十六进制
        for (int k = 0; k < buf.length; k++) {
            if (buf[k] < 16) sb.append("0" + Integer.toHexString(buf[k]));
            else sb.append(Integer.toHexString(buf[k]));
        }
        return "0x" + sb.toString().toUpperCase();
    }


    /**
     * 针式蓝牙测试打印
     *
     * @param printUtil*/
    public void pinTypeTestPrinterType(PrintUtil printUtil){
        printUtil.defaultSettingPinBT();
        printUtil.printMainTitle("主营门店 销售单");
        printUtil.appendReturn();
        printUtil.appendReturn();
        printUtil.printBillNum("k11111110","客户:李小明","营业员:小王","2017-8-8");
        printUtil.appendReturn();
        printUtil.getPrintSplitLine();
        printUtil.printProductSerialTitle("序号" ,"货品信息" ,"售价" ,"数量" ,"小计金额","备注");
        printUtil.appendReturn();
        printUtil.getPrintSplitLine();
        for (int i = 0; i < 15; i++) {
            printUtil.printProductSerialNum(
                i,
            "khss110088",
             "上衣" ,
             "淡黄色，XL" ,
             "100.00" ,
             "10" ,
             "1000.00" ,
             "打折品"
            );
        }
        printUtil.getPrintSplitLine();
        printUtil.printTotalMoneyWithArr1(new String[]{"本单金额:1000.00","实付:100","未付:0","现金:1000.0"},new String[]{"前期余额:99.0","本单结余:100","累计总余额:999"});
        printUtil.appendReturn();
        printUtil.getPrintSplitLine();
        printUtil.lineSpacePinTBSet();
        printUtil.printContactInfo(new String[]{"400-1818-975转602"}, "地址:地址地址地址地址地址地址地址地址地址",new String[]{"农行行:6228 5677 1234 8888 8888  王小丫"}, new String[]{"门店备注:门店"},"开单备注:开单");
        printUtil.appendReturn();
        printUtil.printVersion("掌柜帮商户版:1.1.1.0","打印时间:2017-8-8 15:42");
        printUtil.defaultLineSpace();
        printUtil.nextPage();
    }

//+ (ZgbPrinterManager *)pinTypeTestPrinterType;
//    {
//        ZgbPrinterManager * printer = [ZgbPrinterManager new];
//        printer.printType = kPrinterTypePinBT;
//    [printer defaultSettingPinBT];
//    [printer printMainTitle:@"主营门店 销售单"];
//    [printer appendReturn];
//    [printer appendReturn];
//
//        NSDate *currentDate = [NSDate date];
//        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
//    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
//        NSString *dateString = [dateFormatter stringFromDate:currentDate];
//    [printer printBillNum:@"k11111110" customer:@"客户:李小明" saler:@"营业员:小王" date:dateString];
//
//    [printer appendReturn];
//    [printer getPrintSplitLine];
//
//    [printer printProductSerialNum:@"序号" info:@"货品信息" price:@"售价" num:@"数量" totalPrice:@"小计金额" remark:@"备注"];
//
//    [printer appendReturn];
//    [printer getPrintSplitLine];
//
//        for (int i = 0; i < 15; i++) {
//        [printer printProductSerialNum:F(@"%zd", i) styleNumber:@"khss110088" name:@"上衣" colorAndSize:@"淡黄色，XL" price:@"100.00" num:@"10" totalPrice:@"1000.00" remark:@"打折品"];
//        [printer appendReturn];
//        }
//
//    [printer getPrintSplitLine];
//
//    [printer printTotalMoneyWithArr1:@[@"本单金额:1000.00",@"实付:100",@"未付:0",@"现金:1000.0"] arr2:@[@"前期余额:99.0",@"本单结余:100",@"累计总余额:999"]];
//
//    [printer appendReturn];
//    [printer getPrintSplitLine];
//
//    [printer lineSpacePinTBSet];
//
//    [printer printContactInfo:@[@"400-1818-975转602"] address:@"地址:地址地址地址地址地址地址地址地址地址" accounts:@[@"农行行:6228 5677 1234 8888 8888  王小丫"] storeRemark:@[@"门店备注:门店"] billRemark:@"开单备注:开单"];
//
//    [printer appendReturn];
//
//        NSString * version = [VERSION_APP substringToIndex:3];
//    [printer printVersion:F(@"掌柜帮商户版%@", version)  printDate:F(@"打印时间:%@", dateString)];
//    [printer defaultLineSpace];
//
//    [printer nextPage];
//        return printer;
//    }

}
