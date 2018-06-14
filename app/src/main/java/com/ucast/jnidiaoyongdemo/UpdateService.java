package com.ucast.jnidiaoyongdemo;

import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.MoneyBoxEvent;
import com.ucast.jnidiaoyongdemo.Model.MyUsbManager;
import com.ucast.jnidiaoyongdemo.jsonObject.HeartBeatResult;
import com.ucast.jnidiaoyongdemo.queue_ucast.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.globalMapObj.MermoyKeyboardSerial;
import com.ucast.jnidiaoyongdemo.globalMapObj.MermoyPrinterSerial;
import com.ucast.jnidiaoyongdemo.globalMapObj.MermoyUsbWithByteSerial;
import com.ucast.jnidiaoyongdemo.protocol_ucast.MsCardProtocol;
import com.ucast.jnidiaoyongdemo.protocol_ucast.PrinterProtocol;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.queue_ucast.UploadDataQueue;
import com.ucast.jnidiaoyongdemo.Serial.KeyBoardSerial;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;
import com.ucast.jnidiaoyongdemo.Serial.UsbWithByteSerial;
import com.ucast.jnidiaoyongdemo.jsonObject.BaseHttpResult;
import com.ucast.jnidiaoyongdemo.mytime.MyTimeTask;
import com.ucast.jnidiaoyongdemo.mytime.MyTimer;
import com.ucast.jnidiaoyongdemo.socket.net_print.NioNetPrintServer;
import com.ucast.jnidiaoyongdemo.tools.MyDialog;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by pj on 2016/11/21.
 */
public class UpdateService extends Service {

    private boolean connected;

    private static final long MONEYBOXTISHITIME = 1000L * 12;
    private static long oldMoneyBoxTime ;
    private static Dialog moneyBoxDialog;

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
        startTimer();
        startMoneyBoxTimer();
        copyCfg("ums.bmp");
        copyCfg("ucast.bmp");
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


        UsbWithByteSerial usbPort = new UsbWithByteSerial(Config.UsbSerial);
        usbPort.Open();
        MermoyUsbWithByteSerial.Add(usbPort);

//        PrinterSerialRestart.StartTimer();
//        UsbSerialRestart.StartTimer();
        ListPictureQueue.StartTimer();
        UploadDataQueue.StartTimer();
        ReadPictureManage.GetInstance();

//        NioTcpServer tcpServer = new NioTcpServer(7700);
//        new Thread(tcpServer).start();

        //开启网口打印监听
        NioNetPrintServer netPrintServer = new NioNetPrintServer();
        new Thread(netPrintServer).start();

        String isOpenPrint = SavePasswd.getInstace().readxml(SavePasswd.ISOPENPRINT,SavePasswd.CLOSEPRINT);
        boolean isClose = isOpenPrint.equals(SavePasswd.CLOSEPRINT);
        setIsClosePrintMode(isClose);

        String netPrintUploadstr = SavePasswd.getInstace().readxml(SavePasswd.ISNETPRINTUPLOADTOSERVICE,SavePasswd.OPEN);
        boolean isCloseNetPrintUpload = netPrintUploadstr.equals(SavePasswd.CLOSE);
        setCloseNetPrinterUploadToService(isCloseNetPrintUpload);

        registUsbBroadcast();
        moneyBoxDialog = MyDialog.showIsOpenMoneyBoxDialog();
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showIsOpenMoneyBox(MoneyBoxEvent event){
        if (!event.isShow()){
            if (moneyBoxDialog.isShowing())
                moneyBoxDialog.dismiss();
            return;
        }
        if (moneyBoxDialog != null){
            oldMoneyBoxTime = System.currentTimeMillis();
            if (moneyBoxDialog.isShowing())
                return;
            moneyBoxDialog.show();
        }
    }


    /**
     * 当服务被杀死时重启服务
     * */
    public void onDestroy() {
        stopForeground(true);
        Intent localIntent = new Intent();
        localIntent.setClass(this, UpdateService.class);
        EventBus.getDefault().unregister(this);
        if(receiver != null){
            unregisterReceiver(receiver);
        }
        this.startService(localIntent);    //销毁时重新启动Service
    }


    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    public void registUsbBroadcast() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
//                ExceptionApplication.gLogger.info("来的广播为："+ action);

