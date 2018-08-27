package com.ucast.jnidiaoyongdemo.testActs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.R;
import com.ucast.jnidiaoyongdemo.Serial.SerialTest;
import com.ucast.jnidiaoyongdemo.erweima.view.mysaomiao.CaptureActivity;
import com.ucast.jnidiaoyongdemo.jsonObject.BaseHttpResult;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyDialog;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.ResponseEntity;
import com.ucast.jnidiaoyongdemo.tools.WiFiUtil;
import com.ucast.jnidiaoyongdemo.tools.WifiConnect;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;
import com.ucast.jnidiaoyongdemo.xutilEvents.MsCardEvent;
import com.ucast.jnidiaoyongdemo.xutilEvents.Serial_huihuanEvent;
import com.ucast.jnidiaoyongdemo.xutilEvents.WIfiEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ContentView(R.layout.activity_test_main)
public class TestMainActivity extends BaseNavActivity {
    public final String TEST_TIME= "test_time";
    public final String DEVICE_ID= "device_id";
    public final String BLUEBOOTH_TEST= "bluetooth";
    public final String WIFI= "wifi";
    public final String TWO_ETH= "two_eth";
    public final String SERIAL= "serial";
    public final String MSCARD= "mscard";
    public final String MONEYBOX= "moneybox";
    public final String PRINTER= "printer";
    public final String USB_P= "usb_p";
    public final String QIAN_CAMERA= "qian_camera";
    public final String CE_CAMERA= "ce_camera";

    public String[] allTestItems = {BLUEBOOTH_TEST,WIFI,TWO_ETH,SERIAL,MSCARD,
                                    MONEYBOX,PRINTER,USB_P,QIAN_CAMERA,CE_CAMERA
                                    };


    public final String TEST_SUCCESS = "测试通过";
    public final String TEST_FAIL = "测试失败";
    public final String TEST_NO= "没有测试";

    public final String WIFI_NAME= "tendaucast";
    public final String WIFI_PWD= "1234567890";
    public final String SERIAL_TEST_STR= "/dev/ttymxc2 test";

    public ProgressDialog progressDialog;
    public ProgressDialog msProgressDialog;
    public ProgressDialog uploadResultProgressDialog;
    public Dialog moneyboxDialog;
    public Dialog printerboxDialog;
    public Dialog usb_p_Dialog;
    public Dialog two_ethDialog;

    public SerialTest serial_huihuan;


    public ThreadPoolExecutor poolExecutor;
    public static boolean isAutoTest = false;

    public boolean isSerialTestSuccess = false;

    public WifiManager manager;
    public WifiConnect wifiConnect;

    @ViewInject(R.id.tv_deviceId)
    TextView tv_deviceID;
    @ViewInject(R.id.tv_showMessage)
    TextView tv_showMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initToolbar(getString(R.string.testDevice));
        poolExecutor = new ThreadPoolExecutor(1, 3,
                0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(128));
        EventBus.getDefault().register(this);
        tv_deviceID.setText(Config.DEVICE_ID);

