package com.ucast.jnidiaoyongdemo.Serial;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ucast.jnidiaoyongdemo.Tool.ArrayQueue;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;


/**
 * Created by Administrator on 2016/1/22.
 */
public class NoHeadEnd_16HEXPort {
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private int Baudrate = 115200;
    private String Path = "/dev/ttyS3";
    private boolean mDispose;
    public String Name = "ttyS3";
    private String sBuffer;
    private byte[] Buffer;
    private byte[] allBufferDatas;
    Handler handler;
    //SerialMessage message;
    private ArrayQueue<byte[]> _mQueues = new ArrayQueue<byte[]>(0x400);

//    private ArrayQueue<byte[]> receive_data_queues = new ArrayQueue<byte[]>(0x400);

    public NoHeadEnd_16HEXPort(String path, int baudrate, Handler _handler) {
        Baudrate = baudrate;
        Path = path;
        sBuffer = new String();
        Buffer = new byte[1024];
        handler = _handler;
       // message = new SerialMessage(handler);
    }

    public boolean Open() {
        try {
            ser = new SerialPort(new File(Path), Baudrate, 0);
            intput = ser.getInputStream();
            output = ser.getOutputStream();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Receive();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WRun();
                }
            }).start();
            return true;
        } catch (IOException e) {
            //提示打开串口错误
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
                    Thread.sleep(5);
                }else if( len == 0){
                    Thread.sleep(15);
                    if (allBufferDatas != null && allBufferDatas.length >0){
                        serial(allBufferDatas);
                        allBufferDatas = null;
                    }
                    continue;
                }
                Log.e("input数据", "Receive: "+ Buffer.length );
                if (len < 0) {
                    Dispose();
                }
            } catch (Exception e) {
                Log.e("calm", "---接收串口数据的线程停止---");
                Dispose();
            }finally {

            }
        }
        Log.e("calm", "---while线程停止---");
    }

    private String cut_paper = "1D 56";

    private void AnalyticalProtocol(byte[] buffer) {
        try {
            if (allBufferDatas == null){
                allBufferDatas = new byte[buffer.length];
                System.arraycopy(buffer,0,allBufferDatas,0,buffer.length);
            } else {
                byte[] tem = new byte[allBufferDatas.length];
                System.arraycopy(allBufferDatas,0,tem,0,allBufferDatas.length);

                allBufferDatas = new byte[buffer.length + tem.length];
                System.arraycopy(tem,0,allBufferDatas,0,tem.length);
                System.arraycopy(buffer,0,allBufferDatas,tem.length,buffer.length);
            }

            //获取切纸前的所有数据
            String str = EpsonParseDemo.printHexString(buffer);
            sBuffer = sBuffer + str;
            while (sBuffer.length() > 0) {
                int startIndex = 0 ;
                int endIndex = sBuffer.indexOf(cut_paper, startIndex);
                if (endIndex <= -1)
                    break;
                if (endIndex < startIndex)
                    break;
                int len = endIndex + 9;
                String ong_Print_msg = sBuffer.substring(startIndex, len);
                serialString(ong_Print_msg);
                Log.e(" ------->>>>>", "AnalyticalProtocol: " + ong_Print_msg );
                if (len >= sBuffer.length()){
                    sBuffer = "";
                }else {
                    sBuffer = sBuffer.substring(len, sBuffer.length());
                }
            }




        }catch (Exception e){

        }
//        while (sBuffer.length() > 0) {
//            int startIndex = sBuffer.indexOf("@");
//            if (startIndex <= -1)
//                break;
//            int endIndex = sBuffer.indexOf("\r\n", startIndex);
//            if (endIndex <= -1)
//                break;
//            if (endIndex < startIndex)
//                break;
//            int len = endIndex + 2;
//            serial(sBuffer.substring(startIndex, len));
//            sBuffer = sBuffer.substring(len, sBuffer.length());
//            System.out.println("OFFSET:"+sBuffer.length());
//        }

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
                Thread.sleep(20);
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
        synchronized (_mQueues) {
            _mQueues.enqueue(buffer);
        }
    }

    private byte[] GetItem() {
        synchronized (_mQueues) {
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
        synchronized (this) {
            if (!mDispose) {
                mDispose = true;
                Log.e("calm", "----關閉串口----");
                MyDispose();
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
            ser.closeSerialPort();
        } catch (IOException e) {

        } finally {

        }
    }

    public void serial(byte[] string) {
//        String str = string.substring(0, string.length());
        try {
           // if (message == null)
              //  return;
            //message.rePacksge(str);
            Message msg = this.handler.obtainMessage();
            msg.obj = string;
            msg.what = 10;
            this.handler.sendMessage(msg);
        } catch (Exception e) {

        }
    }
    public void serialString(String string) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "/ucast/port_str_01.txt");
            FileOutputStream fos = new FileOutputStream(file,true);
            fos.write(string.getBytes());
            fos.write("\n-----000000000000000000000000-----\n".getBytes());
            fos.close();


            Message msg = this.handler.obtainMessage();
            msg.obj = string;
            msg.what = 20;
            this.handler.sendMessage(msg);
        } catch (Exception e) {

        }
    }
}

