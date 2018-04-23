package com.ucast.jnidiaoyongdemo.socket.net_print;


import com.ucast.jnidiaoyongdemo.socket.MessageCallback.IMsgCallback;
import com.ucast.jnidiaoyongdemo.socket.MessageCallback.ReplyToNetPrintCallbackHandle;
import com.ucast.jnidiaoyongdemo.socket.MessageProtocol.NetPrintPackage;
import com.ucast.jnidiaoyongdemo.socket.MessageProtocol.StationPackage;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by Administrator on 2016/2/4.
 */
public class DataClientInitializer extends ChannelInitializer {

    public IMsgCallback callback;

    public DataClientInitializer() {
        callback = new ReplyToNetPrintCallbackHandle();
    }

    public void initChannel(Channel channel) {
        NetPrintPackage stationPackage = new NetPrintPackage(channel);
        stationPackage.callback = callback;
        TcpClientHandle handle = new TcpClientHandle(stationPackage);
        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(30000, 0,0, TimeUnit.MILLISECONDS));
        channel.pipeline().addLast("handler", handle);
    }

}