        manager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiConnect=new WifiConnect(manager);
        progressDialog = MyDialog.createProgressDialog(this,"测试中...");
        msProgressDialog = MyDialog.createProgressDialog(this,"磁条卡测试，请刷卡");
        uploadResultProgressDialog = MyDialog.createProgressDialog(this,"正在上传测试结果请稍等...");
        for (int i = 0; i < allTestItems.length; i++) {
            save.save(allTestItems[i],TEST_NO);
        }
    }
    @Event(R.id.bt_bluetooth)
    private void testWBlueTooth(View v){
        testBluetoothFunc();
    }

    @Event(R.id.bt_wifi)
    private void testWifi(View v){
        testWiFiFunc();
    }

    @Event(R.id.bt_two_eth)
    private void testTwo_Eth(View v){
        testTwo_EthFunc();
    }

    @Event(R.id.bt_serial)
    private void testSerial(View v){
        testSerialFunc();
    }

    @Event(R.id.bt_msCard)
    private void testMscard(View v){
        testMsCardFunc();
    }

    @Event(R.id.bt_money)
    private void testMoneyBox(View v){
        testMoneyBoxFunc();
    }

    @Event(R.id.bt_printer)
    private void testPrinter(View v){
        testPrinterFunc();
    }

    @Event(R.id.bt_usb_p)
    private void testUSB_P(View v){
        testP_USBFunc();
    }

    //前面摄像头测试
    @Event(R.id.bt_qian_camera)
    private void testQianmianCamera(View v){
        testQianCameraFunc();
    }
    //侧面摄像头测试
    @Event(R.id.bt_cemian_camera)
    private void testCemianCamera(View v){
        testCeCameraFunc();
    }
    //自动测试
    @Event(R.id.bt_start_test)
    private void autoTest(View v){
        isAutoTest = true;
        testBluetoothFunc();
    }

    //保存并退出
    @Event(R.id.bt_save_exit)
    private void save_exit(View v){
        upLoadTestResult();
        StringBuilder sb = new StringBuilder();
        sb.append("测试时间:" + MyTools.millisToDateString(System.currentTimeMillis()) + "\n");
        sb.append("设备MAC:" + Config.DEVICE_ID + "\n");
        for (int i = 0; i < allTestItems.length; i++) {
            String result = save.getIp(allTestItems[i],TEST_NO);
            sb.append(allTestItems[i] + ":" + result + "\n");
        }
        String path = Config.TESTRESULTDIR + "/测试结果_" + Config.DEVICE_ID + ".txt";
        MyTools.writeToFileNoappend(path,sb.toString());
    }

    //测试蓝牙
    public void testBluetoothFunc(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isOK = false;
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                isOK = bluetoothAdapter.enable();
            } else {
                isOK = true;
            }
        }
        setTestResult(isOK,R.id.iv_blutooth,BLUEBOOTH_TEST);
    }

    //测试WIFI
    public void testWiFiFunc(){
        progressDialog.show();
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                connectWifi();
                boolean result = pingNet();
                EventBus.getDefault().post(new WIfiEvent(result));
            }
        });
    }

    //测试两个网口
    public void testTwo_EthFunc(){
        if (two_ethDialog == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("请测试两个网口，是否都可用？");
            builder.setPositiveButton("都可用", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    wifiConnect.openWifi();
                    setTestResult(true,R.id.iv_two_eth,TWO_ETH);
                }
            });
            builder.setNegativeButton("不可用", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    wifiConnect.openWifi();
                    setTestResult(false,R.id.iv_two_eth,TWO_ETH);
                }
            });
            two_ethDialog = builder.create();
        }
        wifiConnect.closeWifi();
        two_ethDialog.show();
    }

    //测试串口
    public void testSerialFunc(){
        progressDialog.show();
        isSerialTestSuccess = false;
        startTestSerial();
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (!isSerialTestSuccess)
                        EventBus.getDefault().post(new Serial_huihuanEvent("失败",false));
                }catch (Exception e){

                }
            }
        });
    }
    //测试磁条卡
    public void testMsCardFunc(){
        msProgressDialog.show();
    }
    //测试钱箱
    public void testMoneyBoxFunc(){
        MyTools.openMoneyBox();
        if (moneyboxDialog == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("钱箱是否打开？");
            builder.setPositiveButton("打开了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setTestResult(true,R.id.iv_money,MONEYBOX);
                }
            });
            builder.setNegativeButton("没打开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setTestResult(false,R.id.iv_money,MONEYBOX);
                }
            });
            moneyboxDialog = builder.create();
        }
        moneyboxDialog.show();
    }
    //测试打印机
    public void testPrinterFunc(){
        String path = Environment.getExternalStorageDirectory().getPath() + "/ucast.bmp";
        ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path),true));
        if (printerboxDialog == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("打印机是否打印？");
            builder.setPositiveButton("打印了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setTestResult(true,R.id.iv_printer,PRINTER);
                }
            });
            builder.setNegativeButton("没打印", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setTestResult(false,R.id.iv_printer,PRINTER);
                }
            });
            printerboxDialog = builder.create();
        }
        printerboxDialog.show();
    }
    //测试打印USB口
    public void testP_USBFunc(){
        if (usb_p_Dialog == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("请从电脑发送打印信息，打印机是否打印？");
            builder.setPositiveButton("打印了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setTestResult(true,R.id.iv_usb_py, USB_P);
                }
            });
            builder.setNegativeButton("没打印", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setTestResult(false,R.id.iv_usb_py, USB_P);
                }
            });
            usb_p_Dialog = builder.create();
        }
        usb_p_Dialog.show();
    }

    //测试前置摄像头
    public void testQianCameraFunc() {
        startAc(1);
    }

    //测试侧面摄像头
    public void testCeCameraFunc() {
        startAc(0);
    }

    private void connectWifi() {
        if (!manager.isWifiEnabled()){
            wifiConnect.openWifi();
            try {
                Thread.sleep(8000);
            }catch (Exception e){

            }
        }
        if (!WiFiUtil.isWifiConnect()){
            wifiConnect.connect(WIFI_NAME,WIFI_PWD, WifiConnect.WifiCipherType.WIFICIPHER_WPA);
        }
        if (WiFiUtil.isWifiConnect() && WIFI_NAME.equals(WiFiUtil.getNowWiFiSSID())){

        }else{
            wifiConnect.connect(WIFI_NAME,WIFI_PWD, WifiConnect.WifiCipherType.WIFICIPHER_WPA);
        }
    }

    public boolean pingNet(){
        boolean result = false;
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + "www.baidu.com");
            int status = p.waitFor();
            if (status == 0) {
                result = true;
            }
        } catch (IOException e) {

        } catch (InterruptedException e) {

        }finally {
            return result;
        }

    }

    public void startTestSerial(){
        if (serial_huihuan == null) {
            String path = "/dev/ttymxc2";
            serial_huihuan = new SerialTest(path);
            serial_huihuan.Open();
        }
        serial_huihuan.Send(SERIAL_TEST_STR);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void handleWifiTestResult(WIfiEvent data) {
        progressDialog.dismiss();
        setTestResult(data.isResult(),R.id.iv_wifi,WIFI);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void handleSerialResult(Serial_huihuanEvent data) {
        if (!data.isFromSerial()){
            progressDialog.dismiss();
            setTestResult(false, R.id.iv_serial, SERIAL);
            return;
        }
        isSerialTestSuccess = true;
        String str = data.getMsg();
        boolean isOK = str!=null && !str.equals("") && SERIAL_TEST_STR.contains(str);
        setTestResult(isOK,R.id.iv_serial,SERIAL);
    }
    public int msFlag = 0;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void handleMScardResult(MsCardEvent data) {
        int type = data.getType();
        String msg = data.getMsg();
        switch (type){
            case 1:
                msFlag = 0;
                if (msg != null && !msg.equals("")) {
                    msFlag += 1;
                    return;
                }
                showToast("1轨没数据");
                break;
            case 2:
                if (msg != null && !msg.equals("")) {
                    msFlag += 1;
                    return;
                }
                showToast("2轨没数据");
                break;
            case 3:
                if (msg != null && !msg.equals("")) {
                    msFlag += 1;
                }else {
                    showToast("3轨没数据");
                }
                boolean isOK = false;
                if (msFlag == 3)
                    isOK = true;
                msProgressDialog.dismiss();
                setTestResult(isOK,R.id.iv_msCard,MSCARD);
                break;
        }
    }


    public void setTestResult(boolean isOK,int id,String key){
        if (isOK) {
            setViewBackgronudSuccess(id);
            save.save(key ,TEST_SUCCESS );
        } else {
            setViewBackgronudFail(id);
            save.save(key ,TEST_FAIL );
        }

        switch (id){
            case R.id.iv_blutooth:
                if (isAutoTest)
                    testWiFiFunc();
                break;
            case R.id.iv_wifi:
                if (isAutoTest)
                    testTwo_EthFunc();
                break;
            case R.id.iv_two_eth:
                if (isAutoTest)
                    testSerialFunc();
                break;
            case R.id.iv_serial:
                if (isAutoTest)
                    testMsCardFunc();
                break;
            case R.id.iv_msCard:
                if (isAutoTest)
                    testMoneyBoxFunc();
                break;
            case R.id.iv_money:
                if (isAutoTest)
                    testPrinterFunc();
                break;
            case R.id.iv_printer:
                if (isAutoTest)
                    testP_USBFunc();
                break;
            case R.id.iv_usb_py:
                if (isAutoTest)
                    testQianCameraFunc();
                break;
            case R.id.iv_qian_camera:
                if (isAutoTest)
                    testCeCameraFunc();
                break;
            case R.id.iv_cemian_camera:
                isAutoTest = false;
                showToast("记得保存测试结果哦！");
                break;

        }

    }

    public void startAc(int type) {
        Intent intent = new Intent(TestMainActivity.this, CaptureActivity.class);
        intent.putExtra(CaptureActivity.CAMERAKEY, type);
        startActivityForResult(intent, type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (serial_huihuan != null)
            serial_huihuan.Dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        String r = "";
        boolean isOK;
        switch (resultCode) {
            case 0://侧面摄像头
                r = data.getStringExtra(CaptureActivity.RESULT);
                showToast(r);
                isOK = !r.equals("") && r.length() >= 5;
                setTestResult(isOK,R.id.iv_cemian_camera,CE_CAMERA);
                break;
            case 1://前面摄像头
                r = data.getStringExtra(CaptureActivity.RESULT);
                showToast(r);
                isOK = !r.equals("") && r.length() >= 5;
                setTestResult(isOK,R.id.iv_qian_camera,QIAN_CAMERA);
                break;
        }
    }

    public void upLoadTestResult(){
        if ( !MyTools.isNetworkAvailable(ExceptionApplication.getInstance())){
            showToast("网络不可用，请连接网络！");
            return;
        }
        RequestParams params = new RequestParams(YinlianHttpRequestUrl.TEST_RESULT_UPLOAD_URL);
        params.addBodyParameter(TEST_TIME,MyTools.millisToDateString(System.currentTimeMillis()));
        params.addBodyParameter(DEVICE_ID,Config.DEVICE_ID);
        params.setConnectTimeout(1000 * 60);
        for (int i = 0; i < allTestItems.length; i++) {
            String result = save.getIp(allTestItems[i],TEST_NO);
            params.addBodyParameter(allTestItems[i],result);
        }
        uploadResultProgressDialog.show();
        x.http().post(params, new Callback.CommonCallback<ResponseEntity>() {

                @Override
                public void onSuccess(ResponseEntity result) {
                    BaseHttpResult base = JSON.parseObject(result.getResult(), BaseHttpResult.class);
                    if(base.getMsgType().equals(BaseHttpResult.SUCCESS)){
                        showToast("上传测试结果成功");
                        TestMainActivity.this.finish();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    showToast("上传测试结果失败");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    uploadResultProgressDialog.dismiss();
                }
        });

    }

}
