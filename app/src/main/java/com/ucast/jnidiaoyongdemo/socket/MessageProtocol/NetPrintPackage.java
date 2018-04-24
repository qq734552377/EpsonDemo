package com.ucast.jnidiaoyongdemo.socket.MessageProtocol;


import android.graphics.Bitmap;

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
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

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
            HandleEpsonDataByUcastPrint.serialString(ong_Print_msg);
            cache.cutBuffer();
        }
    }


    public MessageBase MessageRead(String value) throws Exception {
       return null;
    }

}
