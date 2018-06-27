package com.ucast.jnidiaoyongdemo.queue_ucast;

import android.os.SystemClock;

import com.ucast.jnidiaoyongdemo.Model.UploadData;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;
import com.ucast.jnidiaoyongdemo.db.UploadDBHelper;
import com.ucast.jnidiaoyongdemo.mytime.MyTimeTask;
import com.ucast.jnidiaoyongdemo.mytime.MyTimer;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pj on 2018/3/30.
 */

public class UploadDataQueue {
    private static List<UploadData> list = new ArrayList();

    private static MyTimer timer;

    private static boolean onOff;

    private static long lastUploadTime = 0L;

    public static void StartTimer() {
        // 初始化所有没有上传的数据
        initUploadList();

        timer = new MyTimer(new MyTimeTask(new Runnable() {
            public void run() {
                synchronized (UploadDataQueue.class) {

                    if (list.size() <= 0)
                        return;
                    long time = (long) (SystemClock.elapsedRealtime() - lastUploadTime) / 1000;
                    if (time < 60) {
                        return;
                    }
                    sendAgain();
                }
            }
        }), 2000L, 14000L);
        timer.initMyTimer().startMyTimer();
    }

    private static void initUploadList(){
        UploadDBHelper.getInstance().deleteUploadSuccessData();
        List<UploadData> temList = UploadDBHelper.getInstance().selectAll();
        if (temList != null){
            list.addAll(temList);
        }
        deleteDirectory(new File(EpsonPicture.TEMPBITPATH));
    }
    //删除文件夹
    private static void deleteDirectory(File file) {
        if (!file.exists()){
            file.mkdir();
            return;
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            f.delete();
        }
    }

    public static void removeOne() {
        if (list.size() <= 0) {
            return;
        }
        list.remove(0);
    }

    private static void sendAgain() {
        lastUploadTime = SystemClock.elapsedRealtime();
        if (list.size() <= 0)
            return;
        UploadData oneData = list.get(0);
        if (oneData != null){
            uploadOneData(oneData);
        }
    }

    private static void uploadOneData(UploadData one){
        if (one.getData() == null && !new File(one.getPath()).exists()){
            UploadDBHelper.getInstance().updateIsUploadById(true,one.getId());
            removeOne();
            sendAgain();
            return;
        }
        if (one.getType() == UploadData.PATH_TYPE){
            MyTools.uploadFile(one.getPath(), YinlianHttpRequestUrl.UPLOADFILEURL);
        }else{
            if (one.getUpLoadURL() == null) {
                MyTools.uploadData(one.getData(), YinlianHttpRequestUrl.UPLOADBASE64URL);
            }else{
                MyTools.uploadDataWithFile(one.getData(),one.getPath(),YinlianHttpRequestUrl.UPLOADBASE64URL);
            }
        }
    }

    public static void sendNextByResult(boolean result){
        synchronized (UploadDataQueue.class){
            if (list.size() <= 0)
                return;
            if (result){
                UploadData oneData = list.get(0);
                if (oneData != null && oneData.getId() != -1){
                    UploadDBHelper.getInstance().updateIsUploadById(true,oneData.getId());
                }
                removeOne();
                sendAgain();
            }else {
                try {
                    Thread.sleep(30000);
                    sendAgain();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addOneDataToList(UploadData model) {
        synchronized (UploadDataQueue.class) {
            if (list.size() <= 0) {
                list.add(model);
                sendAgain();
            }else {
                list.add(model);
            }
            int id = (int)UploadDBHelper.getInstance().insertOneUploadData(model);
            model.setId(id);
        }
    }

    public static int getListSize() {
        synchronized (UploadDataQueue.class) {
            int s = list.size();
            return s;
        }
    }

    public static void EndTimer() {
        if (timer == null)
            return;
        timer.stopMyTimer();
    }
}
