package com.ucast.jnidiaoyongdemo;

import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.Model.MermoyKeyboardSerial;
import com.ucast.jnidiaoyongdemo.Model.MermoyPrinterSerial;
import com.ucast.jnidiaoyongdemo.Model.MermoyUsbSerial;
import com.ucast.jnidiaoyongdemo.Model.MermoyUsbWithByteSerial;
import com.ucast.jnidiaoyongdemo.Model.MsCardProtocol;
import com.ucast.jnidiaoyongdemo.Model.PrinterProtocol;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Model.UploadDataQueue;
import com.ucast.jnidiaoyongdemo.Serial.KeyBoardSerial;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;
import com.ucast.jnidiaoyongdemo.Serial.PrinterSerialRestart;
import com.ucast.jnidiaoyongdemo.Serial.UsbSerial;
import com.ucast.jnidiaoyongdemo.Serial.UsbSerialRestart;
import com.ucast.jnidiaoyongdemo.Serial.UsbWithByteSerial;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.PrintAndDatas;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.mytime.MyTimeTask;
import com.ucast.jnidiaoyongdemo.mytime.MyTimer;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyDialog;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by pj on 2016/11/21.
 */
public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return this.START_STICKY;
    }

    @Override
    public void onCreate() {

        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1, notification);
        super.onCreate();
//        startTimer();

        OpenPrint print = new OpenPrint(Config.PrinterSerial);
        boolean isOpen = print.Open();
        MermoyPrinterSerial.Add(print);
        if (isOpen){
            print.Send(PrinterProtocol.getPrinterSwitchOnProtocol());
            print.Send(MsCardProtocol.getOpenMsCardProtocol());
        }

        KeyBoardSerial keyBoardSerial = new KeyBoardSerial(Config.KeyboardSerial);
        keyBoardSerial.Open();
        MermoyKeyboardSerial.Add(keyBoardSerial);

//        UsbSerial usbPort = new UsbSerial(Config.UsbSerial,handler);
//        usbPort.Open();
//        MermoyUsbSerial.Add(usbPort);
        UsbWithByteSerial usbPort = new UsbWithByteSerial(Config.UsbSerial,handler);
        usbPort.Open();
        MermoyUsbWithByteSerial.Add(usbPort);

//        PrinterSerialRestart.StartTimer();
//        UsbSerialRestart.StartTimer();
//        ListPictureQueue.StartTimer();
        UploadDataQueue.StartTimer();
        ReadPictureManage.GetInstance();

//        NioTcpServer tcpServer = new NioTcpServer(7700);
//        new Thread(tcpServer).start();

    }

    /**
     * 当服务被杀死时重启服务
     * */
    public void onDestroy() {
        stopForeground(true);
        Intent localIntent = new Intent();
        localIntent.setClass(this, UpdateService.class);
        this.startService(localIntent);    //销毁时重新启动Service
    }

    public MyTimer timer;
    public void startTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            @Override
            public void run() {
                String url= YinlianHttpRequestUrl.TIMEUPDATEURL;
                getSystemTime(url.trim());
            }
        }), 1000*60*2L, 1000*30*60L);
        timer.initMyTimer().startMyTimer();
    }

    private static final String TAG = "UpdateService";
    public void getSystemTime(String url){
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String time=result.replace("\"","").trim();
                Log.e(TAG, "onSuccess: " +time);
                setTime(time);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 10:
                    String path = (String) msg.obj;
//                    ExceptionApplication.gLogger.info("get pic from UsbSerial -->" + System.currentTimeMillis() + "   \npath:" + path);
                    ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path));
                    break;
            }
        }
        };




    public void setTime(String mytime){
        Date mydate=StringToDate(mytime);
        long curMs=mydate.getTime();
        boolean isSuc = SystemClock.setCurrentTimeMillis(curMs);//需要Root权限
        Log.e(TAG, "setTime: "+isSuc );
    }
    private Date StringToDate(String s){
        Date time=null;
        SimpleDateFormat sd=new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            time=sd.parse(s);
        } catch (java.text.ParseException e) {
            System.out.println("输入的日期格式有误！");
            e.printStackTrace();
        }
        return time;
    }

}
