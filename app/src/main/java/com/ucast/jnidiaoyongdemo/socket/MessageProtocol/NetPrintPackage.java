package com.ucast.jnidiaoyongdemo.socket.MessageProtocol;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.ByteArrCache;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Serial.UsbWithByteSerial;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.HandleEpsonDataByUcastPrint;
import com.ucast.jnidiaoyongdemo.bmpTools.PrintAndDatas;
import com.ucast.jnidiaoyongdemo.bmpTools.SomeBitMapHandleWay;
import com.ucast.jnidiaoyongdemo.socket.Message.Heartbeat;
import com.ucast.jnidiaoyongdemo.socket.Message.MessageBase;
import com.ucast.jnidiaoyongdemo.socket.Message.PrintMessage;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;
import com.ucast.jnidiaoyongdemo.xutilEvents.MoneyBoxEvent;
import com.ucast.jnidiaoyongdemo.xutilEvents.TishiMsgEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.security.spec.ECField;
import java.util.List;

import io.netty.channel.Channel;

/**
 * Created by Administrator on 2016/2/3.
 */
public class NetPrintPackage extends Package {


    private ByteArrCache cache;
    //设置存放消息数组的设定长度
    private int fanhuiBufferLen = 1024 * 200;

    public NetPrintPackage(Channel _channel) {
        super(_channel);
        cache = new ByteArrCache(fanhuiBufferLen);
    }

    @Override
    public void Import(byte[] buffer, int Offset, int count) throws Exception {
        if(buffer[0] == 0x40 && buffer[buffer.length - 1] == 0x24){
            String value = new String(buffer);
            handleProtocol(value);
            return;
        }

        cache.jointBuffer(buffer);
        while (cache.getOffSet() > 0) {
            int startIndex = 0 ;
            int endIndex = cache.getCutpapperPosition(UsbWithByteSerial.cut_paper_byte_1);
            if (endIndex <= -1){
                endIndex = cache.getCutpapperPosition(UsbWithByteSerial.cut_paper_byte_2);
                if (endIndex <= -1){
                    break;
                }
            }
            if (endIndex < startIndex)
                break;
            int len = endIndex + 2;
            byte[] ong_Print_msg = cache.getOneDataFromBuffer(startIndex,len);
            String netPrinterUploadStr = SavePasswd.getInstace().readxml(SavePasswd.ISNETPRINTUPLOADTOSERVICE,SavePasswd.OPEN);
            boolean isNotUpload = netPrinterUploadStr.equals(SavePasswd.CLOSE);
            HandleEpsonDataByUcastPrint.serialString(ong_Print_msg,!isNotUpload);
            cache.cutBuffer();
        }
    }

    public void handleProtocol(String value){
        String msg = value.substring(1, value.length() - 1);
        String[] item = msg.split(",");
        switch (item[0]) {
            //打印机图片地址
            case "2100":
                String path = item[1].trim();
                boolean isCutPaper = item[2].trim().equals("1");
                File f = new File(path);
                if (!f.exists()){
                    EventBus.getDefault().post(new TishiMsgEvent(path + " 文件不存在！"));
                    return;
                }
                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path),isCutPaper));
                try {
//                    Thread.sleep(1500);
//                    EventBus.getDefault().post(new MoneyBoxEvent(false));

                }catch (Exception e){

                }
                break;
            default:
                break;
        }

    }

    public MessageBase MessageRead(String value) throws Exception {
       return null;
    }

}
