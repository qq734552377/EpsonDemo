package com.ucast.jnidiaoyongdemo.tools;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Model.UploadData;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.jsonObject.TicketMsgResult;
import com.ucast.jnidiaoyongdemo.queue_ucast.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.queue_ucast.UploadDataQueue;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;
import com.ucast.jnidiaoyongdemo.jsonObject.BaseHttpResult;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by pj on 2016/11/23.
 */
public class MyTools {
    public static final String MONEYBOXFILEPATH = "/sys/devices/platform/avrctl/moneybox";
    public static final String CEMIANCAMERAFILEPATH = "/sys/bus/i2c/drivers/ov564x/vcm";
    public static final String ZHENGMIANCAMERAFILEPATH = "/sys/bus/i2c/drivers/ov564x_mipi/vcm";

    public MyTools() {
    }

    public static Date StringToDate(String s) {
        Date time = null;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            time = sd.parse(s);
        } catch (java.text.ParseException e) {
            System.out.println("输入的日期格式有误！");
            e.printStackTrace();
        }
        return time;
    }


    public static long getIntToMillis(String str) {
        String str_date = str + " " + "00:00:00";
        Date date = StringToDate(str_date);
        return date.getTime();
    }

    public static String millisToDateString(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date;
        Date curDate = new Date(time);
        date = formatter.format(curDate);
        return date;
    }


    public static String millisToDateStringNoSpace(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String date;
        Date curDate = new Date(time);
        date = formatter.format(curDate);
        return date;
    }
    public static String millisToDateStringOnlyYMD(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date;
        Date curDate = new Date(time);
        date = formatter.format(curDate);
        return date;
    }


    public static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024]; int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
    /** Get the STB MacAddress*/
    public static String getMacAddress(){
        try {
            return loadFileAsString("/sys/class/net/eth0/address") .toUpperCase().substring(0, 17).replace(':','-');
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap generateBitmap(String content,int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void downloadFileUsetoPrint(final String url, String path) {
        if ( !isNetworkAvailable(ExceptionApplication.getInstance())){
            return;
        }
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
            @Override
            public void onSuccess(File result) {
                String path = isExitInSdcard(url);
                if (path != null){
                    ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path), true));
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();

            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    public static String isExitInSdcard(String url){
        String path = Config.PICPATHDIR + url.substring(url.lastIndexOf("/") + 1) ;
        File file = new File(path);
        if (!file.exists()){
            downloadFileUsetoPrint(url,path);
            return null;
        }else{
            return path;
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void uploadFileByQueue(String path){
        UploadData one = new UploadData();
        one.setType(UploadData.PATH_TYPE);
        one.setPath(path);
        one.setUpLoadURL(YinlianHttpRequestUrl.UPLOADFILEURL);
        one.setMsg_create_time(MyTools.millisToDateString(System.currentTimeMillis()));
        UploadDataQueue.addOneDataToList(one);
    }
    public static void uploadDataAndFileWithURLByQueue(String data,String path){
        UploadData one = new UploadData();
        one.setType(UploadData.DATA_TYPE);
        one.setPath(path);
        one.setUpLoadURL(YinlianHttpRequestUrl.UPLOADBASE64URL);
        one.setData(data);
        one.setMsg_create_time(MyTools.millisToDateString(System.currentTimeMillis()));
        UploadDataQueue.addOneDataToList(one);
    }

    public static void uploadOneUploadData(UploadData one){
        if ( !isNetworkAvailable(ExceptionApplication.getInstance())){
            return;
        }
        RequestParams params = null;
        if (one.getType() == UploadData.PATH_TYPE){
            params = new RequestParams(YinlianHttpRequestUrl.UPLOADFILEURL);
        }else{
            params = new RequestParams(YinlianHttpRequestUrl.UPLOADBASE64URL);
            params.addBodyParameter("Content",one.getData());
        }
        params.setMultipart(true);
        params.addBodyParameter("work_order_image",new File(one.getPath()));
        params.addBodyParameter("Time",one.getMsg_create_time());
        params.addBodyParameter("Imei", Config.DEVICE_ID);
        x.http().post(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity o) {
                handleTicket(o.getResult());
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
//                UploadDataQueue.sendNextByResult(false);
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static void handleTicket(String result){
        EventBus.getDefault().postSticky(result);
        BaseHttpResult base = JSON.parseObject(result, BaseHttpResult.class);
        if (base.getMsgType().equals(BaseHttpResult.SUCCESS))
            UploadDataQueue.sendNextByResult(true);

        if (base.getMsgType().equals(BaseHttpResult.SUCCESS) && base.getData() != null && !base.getData().equals("")){
            TicketMsgResult ticketMsgResult = JSON.parseObject(base.getData(),TicketMsgResult.class);
            double moneyD = Double.parseDouble(ticketMsgResult.getAmount());
            String printMsg = ticketMsgResult.getStr();
            String linkMsg = ticketMsgResult.getLink();
            String bmpUrlMsg = ticketMsgResult.getImg();
            try {
                int money = (int) moneyD;
                if (money >= 100) {
                    String printLocalMsg = "您本次已消费" + money + "元，免费获得" + (money / 100) + ".0小时停车券。\n欢迎下次光临！\n" +
                            "\n";
                    Bitmap b = EpsonPicture.getBitMapByStringReturnBigBitmap(printLocalMsg);
                    String path = Environment.getExternalStorageDirectory().getPath() + "/ums.bmp";
                    ;
                    ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(b, false));
                    ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path), true));
                    return;
                }
                Bitmap strBmp = null;
                Bitmap linkBmp = null;
                if (printMsg != null && !printMsg.equals("")){
                    strBmp = EpsonPicture.getBitMapByStringReturnBigBitmap(printMsg);
                }
                if (linkMsg != null && !linkMsg.equals("")){
                    linkBmp = MyTools.generateBitmap(linkMsg, SomeBitMapHandleWay.PRINT_WIDTH,SomeBitMapHandleWay.PRINT_WIDTH);
                    if (strBmp != null)
                        ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(strBmp, false));
                    if (linkBmp != null)
                        ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(linkBmp, true));
                }
                if (bmpUrlMsg != null && !bmpUrlMsg.equals("")){
                    String path = isExitInSdcard(bmpUrlMsg);
                    if (path != null){
                        ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path), true));
                    }
                }
            }catch (Exception e){

            }
        }
    }

    public static void openMoneyBox(){
        String filePath = MONEYBOXFILEPATH;
        sendOrderToDeviceFile(filePath,"on");
    }

    public static void setCameraFixed(){
        String filePath = ZHENGMIANCAMERAFILEPATH;
        sendOrderToDeviceFile(filePath,"700");
    }
    public static void setCameraAuto(){
        String filePath = ZHENGMIANCAMERAFILEPATH;
        sendOrderToDeviceFile(filePath,"0");
    }

    public static void writeToFile(String path , String data){
        try{
            File f = new File(path);
            FileOutputStream fout = new FileOutputStream(f , true);
            BufferedOutputStream buff = new BufferedOutputStream(fout);
            buff.write((data + "\r\n").getBytes());
            buff.flush();
            buff.close();
        }catch (Exception e){

        }
    }

    public static void writeToFileNoappend(String path , String data){
        try{
            File f = new File(path);
            FileOutputStream fout = new FileOutputStream(f , false);
            BufferedOutputStream buff = new BufferedOutputStream(fout);
            buff.write((data + "\r\n").getBytes());
            buff.flush();
            buff.close();
        }catch (Exception e){

        }
    }

    public static void sendOrderToDeviceFile(String filePath,String order){
        File f = new File(filePath);
        if (!f.exists())
            return;
        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(order.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*** 获取文件大小 ***/
    public static long getFileSizes(String apkPath) throws Exception {
        File f=new File(apkPath);
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    /*** 获取apk文件的的版本号 ***/
    public static String getApkversion(){
        File f=new File(ApkInfo.apkPath);
        if(!f.exists()){
            return "0.0";
        }
        PackageInfo packageInfo;
        try {
            PackageManager packageManager = ExceptionApplication.getInstance().getPackageManager();
            packageInfo = packageManager.getPackageArchiveInfo(ApkInfo.apkPath, PackageManager.GET_ACTIVITIES);
        }catch (Exception e){
            return "1.0";
        }
        return packageInfo == null ? "0.0" :  packageInfo.versionName;
    }
    /** 静默安装apk文件*/
    public static void install(String path) {
        try {
            Uri uri = Uri.fromFile(new File(path));
            PackageManager pm = ExceptionApplication.getInstance().getPackageManager();
            MyPakcageInstallObserver observer = new MyPakcageInstallObserver();
            //获取方法中的参数类型列表
            Class<?> param[] = getParamTypes(pm.getClass(), "installPackage");
            //获取方法
            Method method = pm.getClass().getDeclaredMethod("installPackage", param);
            //方法的调用
            method.invoke(pm, uri, observer, 0, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /** 静默s删除apk文件*/
    public static void detele(String packageName) {
        try {
            PackageManager pm = ExceptionApplication.getInstance().getPackageManager();
            MyPakcageDeleteObserver observer = new MyPakcageDeleteObserver();
            Class<?> param[] = getParamTypes(pm.getClass(), "deletePackage");
            Method method = pm.getClass().getDeclaredMethod("deletePackage", param);//
            method.invoke(pm, packageName, observer, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static Class<?>[] getParamTypes(Class<?> cls, String mName) {
        Class<?> cs[] = null;
        Method[] mtd = cls.getMethods();
        for (int i = 0; i < mtd.length; i++) {
            if (!mtd[i].getName().equals(mName)) {
                continue;
            }
            cs = mtd[i].getParameterTypes();
        }
        return cs;
    }


    public static class MyPakcageDeleteObserver extends android.content.pm.IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode) throws RemoteException {
            if (returnCode == 1) {
                System.out.println("删除laile");

            } else {
                System.out.println("删除失败,返回码是");

            }
        }
    }


    public static class MyPakcageInstallObserver extends android.content.pm.IPackageInstallObserver.Stub {
        public void packageInstalled(String packageName, int returnCode) {
            Message msg = Message.obtain();
            if (returnCode == 1) {
                Log.e("", "packageInstalled 安装成功");

            } else {
                Log.e("", "packageInstalled 安装失败");

            }
        }
    }


    /**
     * 将屏幕旋转锁定
     */
    public static int setRoat(Context context){
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        //得到是否开启
        int flag = Settings.System.getInt(context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
        return  flag;
    }


    /**
     * 将assets目录下的cfg.xml拷入到SD卡中
     */
    public static void copyCfg() {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/cfg.xml";
        FileOutputStream os = null;
        InputStream is = null;
        int len = -1;
        try {
            is =  ExceptionApplication.getInstance().getClass().getClassLoader().getResourceAsStream("assets/cfg.xml");
            os = new FileOutputStream(dirPath);
            byte b[] = new byte[1024];

            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }

            is.close();
            os.close();
        } catch (Exception e) {
            Log.e(ContentValues.TAG, "copyCfg: 写入失败");
        }
    }

    /**
     * 将文件从assets目录拷入到SD卡中
     */
    public static boolean retrieveApkFromAssets(Context context, String fileName, String path) {
        boolean bRet = false;

        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                file.createNewFile();
                InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/" + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] temp = new byte[1024];
                int i = 0;
                while ((i = is.read(temp)) != -1) {
                    fos.write(temp, 0, i);
                }
                fos.flush();
                fos.close();
                is.close();
                bRet = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bRet;
    }

    /**
     * 设置为中文环境
     */
    public  static boolean SettingLanguage() {
        try {
            Class amnClass = Class.forName("android.app.ActivityManagerNative");
            Method methodGetDefault = amnClass.getMethod("getDefault");
            Object amn = methodGetDefault.invoke(amnClass);
            Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
            Configuration config = (Configuration) methodGetConfiguration.invoke(amn);
            Class configClass = config.getClass();
            Field f = configClass.getField("userSetLocale");
            f.setBoolean(config, true);
            if(config.locale==Locale.CHINA){
            	return false;
            }
            config.locale = Locale.CHINA;
            Method methodUpdateConfiguration = amnClass.getMethod("updateConfiguration", Configuration.class);
            methodUpdateConfiguration.invoke(amn, config);
            BackupManager.dataChanged("com.android.providers.settings");
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;

    }
    /***
     * 获取内核版本号
     */
    public static String getLinuxKernalInfo() {
        Process process = null;
        String mLinuxKernal = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
                // result += "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.toString();
    }

}
