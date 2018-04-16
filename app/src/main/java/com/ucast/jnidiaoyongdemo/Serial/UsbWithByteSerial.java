package com.ucast.jnidiaoyongdemo.Serial;

import android.graphics.Bitmap;
import android.os.Handler;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
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
//    private ArrayQueue<byte[]> receive_data_queues = new ArrayQueue<byte[]>(0x400);


    //用于存放打印返回信息
    private byte[] fanhuiBuffer ;
    //用于监控fanBuffer的初始偏移量
    private int offSet = 0;
    //用于反应当前应截取的位置
    private int cutPosition = 0;


    public UsbWithByteSerial(String path, Handler handler) {
        this.Path = path;
        this.handler = handler;
        sBuildBuffer = new StringBuilder();
        Buffer = new byte[1024 * 10];
        fanhuiBuffer = new byte[1024 * 60];
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
    private byte[] cut_paper_byte_1 = {0x1D,0x56};
    private String cut_paper_2 = "1B 69";
    private byte[] cut_paper_byte_2 = {0x1B,0x69};

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
//            ExceptionApplication.gLogger.info(" get data time -->" + System.currentTimeMillis());
            //获取切纸前的所有数据
            jointBuffer(buffer);
            while (offSet > 0) {
                int startIndex = 0 ;
                int endIndex = getCutpapperPosition(cut_paper_byte_1);
                if (endIndex <= -1){
                    endIndex = getCutpapperPosition(cut_paper_byte_2);
                    if (endIndex <= -1){
                        break;
                    }
                }
                if (endIndex < startIndex)
                    break;
                int len = endIndex + 2;
                byte[] ong_Print_msg = getPrintbyte(startIndex,len);
                serialString(ong_Print_msg);
                cutBuffer();
            }
        }catch (Exception e){

        }
    }

    private boolean isContainByteArr(byte[] src,byte[] item){
        if (item.length < 3){
            return false;
        }
        boolean isContain = false;
        for (int i = 0; i < src.length; i++) {
            if (src[i] == item[2] && i > 1){
                if(src[i-1] == item[1] && src[i-2] == item[0]){
                    isContain = true;
                }
            }
        }
        return isContain;
    }

    private int getCutpapperPosition(byte[] b){
        if (b.length < 2){
            return -1;
        }
        for (int i = 0; i < offSet; i++) {
            if (fanhuiBuffer[i] == b[1] && i > 0) {
                if(fanhuiBuffer[i-1] == b[0]){
                    return i - 1;
                }
            }
        }
        return -1;
    }

    private int getIndexByByte( byte b) {
        for (int i = 0; i < offSet; i++) {
            if (fanhuiBuffer[i] == b) {
                return i;
            }
        }
        return -1;
    }
    private void jointBuffer(byte[] buffer) {
        if (offSet + buffer.length  > fanhuiBuffer.length) {
            // 扩容 为原来的两倍
            byte[] temp = new byte[fanhuiBuffer.length];
            System.arraycopy(fanhuiBuffer,0,temp,0,fanhuiBuffer.length);
            fanhuiBuffer = new byte[fanhuiBuffer.length * 2];
            System.arraycopy(temp,0,fanhuiBuffer,0,temp.length);
        }
        System.arraycopy(buffer,0,fanhuiBuffer,offSet,buffer.length);
        offSet = offSet + buffer.length;
    }



    //返回一个byte对象 用于发送消息 该数组不会包含 头和尾 即0x02和0x03
    private byte[] getPrintbyte(int start, int len) {
        byte[] printByte = new byte[len];
        int position = start;
        System.arraycopy(fanhuiBuffer,position,printByte,0,printByte.length);
        cutPosition = len;
        return printByte;
    }

    //用于重新截取fanhuiBuffer的数据
    private void cutBuffer() {
        System.arraycopy(fanhuiBuffer,cutPosition,fanhuiBuffer,0,offSet - cutPosition);
        offSet = offSet - cutPosition;
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




    public void serialString(byte[] string) {
        try {
            if(isContainByteArr(string,EpsonParseDemo.STARTEPSONBYTE)){
                List<String> paths = EpsonParseDemo.parseEpsonBitData(string);
                String p = SomeBitMapHandleWay.compoundOneBitPic(paths);
                MyTools.uploadFileByQueue(p);
                return;
            }
            printOne(string);
        } catch (Exception e) {
            ExceptionApplication.gLogger.info("paser bitmap error ");
            e.printStackTrace();
        }
    }
    public void handleToServiceToPrint(String path ,boolean isCutPapper) {
        ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path,isCutPapper));
    }

    public String printOne(byte[] data){
        String path = "" ;
        List<byte[]> byteList = EpsonParseDemo.getEpsonFromByteArr(data);
        try {
            List<PrintAndDatas> printdatas = EpsonParseDemo.parseEpsonByteList(byteList);

            List<PrintAndDatas> goodPrintdatas = EpsonParseDemo.makeListIWant(printdatas);

            List<Bitmap> bmps = EpsonParseDemo.parseEpsonBitDataAndStringReturnBitmap(goodPrintdatas);

            path = SomeBitMapHandleWay.compoundOneBitPicWithBimaps(bmps);
            if (path != null && path != ""){
                MyTools.uploadDataAndFileWithURLByQueue(goodPrintdatas.get(0).datas,path , YinlianHttpRequestUrl.UPLOADBASE64URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return path;
        }

    }

}

