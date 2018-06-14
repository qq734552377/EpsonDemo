package com.ucast.jnidiaoyongdemo.socket.net_print;

import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.socket.serviceforclient.DataServerInitializer;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Administrator on 2016/2/4.
 */
public class NioNetPrintServer implements Runnable {

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ServerBootstrap server;


    public NioNetPrintServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }


    public void open() {
        try {
            server = new ServerBootstrap();
            server.group(bossGroup, workerGroup);
            server.channel(NioServerSocketChannel.class);// 类似NIO中serverSocketChannel
            //需要将服务端扩容
            server.option(ChannelOption.SO_BACKLOG, 1024*1024*1);// 配置TCP参数
            server.childHandler(new DataNetPrinterInitializer());
            // 服务器启动后 绑定监听端口 同步等待成功 主要用于异步操作的通知回调 回调处理用的ChildChannelHandler


            ChannelFuture f = server.bind(Config.NET_PRINT_PORT).sync();
            ExceptionApplication.gLogger.info("开启网口监听...");
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {

            }
        } catch (Exception e) {

        } finally {

            Close();
        }
    }


    public void Close() {
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        if (workerGroup != null)
            workerGroup.shutdownGracefully();
    }

    @Override
    public void run() {
        open();
    }

}
