package com.ucast.jnidiaoyongdemo.Serial;

import android.graphics.Bitmap;
import android.os.Handler;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.ByteArrCache;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.HandleEpsonDataByUcastPrint;
import com.ucast.jnidiaoyongdemo.bmpTools.PrintAndDatas;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.tools.ArrayQueue;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * Created by pj on 2016/1/22.
 */
public class UsbWithByteSerial {
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private String Path = "/dev/g_printer0";
    private boolean mDispose;
    private StringBuilder sBuildBuffer;
    private byte[] Buffer;
    private byte[] allBufferDatas;
    private ArrayQueue<byte[]> _mQueues = new ArrayQueue<byte[]>(0x400);
    private int waitNextMsgTime = 2;
    private int waitNextPicTime = 6;
    private Handler handler;
    private ByteArrCache cache;
    //设置存放消息数组的设定长度
    private int fanhuiBufferLen = 1024 * 100;


    public UsbWithByteSerial(String path) {
        this.Path = path;
        sBuildBuffer = new StringBuilder();
        Buffer = new byte[1024 * 50];
        cache = new ByteArrCache(fanhuiBufferLen);
    }
    public UsbWithByteSerial(String path, Handler handler) {
        this.Path = path;
        this.handler = handler;
        sBuildBuffer = new StringBuilder();
        Buffer = new byte[1024 * 10];
        cache = new ByteArrCache(fanhuiBufferLen);
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
    public static final byte[] cut_paper_byte_1 = {0x1D,0x56};
    private String cut_paper_2 = "1B 69";
    public static final byte[] cut_paper_byte_2 = {0x1B,0x69};

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
//            ExceptionApplication.gLogger.info(" 收到USB来的数据了-->" + System.currentTimeMillis());
            //获取切纸前的所有数据
            cache.jointBuffer(buffer);
            while (cache.getOffSet() > 0) {
                int startIndex = 0 ;
                int endIndex = cache.getCutpapperPosition(cut_paper_byte_1);
                if (endIndex <= -1){
                    endIndex = cache.getCutpapperPosition(cut_paper_byte_2);
                    if (endIndex <= -1){
                        break;
                    }
                }
                if (endIndex < startIndex)
                    break;
                int len = endIndex + 2;
                byte[] ong_Print_msg = cache.getOneDataFromBuffer(startIndex,len);
                HandleEpsonDataByUcastPrint.serialString(ong_Print_msg,true);
                cache.cutBuffer();
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
        synchronized (UsbWithByteSerial.class) {
            _mQueues.enqueue(buffer);
        }
    }

    private byte[] GetItem() {
        synchronized (UsbWithByteSerial.class) {
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
        synchronized (UsbWithByteSerial.class) {
            if (!mDispose) {
                mDispose = true;
                ExceptionApplication.gLogger.error("Usb serial error close!");
                MyDispose();
                UsbWithByteSerialRestart.Check();
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
}

