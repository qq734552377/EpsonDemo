package com.ucast.jnidiaoyongdemo.Model;

import android.os.SystemClock;

import com.ucast.jnidiaoyongdemo.mytime.MyTimeTask;
import com.ucast.jnidiaoyongdemo.mytime.MyTimer;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;


import java.util.ArrayList;
import java.util.List;


public class ListPictureQueue {

    private static List<PictureModel> list = new ArrayList();

    private static MyTimer timer;



    public static void StartTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            public void run() {
                synchronized (ListPictureQueue.class) {
                    if (list.size() <= 0)
                        return;
                    PictureModel info = list.get(0);
                    long time = (long) (SystemClock.elapsedRealtime() - info.getOutTime()) / 1000;
                    if (time < 8)
                        return;
                    SendAgain(true);
                }
            }

        }), 2000L, 2000L);
        timer.initMyTimer().startMyTimer();
    }

    public static void Remove() {
        if (list.size() <= 0) {
            return;
        }
        list.remove(0);
    }
    public static void safeRemove() {
        synchronized (ListPictureQueue.class) {
            if (list.size() <= 0) {
                return;
            }
            PictureModel info = list.get(0);
            int baoSize = info.BufferPicture.size();
            int curIndex = info.getCurtNum();
            if (baoSize == curIndex) {
                list.remove(0);
                SendAgain(false);
            }else{
                SendAgain(true);
            }
        }
    }

    public static void Clean() {
        synchronized (ListPictureQueue.class) {
            if (list.size() <= 0)
                return;
            list.remove(0);
        }
    }

    public static void EndTime() {
        if (timer == null)
            return;
        timer.stopMyTimer();
    }

    public static void Add(PictureModel model) {
        synchronized (ListPictureQueue.class) {
            if (list.size() <= 0) {
                list.add(model);
                SendFirst();
            }else {
                list.add(model);
            }
        }
    }


    public static void SendFirst() {
            if (list.size() <= 0)
                return;
            PictureModel info = list.get(0);
            if (info == null ) {
                return;
            }
            if(info.isCutPapper() && info.BufferPicture.size() <= 0){
                SendPackage.sendToPrinter(PrinterProtocol.getPrinterCutPaperProtocol());
                ExceptionApplication.gLogger.info("info cutPapper is true --- > " );
                return;
            }
            byte[] str = info.BufferPicture.get(0);
            info.setOutTime(SystemClock.elapsedRealtime());
            SendPackage.sendToPrinter(str);
//        ExceptionApplication.gLogger.info(" send First Picture first time： -->" + System.currentTimeMillis());
//            ExceptionApplication.gLogger.info(" send First Picture first time： " +  MyTools.millisToDateString(System.currentTimeMillis()));
//            ExceptionApplication.gLogger.info(EpsonParseDemo.printHexString(str));
//            for (int i = 0; i < info.BufferPicture.size(); i++) {
//                byte[] str = info.BufferPicture.get(i);
//                info.setCurtNum(i + 1);
//                info.setOutTime(SystemClock.elapsedRealtime());
//                SendPackage.sendToPrinter(str);
//                ExceptionApplication.gLogger.info((i+ 1) + " --> cur pakage  " +  System.currentTimeMillis());
//                try{
//                    Thread.sleep(52);
//                }catch (Exception e){
//
//                }
//                if (i == info.BufferPicture.size() - 1 ){
//                    SendPackage.sendToPrinter(PrinterProtocol.getPrinterCutPaperProtocol());
//                    try{
//                        Thread.sleep(1997);
//                    }catch (Exception e){
//
//                    }
//                    Remove();
//                }
//            }

    }

    private static void SendAgain(boolean isSendAgain) {
        if (list.size() <= 0)
            return;
        PictureModel info = list.get(0);
        if (info == null) {
            return;
        }
        if(info.isCutPapper() && info.BufferPicture.size() <= 0){
            SendPackage.sendToPrinter(PrinterProtocol.getPrinterCutPaperProtocol());
            ExceptionApplication.gLogger.info("info cutPapper is true --- > " );
            return;
        }
        byte[] str = info.BufferPicture.get(0);
        info.setOutTime(SystemClock.elapsedRealtime());
        SendPackage.sendToPrinter(str);
        if(isSendAgain) {
//            ExceptionApplication.gLogger.info(" send same Picture first package");
        }else {
//            ExceptionApplication.gLogger.info(" send next Picture first package");
        }
    }

    public static void SendByIndex(int index) {
        synchronized (ListPictureQueue.class) {
            ResultSend(index);
        }
    }

    public static void ResultSend(int index) {
        if (list.size() <= 0 || index <= 0)
            return;
        try {
            PictureModel info = list.get(0);
            if(info.isCutPapper() && info.BufferPicture.size() <= 0){
                SendPackage.sendToPrinter(PrinterProtocol.getPrinterCutPaperProtocol());
                ExceptionApplication.gLogger.info("info cutPapper is true --- > " );
                return;
            }
            int baoSize = info.BufferPicture.size();
            //发送打印第1包 包序号号为0    返回  index=2 准备打印第2包 包序号为1
            if (index > 0 && baoSize >= index) {
                byte[] buffer = info.BufferPicture.get(index - 1);
                //设置打印的当前包序号
                info.setCurtNum(index);
                info.setOutTime(SystemClock.elapsedRealtime());
                SendPackage.sendToPrinter(buffer);
            }
            if (baoSize == index) {
                if (list.size() <= 0)
                    return;
                //打完一张图片切纸
                if (info.isCutPapper()) {
                    SendPackage.sendToPrinter(PrinterProtocol.getPrinterCutPaperProtocol());
                }else {
                    list.remove(0);
                    SendAgain(false);
                }
//                Channel channel = info.getChannel();
//                if(channel!= null && channel.isActive()){
//                    //告诉客户端打印完成
//                    byte[] appdata = Common.GetFormat("1201", 1, 1, new String[]{true ? "0" : "1"});
//                    SendPackage.ChannelSendBuffer(channel,appdata);
//                }

                //改为在收到切纸回复后  发送下一张图片
//                Remove();
//                SendNext();
                return;
            }

            if (baoSize < index | index < 0) {
                return;
            }
        } catch (Exception e) {
        }
    }

}
