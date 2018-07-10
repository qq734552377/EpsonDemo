package com.ucast.jnidiaoyongdemo.socket.net_print;


import com.ucast.jnidiaoyongdemo.socket.MessageCallback.IMsgCallback;
import com.ucast.jnidiaoyongdemo.socket.MessageProtocol.NetPrintPackage;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by Administrator on 2016/2/4.
 */
public class DataNetPrinterInitializer extends ChannelInitializer {

    public IMsgCallback callback;

    public DataNetPrinterInitializer() {
    }

    public void initChannel(Channel channel) {
        NetPrintPackage stationPackage = new NetPrintPackage(channel);
        NetPrinterHandle handle = new NetPrinterHandle(stationPackage);
        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(30000, 0,0, TimeUnit.MILLISECONDS));
        channel.pipeline().addLast("handler", handle);
    }

}
