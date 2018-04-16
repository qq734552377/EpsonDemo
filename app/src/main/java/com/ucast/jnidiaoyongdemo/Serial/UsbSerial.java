package com.ucast.jnidiaoyongdemo.Serial;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Message;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.bmpTools.PrintAndDatas;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.tools.ArrayQueue;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import android.os.Handler;



/**
 * Created by pj on 2016/1/22.
 */
public class UsbSerial {
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private String Path = "/dev/g_printer0";
    private boolean mDispose;
//    private String sBuffer;
    private StringBuilder sBuildBuffer;
    private byte[] Buffer;
    private byte[] allBufferDatas;
    //SerialMessage message;
    private ArrayQueue<byte[]> _mQueues = new ArrayQueue<byte[]>(0x400);
    private int waitNextMsgTime = 2;
    private int waitNextPicTime = 6;
    private Handler handler;
//    private ArrayQueue<byte[]> receive_data_queues = new ArrayQueue<byte[]>(0x400);

    public UsbSerial(String path,Handler handler) {
        this.Path = path;
        this.handler = handler;
        sBuildBuffer = new StringBuilder();
        Buffer = new byte[1024 * 10];
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean Open() {
        try {
            ser = new SerialPort(new File(Path), SerialPort.USB_TYPE, 0);
            intput = ser.getInputStream();
            output = ser.getOutputStream();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Receive();
                }
            }).start();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    WRun();
//                }
//            }).start();
            ExceptionApplication.gLogger.info("Usb serial open normally !");
            return true;
        } catch (IOException e) {
            ExceptionApplication.gLogger.info("Usb serial open failed !");
            return false;
        }
    }

    /**
     * 监听串口程序
     */
    private void Receive() {

        while (!mDispose) {
            try {
                int len = intput.read(Buffer);
                if (len > 0) {
                    //解析数据
                    byte[] buffer = new byte[len];
                    System.arraycopy(Buffer, 0, buffer, 0, len);
                    AnalyticalProtocol(buffer);
                }else{
                    if (allBufferDatas != null && allBufferDatas.length >0){
//                        serial(allBufferDatas);
                        allBufferDatas = null;
                        Thread.sleep(waitNextPicTime);
                    }else {
                        Thread.sleep(waitNextMsgTime);
                    }
                    continue;
                }
                if (len < 0) {
                    Dispose();
                }
            } catch (Exception e) {
                Dispose();
            }finally {

            }
        }
    }

    private String cut_paper_1 = "1D 56";
    private String cut_paper_2 = "1B 69";

    private void AnalyticalProtocol(byte[] buffer) {
        try {
//            if (allBufferDatas == null){
//                allBufferDatas = new byte[buffer.length];
//                System.arraycopy(buffer,0,allBufferDatas,0,buffer.length);
//            } else {
//                byte[] tem = new byte[allBufferDatas.length];
//                System.arraycopy(allBufferDatas,0,tem,0,allBufferDatas.length);
//                allBufferDatas = new byte[buffer.length + tem.length];
//                System.arraycopy(tem,0,allBufferDatas,0,tem.length);
//                System.arraycopy(buffer,0,allBufferDatas,tem.length,buffer.length);
//            }
            ExceptionApplication.gLogger.info(" get data time -->" + System.currentTimeMillis());
            //获取切纸前的所有数据
            String str = EpsonParseDemo.printHexString(buffer);
//            sBuffer = sBuffer + str;
            sBuildBuffer.append(str);
            while (sBuildBuffer.length() > 0) {
                int startIndex = 0 ;
                int endIndex = sBuildBuffer.indexOf(cut_paper_1, startIndex);
                if (endIndex <= -1){
                    endIndex = sBuildBuffer.indexOf(cut_paper_2, startIndex);
                    if (endIndex <= -1){
                        break;
                    }
                }
                if (endIndex < startIndex)
                    break;
                int len = endIndex + 6;
//                ExceptionApplication.gLogger.info("get one data  -->" + System.currentTimeMillis());
                String ong_Print_msg = sBuildBuffer.substring(startIndex, len);
                serialString(ong_Print_msg);
                if (len >= sBuildBuffer.length()){
                    sBuildBuffer.delete(0 , sBuildBuffer.length());
                }else {
                    sBuildBuffer.delete(0 , len);
                }
            }
        }catch (Exception e){

        }
    }


    private void Send(byte[] buffer) {
        try {
            if (mDispose)
                return;
            output.write(buffer);
            output.flush();
        } catch (IOException e) {
            Dispose();
        }
    }

    private void OnRun() {
        byte[] item = GetItem();
        try {
            if (item != null) {
                Send(item);
            } else {
                Thread.sleep(7);
            }
        } catch (Exception e) {

        }
    }

    private void WRun() {
        while (!mDispose) {
            OnRun();
        }
    }

    public void AddHandle(byte[] buffer) {
        synchronized (UsbSerial.class) {
            _mQueues.enqueue(buffer);
        }
    }

    private byte[] GetItem() {
        synchronized (UsbSerial.class) {
            if (_mQueues.size() > 0) {
                return _mQueues.dequeue();
            }
            return null;
        }
    }

    public void SendMessage(String data) {
        try {
            AddHandle(data.getBytes());
        } catch (Exception e) {
        }
    }

    public void SendMessage(byte[] data) {
        try {
            AddHandle(data);
        } catch (Exception e) {
        }
    }


    //关闭
    public void Dispose() {
        synchronized (UsbSerial.class) {
            if (!mDispose) {
                mDispose = true;
                ExceptionApplication.gLogger.error("Usb serial error close!");
                MyDispose();
                UsbSerialRestart.Check();
            }
        }
    }

    private void MyDispose() {
        try {
            if (intput != null) {
                intput.close();
            }
            if (output != null) {
                output.close();
            }
            if (handler != null){
                handler = null;
            }
            if (ser != null)
                ser.closeSerialPort();
        } catch (IOException e) {

        } finally {

        }
    }



    public void serial(byte[] data) {
        try {
//            Message msg = this.handler.obtainMessage();
//            msg.obj = data;
//            msg.what = 10;
//            this.handler.sendMessage(msg);

//            try {
//                File file = new File(Environment.getExternalStorageDirectory(),
//                        "/ucast/port.txt");
//                FileOutputStream fos = new FileOutputStream(file,true);
//                fos.write(data);
//                fos.write("\n-----000000000000000000000000-----\n".getBytes());
//                fos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {

        }
    }
    public void serialString(String string) {
        try {
            if(string.contains(EpsonParseDemo.startEpsonStr)){
                List<String> paths = EpsonParseDemo.parseEpsonBitData(string.trim());
                for (int i = 0; i <paths.size() ; i++) {
                    if (i == paths.size() -1 ) {
                        handleToServiceToPrint(paths.get(i),true);
                    }else{
                        handleToServiceToPrint(paths.get(i),false);
                    }
                }
                String p = SomeBitMapHandleWay.compoundOneBitPic(paths);
                MyTools.uploadFileByQueue(p);
//                handleToServiceToPrint(p);
                return;
            }
            printOne(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleToServiceToPrint(String path ,boolean isCutPapper) {
//        Message msg = Message.obtain(handler);
//        msg.obj = path;
//        msg.what = 10;
//        msg.sendToTarget();
        ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path,isCutPapper));

    }

    public String printOne(String data){

        String [] bytes = data.split(" ");
        byte [] datas =new byte[bytes.length];
        String path = "" ;

        for (int i = 0; i < bytes.length; i++) {
            datas[i] = (byte) Integer.parseInt(bytes[i], 16);
        }
        List<byte[]> byteList = EpsonParseDemo.getEpsonFromByteArr(datas);
        try {
            List<PrintAndDatas> printdatas = EpsonParseDemo.parseEpsonByteList(byteList);

            List<PrintAndDatas> goodPrintdatas = EpsonParseDemo.makeListIWant(printdatas);

//            MyTools.uploadDataByQueue(goodPrintdatas.get(0).stringDatas);

            List<Bitmap> bmps = EpsonParseDemo.parseEpsonBitDataAndStringReturnBitmap(goodPrintdatas);

            path = SomeBitMapHandleWay.compoundOneBitPicWithBimaps(bmps);
            if (path != null && path != ""){
                MyTools.uploadDataAndFileWithURLByQueue(goodPrintdatas.get(0).datas,path , YinlianHttpRequestUrl.UPLOADBASE64URL);
                handleToServiceToPrint(path,true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return path;
        }

    }

}

