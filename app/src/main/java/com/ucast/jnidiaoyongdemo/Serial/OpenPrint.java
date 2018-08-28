package com.ucast.jnidiaoyongdemo.Serial;

import android.util.EventLog;

import com.ucast.jnidiaoyongdemo.Model.Common;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;
import com.ucast.jnidiaoyongdemo.queue_ucast.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.protocol_ucast.MsCardProtocol;
import com.ucast.jnidiaoyongdemo.protocol_ucast.PrinterProtocol;
import com.ucast.jnidiaoyongdemo.Model.SendPackage;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.tools.ArrayQueue;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.xutilEvents.MsCardEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by pj on 2016/1/22.
 */
public class OpenPrint {
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


    public OpenPrint(String path) {
        Path = path;
        Buffer = new byte[1024];
        fanhuiBuffer = new byte[fanhuiBufferLen];
    }

    public boolean Open() {
        try {
            //实例串口
            ser = new SerialPort(new File(Path), Config.PrinterSerialType ,0);
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
                Thread.sleep(4);
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
        synchronized (OpenPrint.class) {
            _mQueues.enqueue(buffer);
        }
    }

    private byte[] GetItem() {
        synchronized (OpenPrint.class) {
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
        //添加串口数据
        jointBuffer(buffer);
        MyTools.writeToFile(EpsonPicture.TEMPBITPATH + File.separator + "templog.txt",EpsonParseDemo.printHexString(buffer));
//        ExceptionApplication.gLogger.info("所有的数据-->"+EpsonParseDemo.printHexString(buffer));
        while (offSet > 0) {
            int startIndex = getIndexByByte((byte) 0x02);
            if (startIndex <= -1) {
                break;
            }
            int endIndex = getIndexByByte((byte) 0x03);
            if (endIndex <= -1) {
                break;
            }
            if (endIndex < startIndex) {
                cutPosition = endIndex + 1;
                cutBuffer();
                continue;
            }
            byte[] printBuffer = getPrintbyte(startIndex , endIndex);
            serial(printBuffer);
            cutBuffer();
        }
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
    private byte[] getPrintbyte(int start, int end) {
        byte[] printByte = new byte[end - start - 1];
        int position = start + 1;
        System.arraycopy(fanhuiBuffer,position,printByte,0,printByte.length);
        cutPosition = end + 1;
        return printByte;
    }

    //用于重新截取fanhuiBuffer的数据
    private void cutBuffer() {
        System.arraycopy(fanhuiBuffer,cutPosition,fanhuiBuffer,0,offSet - cutPosition);
        offSet = offSet - cutPosition;
        if(fanhuiBuffer.length > fanhuiBufferLen && offSet < fanhuiBufferLen/2){
            byte[] temp = new byte[offSet];
            System.arraycopy(fanhuiBuffer,0,temp,0,offSet);
            fanhuiBuffer = new byte[fanhuiBufferLen];
            System.arraycopy(temp,0,fanhuiBuffer,0,offSet);
        }
    }


    //关闭
    public void Dispose() {
        synchronized (OpenPrint.class) {
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

    public void serial(byte[] buffer) {
        byte[] data = Common.changeByteBack(buffer);
        String string = new String(data);
//        ExceptionApplication.gLogger.info("paser one protocol 111 " + EpsonParseDemo.printHexString(data));
        if (string.length() < 5) {
            return;
        }
        String str = string.substring(0, 2);
        switch (str) {
            case "PA":  //是否打开打印设备回复
                ExceptionApplication.gLogger.info("Printer Swtich on!");
                SendPackage.sendToPrinter(PrinterProtocol.getPrinterStartPrinterProtocol() );
                break;
            case "PB":  //是否关闭打印设备回复
                ExceptionApplication.gLogger.info("Printer Swtich off!");
                break;
            case "PC": //传输打印数据回复
//                ExceptionApplication.gLogger.info("Printer data reply!");
                handlePrintData(data);
                break;
            case "PD"://是否能开始打印回复
                ExceptionApplication.gLogger.info("Printer can start print!  ");
//                ExceptionApplication.gLogger.info(" send path to Queue " + MyTools.millisToDateString(System.currentTimeMillis()));
//                String path = Environment.getExternalStorageDirectory() + "/ucast.bmp";
//                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(path));
                break;
            case "PE"://是否结束打印回复
                ExceptionApplication.gLogger.info("Printer can not print!");
                break;

            case "MA"://是否打开磁卡设备回复
                ExceptionApplication.gLogger.info("MaCard Swtich on!");
                SendPackage.sendToPrinter(MsCardProtocol.getRegisterMsCardProtocol());
                break;
            case "MB"://是否关闭磁卡设备回复
                ExceptionApplication.gLogger.info("MaCard Swtich off!");
                break;
            case "MC"://是否注册刷卡回复
                ExceptionApplication.gLogger.info("MaCard regist sucessful!");
                break;
            case "MD"://是否取消注册刷卡回复
                ExceptionApplication.gLogger.info("MaCard unregist sucessful!");
                break;
            case "MF"://刷卡数据长度回复
                ExceptionApplication.gLogger.info("MaCard data length!");
                break;
            case "MG"://刷卡数据回复
                ExceptionApplication.gLogger.info("MaCard data reply !");
                msDataHandle(data);
                break;
            case "MH"://刷卡后通知命令回复
                ExceptionApplication.gLogger.info("MaCard is working , can get data !");
                for (int i = 0; i < 3; i++) {
                    SendPackage.sendToPrinter(MsCardProtocol.getGetDataMsCardByNumProtocol((byte) (i & 0xFF)));
                }
                break;


            default:
                ExceptionApplication.gLogger.info("other data 222:  " + EpsonParseDemo.printHexString(data));
                break;
        }
    }
    private StringBuilder msSB = new StringBuilder();
    private void msDataHandle(byte[] data) {
        int num = data[5] & 0xff;
        int dataLen = (data[7] << 8 & 0xff00) + (data[6] & 0xff);
        byte[] dataMs = new byte[dataLen];
        System.arraycopy(data,8,dataMs,0,dataLen);
        switch (num){
            case 0 :
                msSB.delete(0,msSB.length());
                msSB.append("T1");
                msSB.append(new String(dataMs));
                EventBus.getDefault().post(new MsCardEvent(1,new String(dataMs)));
                break;
            case 1 :
                msSB.append("T2");
                msSB.append(new String(dataMs));
                EventBus.getDefault().post(new MsCardEvent(2,new String(dataMs)));
                break;
            case 2:
                msSB.append("T3");
                msSB.append(new String(dataMs));
                EventBus.getDefault().postSticky(msSB.toString());
                EventBus.getDefault().post(new MsCardEvent(3,new String(dataMs)));
                ExceptionApplication.gLogger.info("Mscard data ----->" + msSB.toString());
                SendPackage.sendToPrinter(MsCardProtocol.getRegisterMsCardProtocol());
                break;
        }
    }

    private void handlePrintData(byte[] res){
        try{
            byte flag = res[2];

            switch (flag){
                case 0x31://下发打印数据回复
                    sendWithResult(res);
                    break;
                case 0x32://切纸回复
//                    ExceptionApplication.gLogger.info(" cut papper time ： " +  MyTools.millisToDateString(System.currentTimeMillis()));
                    ListPictureQueue.safeRemove();
                    break;
                case 0x33://查询打印机状态回复
                    byte havePaper = res[6];//打印机是否有纸 0表示有纸 1表示缺纸
                    byte isCloseCover = res[7];//打印机轴是否到位 0表示轴到位 1表示不到位
                    int temperature = res[8] & 0xFF;//打印机温度

                    String state = (havePaper == 0x00 ? "有纸" : "无纸") + "  "
                                 + (isCloseCover == 0x00 ? "仓门正常" : "仓门没关") + "  打印机温度为："
                                 + temperature;
                    ExceptionApplication.gLogger.info(state);
                    break;
            }



        }catch (Exception e){
        }
    }

    private void sendWithResult(byte[] res){
        int t = (res[7] << 8 & 0xff00) + (res[6] & 0xff);
        ListPictureQueue.SendByIndex(t);
    }

}
