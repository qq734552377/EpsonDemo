package com.ucast.jnidiaoyongdemo.Serial;

import com.ucast.jnidiaoyongdemo.Model.Common;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.SendPackage;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.protocol_ucast.MsCardProtocol;
import com.ucast.jnidiaoyongdemo.protocol_ucast.PrinterProtocol;
import com.ucast.jnidiaoyongdemo.queue_ucast.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.tools.ArrayQueue;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by pj on 2016/1/22.
 */
public class SerialTest {
    private SerialPort ser;
    private InputStream intput;
    private OutputStream output;
    private String Path = Config.PrinterSerial;
    private boolean mDispose;
    private byte[] Buffer;
    public String Name = "ttyGS0";
    private ArrayQueue<byte[]> _mQueues = new ArrayQueue<byte[]>(0x400);

    private final static String HEAD = new String(new byte[]{0x02});
    private final static String END = new String(new byte[]{0x03});

    //用于存放打印返回信息
    private byte[] fanhuiBuffer ;
    //用于监控fanBuffer的初始偏移量
    private int offSet = 0;
    //用于反应当前应截取的位置
    private int cutPosition = 0;
    //设置存放消息数组的设定长度
    private int fanhuiBufferLen = 1024 ;


    public SerialTest(String path) {
        Path = path;
        Buffer = new byte[1024];
        fanhuiBuffer = new byte[fanhuiBufferLen];
    }

    public boolean Open() {
        try {
            //实例串口
            ser = new SerialPort(new File(Path), SerialPort.TEST_TYPE ,0);
            //获取写入流
            intput = ser.getInputStream();
            //获取输出流
            output = ser.getOutputStream();
            Thread receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Receive();
                }
            });

//            receiveThread.setPriority(2);
            receiveThread.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WRun();
                }
            }).start();
            ExceptionApplication.gLogger.info("Printer serial "+Path+" open normally !");
            return true;
        } catch (IOException e) {
            ExceptionApplication.gLogger.info("Printer serial "+ Path +" open failed !");
            return false;
        }
    }

    /**
     * 监听串口程序
     */
    private void Receive() {
        while (!mDispose) {
            try {
                int tatal = intput.available();
                if (tatal <= 0) {
                    Thread.sleep(5);
                    continue;
                }
                int len = intput.read(Buffer);
                if (len > 0) {
                    //解析数据
                    byte[] buffer = new byte[len];
                    System.arraycopy(Buffer, 0, buffer, 0, len);
                    AnalyticalProtocol(buffer);
                    continue;
                }

                if (len < 0) {
                    Dispose();
                }
            } catch (Exception e) {
                Dispose();
            }
        }
    }

    public void Send(byte[] buffer) {
        try {
            AddHandle(buffer);
        } catch (Exception e) {
        }
    }

    public void Send(String str) {
        try {
            AddHandle(str.getBytes());
        } catch (Exception e) {
        }
    }

    private void OnRun() {
        byte[] item = GetItem();
        try {
            if (item != null) {
                SendMessage(item);
            } else {
                Thread.sleep(1);
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
        synchronized (SerialTest.class) {
            _mQueues.enqueue(buffer);
        }
    }

    private byte[] GetItem() {
        synchronized (SerialTest.class) {
            if (_mQueues.size() > 0) {
                return _mQueues.dequeue();
            }
            return null;
        }
    }

    private void SendMessage(byte[] buffer) {
        try {
            if (mDispose)
                return;
            output.write(buffer);
            output.flush();
        } catch (IOException e) {
            Dispose();
        }
    }



    private void AnalyticalProtocol(byte[] buffer) {
        ExceptionApplication.gLogger.info( Path + "的所有的数据-->"+ new String(buffer));
        EventBus.getDefault().postSticky(Path + "的所有的数据-->"+ new String(buffer));
    }
    //关闭
    public void Dispose() {
        synchronized (SerialTest.class) {
            if (!mDispose) {
                mDispose = true;
                ExceptionApplication.gLogger.error("Printer serial error close!");
                MyDispose();
                PrinterSerialRestart.Check();
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
            if (ser != null )
                ser.closeSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


}
