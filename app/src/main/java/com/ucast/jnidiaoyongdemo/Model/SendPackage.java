package com.ucast.jnidiaoyongdemo.Model;


import com.ucast.jnidiaoyongdemo.Serial.KeyBoardSerial;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;
import com.ucast.jnidiaoyongdemo.socket.Memory.NettyChannelMap;
import com.ucast.jnidiaoyongdemo.socket.Memory.NettyClientMap;
import com.ucast.jnidiaoyongdemo.socket.net_print.NioTcpClient;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * Created by Administrator on 2016/6/14.
 */
public class SendPackage {

    public static void sendToPrinter(byte[] buffer) {
        OpenPrint print = MermoyPrinterSerial.GetChannel(Config.PrinterSerialName);
        if (print == null)
            return;
        print.Send(buffer);
    }
    public static void sendToKeyboard(byte[] buffer) {
        KeyBoardSerial keyboard = MermoyKeyboardSerial.GetChannel(Config.KeyboardSerialName);
        if (keyboard == null)
            return;
        try {
            keyboard.output.write(buffer);
        }catch (Exception e){

        }

    }

    public static boolean sendDataToNetPrintPort(byte[] Data) {
        NioTcpClient client = NettyClientMap.GetChannel(Config.NETPrintName);
        if (client == null)
            return false;
        return client.Send(Data);
    }

    public static boolean sendAllClient(byte[] Data) {
        Set set = NettyChannelMap.ToList();
        boolean isSendOk=false;
        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            Channel value = (Channel) entry.getValue();
            if (value == null)
                return false;
            ByteBuf resp = Unpooled.copiedBuffer(Data);
            value.writeAndFlush(resp);
            isSendOk=true;
        }
        return isSendOk;
    }
    public static void sendChannelHeartbeatData(Channel channel) {
        byte[] data = Common.GetFormat("2307", 1, 1, new String[]{"1"});
        ByteBuf resp = Unpooled.copiedBuffer(data);
        channel.writeAndFlush(resp);
    }

    public static void ChannelSendBuffer(Channel channel, byte[] buffer) {
        if (channel == null)
            return;
        if (!channel.isActive()){
            return;
        }
        ByteBuf resp = Unpooled.copiedBuffer(buffer);
        channel.writeAndFlush(resp);
    }
}
