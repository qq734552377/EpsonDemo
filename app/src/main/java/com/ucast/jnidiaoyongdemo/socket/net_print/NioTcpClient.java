package com.ucast.jnidiaoyongdemo.socket.net_print;

import com.ucast.jnidiaoyongdemo.Model.Config;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Administrator on 2016/2/4.
 */
public class NioTcpClient implements Runnable {

    public String Ip;

    private int Port;

    public String SSid;

    public String Password;

    private EventLoopGroup group;

    public ChannelFuture f;

    public String Name;

    private boolean mDispose;

    public int WaitChannel;

    public boolean Old;

    public boolean NoResult;

    public NioTcpClient() {

    }

    public NioTcpClient(String ssid, String password, String ip, int port, boolean old) {
        Ip = ip;
        Port = port;
        Name = Config.NETPrintName;
        SSid = ssid;
        Password = password;
        Old = old;
    }

    @Override
    public void run() {
        connect();
    }

    public void connect() {
        try {
//            synchronized (this) {
//                if (mDispose)
//                    return;
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            //TODO 可能需要扩容
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024*100);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new DataClientInitializer());
            f = bootstrap.connect(Ip, Port).sync();
//            if (f.isSuccess()) {
//                WaitChannel = 1;
//                if (Old) {
//                    Common.SendHandle();
//                } else {
//                    Common.SendRecovery();
//                }
//                Common.ChangeFirstStatus();
//                NoResult = true;
//            }
            //}
            //等待链接关闭
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {

            }
        } catch (Exception e) {

        } finally {

            Close();
        }
    }

    private void Close() {
        synchronized (this) {
            if (mDispose)
                return;
            group.shutdownGracefully();
            WaitChannel = 2;
        }
    }

    public void Dispose() {
        synchronized (this) {
            if (mDispose)
                return;
            mDispose = true;
            if (group == null)
                return;
            group.shutdownGracefully();
        }
    }

    public boolean Send(byte[] Data) {
        try {
            if (f == null)
                return false;
            if (!f.isSuccess())
                return false;
            Channel channel = f.channel();
            if (channel == null)
                return false;
            ByteBuf resp = Unpooled.copiedBuffer(Data);
            channel.writeAndFlush(resp);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean ClietnStatus() {
        try {
            if (f == null)
                return false;
            if (!f.isSuccess())
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
