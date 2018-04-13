package com.ucast.jnidiaoyongdemo.socket.MessageProtocol;


import io.netty.channel.Channel;

import com.ucast.jnidiaoyongdemo.socket.Message.Heartbeat;
import com.ucast.jnidiaoyongdemo.socket.Message.IcPass;
import com.ucast.jnidiaoyongdemo.socket.Message.MessageBase;
import com.ucast.jnidiaoyongdemo.socket.Message.NfcPass;
import com.ucast.jnidiaoyongdemo.socket.Message.PrintMessage;
import com.ucast.jnidiaoyongdemo.socket.Message.ReadNfc;
import com.ucast.jnidiaoyongdemo.socket.Message.ReadTwoCode;
import com.ucast.jnidiaoyongdemo.socket.Message.TwoCodePass;

/**
 * Created by Administrator on 2016/2/3.
 */
public class StationPackage extends Package {

    private StringBuffer sBuffer;

    public StationPackage(Channel _channel) {
        super(_channel);
        sBuffer = new StringBuffer();
    }

    @Override
    public void Import(byte[] buffer, int Offset, int count) throws Exception {
        sBuffer.append(new String(buffer));
        int offset = 0;
        while (sBuffer.length() > offset&&!mDispose) {
            int startIndex = sBuffer.indexOf("@", offset);
            if (startIndex == -1)
                break;

            int endIndex = sBuffer.indexOf("$", startIndex);
            if (endIndex == -1)
                break;
            int len = endIndex + 1;


            String value = sBuffer.substring(startIndex, len);
            OnMessageDataReader(value);
            offset = len;
        }
        sBuffer.delete(0, offset);
    }

    public MessageBase MessageRead(String value) throws Exception {
        String msg = value.substring(1, value.length() - 1);
        String[] item = msg.split(",");
        MessageBase mbase = null;
        switch (item[0]) {
            case "1110":
                mbase=new Heartbeat();
                break;
            //打印机图片地址
            case "2100":
                mbase = new PrintMessage();
                break;
            default:
                break;
        }
        if (mbase == null)
            return null;
        mbase.Load(item);
        return mbase;
    }

}
