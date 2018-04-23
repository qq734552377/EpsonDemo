package com.ucast.jnidiaoyongdemo.socket.MessageProtocol;


import android.graphics.Bitmap;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
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


    //用于存放打印返回信息
    private byte[] fanhuiBuffer ;
    //用于监控fanBuffer的初始偏移量
    private int offSet = 0;
    //用于反应当前应截取的位置
    private int cutPosition = 0;

    public NetPrintPackage(Channel _channel) {
        super(_channel);
        fanhuiBuffer = new byte[1024 * 200];
    }

    @Override
    public void Import(byte[] buffer, int Offset, int count) throws Exception {
        jointBuffer(buffer);
        while (offSet > 0) {
            int startIndex = 0 ;
            int endIndex = getCutpapperPosition(UsbWithByteSerial.cut_paper_byte_1);
            if (endIndex <= -1){
                endIndex = getCutpapperPosition(UsbWithByteSerial.cut_paper_byte_2);
                if (endIndex <= -1){
                    break;
                }
            }
            if (endIndex < startIndex)
                break;
            int len = endIndex + 2;
            byte[] ong_Print_msg = getPrintbyte(startIndex,len);
            HandleEpsonDataByUcastPrint.serialString(ong_Print_msg);
            cutBuffer();
        }
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

    public MessageBase MessageRead(String value) throws Exception {
       return null;
    }

}