                switch (action) {
                    case UsbManager.ACTION_USB_ACCESSORY_ATTACHED:
                    case UsbManager.ACTION_USB_ACCESSORY_DETACHED:
                        UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                        break;
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        break;
                    case MyUsbManager.ACTION_USB_STATE:
                        connected = intent.getBooleanExtra(MyUsbManager.USB_CONNECTED, false);
                        if( !connected){
                            String url= YinlianHttpRequestUrl.TIMEUPDATEURL;
                            getSystemTime(url.trim());
                        }
                        break;
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(MyUsbManager.ACTION_USB_STATE);
        registerReceiver(receiver, intentFilter);
    }

    public MyTimer timer;
    public MyTimer moneyBoxtimer;
    public void startTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            @Override
            public void run() {
                String url= YinlianHttpRequestUrl.TIMEUPDATEURL;
                getSystemTime(url.trim());
            }
        }), 1000*2L, 1*1000*60L);
        timer.initMyTimer().startMyTimer();
    }
    public void startMoneyBoxTimer() {
        moneyBoxtimer = new MyTimer(new MyTimeTask(new Runnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() - oldMoneyBoxTime < MONEYBOXTISHITIME){
                    return;
                }
                oldMoneyBoxTime = System.currentTimeMillis();
                EventBus.getDefault().postSticky(new MoneyBoxEvent(false));
            }
        }), 1000*2L, 1*1000*2L);
        moneyBoxtimer.initMyTimer().startMyTimer();
    }

    private static final String TAG = "UpdateService";
    public void getSystemTime(String url){
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("DeviceID",Config.DEVICE_ID);
        params.addBodyParameter("IsConnect",connected ? "true" : "false");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                BaseHttpResult base = JSON.parseObject(result, BaseHttpResult.class);
                if(base.getMsgType().equals("Success") && base.getData() != null && !base.getData().equals("")){
                    HeartBeatResult heartBeatResult = JSON.parseObject(base.getData(),HeartBeatResult.class);
                    boolean isCloseModle = heartBeatResult.IsOpenPrintModel.equals(SavePasswd.CLOSEPRINT);
                    setIsClosePrintMode(isCloseModle);
                    if(heartBeatResult.getIsNetPrintUploadToService() != null && heartBeatResult.getIsNetPrintUploadToService().equals(SavePasswd.CLOSE)){
                        setCloseNetPrinterUploadToService(true);
                    }else{
                        setCloseNetPrinterUploadToService(false);
                    }
                }
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

    public void setIsClosePrintMode(boolean isClose){
        if(isClose){
            SavePasswd.getInstace().save(SavePasswd.ISOPENPRINT,SavePasswd.CLOSEPRINT);
            SavePasswd.getInstace().savexml(SavePasswd.ISOPENPRINT,SavePasswd.CLOSEPRINT);
        }else {
            SavePasswd.getInstace().save(SavePasswd.ISOPENPRINT,SavePasswd.OPENPRINT);
            SavePasswd.getInstace().savexml(SavePasswd.ISOPENPRINT,SavePasswd.OPENPRINT);
        }
    }

    public void setCloseNetPrinterUploadToService(boolean isClose){
        if(isClose){
            SavePasswd.getInstace().save(SavePasswd.ISNETPRINTUPLOADTOSERVICE,SavePasswd.CLOSE);
            SavePasswd.getInstace().savexml(SavePasswd.ISNETPRINTUPLOADTOSERVICE,SavePasswd.CLOSE);
        }else {
            SavePasswd.getInstace().save(SavePasswd.ISNETPRINTUPLOADTOSERVICE,SavePasswd.OPEN);
            SavePasswd.getInstace().savexml(SavePasswd.ISNETPRINTUPLOADTOSERVICE,SavePasswd.OPEN);
        }
    }


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

    public  void copyCfg(String picName) {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/"+picName;
        FileOutputStream os = null;
        InputStream is = null;
        int len = -1;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("assets/"+picName);
            os = new FileOutputStream(dirPath);
            byte b[] = new byte[1024];
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
            is.close();
            os.close();
        } catch (Exception e) {
        }
    }

}
