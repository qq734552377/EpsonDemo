package com.ucast.jnidiaoyongdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.KeyboardSwitch;
import com.ucast.jnidiaoyongdemo.Model.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.Model.MermoyKeyboardSerial;
import com.ucast.jnidiaoyongdemo.Model.MermoyPrinterSerial;
import com.ucast.jnidiaoyongdemo.Model.MermoyUsbSerial;
import com.ucast.jnidiaoyongdemo.Model.MsCardProtocol;
import com.ucast.jnidiaoyongdemo.Model.PrinterProtocol;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Model.SendPackage;
import com.ucast.jnidiaoyongdemo.Model.UploadData;
import com.ucast.jnidiaoyongdemo.Model.UploadDataQueue;
import com.ucast.jnidiaoyongdemo.Serial.KeyBoardSerial;
import com.ucast.jnidiaoyongdemo.Serial.UsbSerial;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;
import com.ucast.jnidiaoyongdemo.Serial.PrinterSerialRestart;
import com.ucast.jnidiaoyongdemo.Serial.SerialPort;
import com.ucast.jnidiaoyongdemo.Serial.UsbSerialRestart;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.PrintAndDatas;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.db.UploadDBHelper;
import com.ucast.jnidiaoyongdemo.socket.NioTcpServer;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UsbSerial usbPort;
    private OpenPrint print;
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private TextView tv;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        iv = (ImageView) findViewById(R.id.imageView);

        Intent ootStartIntent = new Intent(this, UpdateService.class);
        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startService(ootStartIntent);

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "开始", Toast.LENGTH_SHORT).show();
//                ExceptionApplication.gLogger.info(" send path to Queue " + MyTools.millisToDateString(System.currentTimeMillis()));
//                String path = Environment.getExternalStorageDirectory() + "/ucast.bmp";
//                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path));
                String str  = "http://www.ucast.sg";
                KeyboardSwitch.sendToKeyboard(str);

            }
        });
    }

    public void getDataBaseShow() {
        tv.setText("");
        int pathNum = 0;
        int datahNum = 0;
        List<UploadData> list = UploadDBHelper.getInstance().selectAll();
        if (list == null){
            tv.setText("数据库没数据");
            return;
        }

        for (int i = 0; i < list.size() ; i++) {
            UploadData one = list.get(i);
            if(one.getType() == UploadData.PATH_TYPE){
                pathNum ++ ;
            }else{
                datahNum ++ ;
            }
        }
        tv.setText("所有的数据为:" + list.size()+" \n未上传的路径数据为："+pathNum+" \n 为上传的数据为："+datahNum);
    }

    long picWaitTime = 0 ;
    StringBuilder onePicStr = new StringBuilder();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what){
                case 10:
                    byte[] data = (byte[]) msg.obj;
//                    tv.setText(new String(data));
                    try {
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/ucast/port.txt");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        fos.write(data);
//                        fos.write("\n-----000000000000000000000000-----\n".getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(MainActivity.this,new String(data),Toast.LENGTH_SHORT).show();

                    break;

                case 20:
                    String dataString = (String) msg.obj;
                    long oldTime = System.currentTimeMillis();
//                    if (picWaitTime == 0 || oldTime - picWaitTime < 400){
//                        if (dataString != null)
//                            onePicStr.append(dataString);
//                        picWaitTime =oldTime;
//                        tv.setText("打印时间间隔： "+ (oldTime - picWaitTime));
//                        return;
//                    }else {
//                        if (dataString != null)
//                            onePicStr.append(dataString);
//                        dataString = onePicStr.toString();
//                        onePicStr.delete(0,onePicStr.length());
//                        picWaitTime =oldTime;
////                        Toast.makeText(MainActivity.this,"开始打印中.....",Toast.LENGTH_SHORT).show();
//                    }
                    tv.setText("");
                    StringBuilder sb = new StringBuilder();
                    ExceptionApplication.gLogger.info("get one bitData from Usb " +  MyTools.millisToDateString(System.currentTimeMillis()));
                    try {
                        if(dataString.contains(EpsonParseDemo.startEpsonStr)){
                            oldTime = System.currentTimeMillis();
                            List<String> paths = EpsonParseDemo.parseEpsonBitData(dataString.trim());
                            sb.append("解析EPSON生成图片的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

                            oldTime = System.currentTimeMillis();
                            String path = SomeBitMapHandleWay.compoundOneBitPic(paths);
                            sb.append("合成为一张图片的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

                            iv.setImageBitmap(BitmapFactory.decodeFile(path));
                            tv.setText(sb.toString());
                            ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path));
                            MyTools.uploadFileByQueue(path);
                            return;
                        }

                        String path = printOne(dataString);

                        File file = new File(Environment.getExternalStorageDirectory(),
                                "/ucast/port_str.txt");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        fos.write(dataString.getBytes());
                        fos.write("\n-----000000000000000000000000-----\n".getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:

                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        getDataBaseShow();
        super.onResume();
    }

    public String printOne(String data){

        long oldTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        String [] bytes = data.split(" ");
        byte [] datas =new byte[bytes.length];
        String path = "" ;

        for (int i = 0; i < bytes.length; i++) {
            datas[i] = (byte) Integer.parseInt(bytes[i], 16);
        }
        List<byte[]> byteList = EpsonParseDemo.getEpsonFromByteArr(datas);
        sb.append("解析EPSON协议数据的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");
        try {
            oldTime = System.currentTimeMillis();
            List<PrintAndDatas> printdatas = EpsonParseDemo.parseEpsonByteList(byteList);
            sb.append("解析成对象集合的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

            oldTime = System.currentTimeMillis();
            List<PrintAndDatas> goodPrintdatas = EpsonParseDemo.makeListIWant(printdatas);
            sb.append("合并数据对象的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

            oldTime = System.currentTimeMillis();
            MyTools.uploadDataByQueue(goodPrintdatas.get(0).datas);
            sb.append("上传数据的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

            oldTime = System.currentTimeMillis();
            List<Bitmap> bmps = EpsonParseDemo.parseEpsonBitDataAndStringReturnBitmap(goodPrintdatas);
            sb.append("生成对应的图片的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");

            oldTime = System.currentTimeMillis();
            path = SomeBitMapHandleWay.compoundOneBitPicWithBimaps(bmps);
            sb.append("生成一张图片的时间为："+ (System.currentTimeMillis() - oldTime) + "ms\n");
            if (path != null && path != ""){
                tv.setText(sb.toString());
                iv.setImageBitmap(BitmapFactory.decodeFile(path));
                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return path;
        }

    }

    private static final String TAG = "MainActivity";
}
