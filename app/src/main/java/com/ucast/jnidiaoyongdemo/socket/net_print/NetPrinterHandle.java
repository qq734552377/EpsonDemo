package com.ucast.jnidiaoyongdemo.socket.net_print;


import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;
import com.ucast.jnidiaoyongdemo.socket.Memory.NetPrinterChannelMap;
import com.ucast.jnidiaoyongdemo.socket.MessageProtocol.Package;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;


import java.io.File;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Administrator on 2016/2/4.
 */
public class NetPrinterHandle extends ChannelInboundHandlerAdapter {

    public Package packageMessage;

    public NetPrinterHandle(Package _packageMessage) {
        packageMessage = _packageMessage;
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            ByteBuf buff = (ByteBuf) msg;
            int len = buff.readableBytes();
            byte[] buffer = new byte[len];
            buff.readBytes(buffer);
            if (packageMessage == null)
                return;
            MyTools.writeToFile(EpsonPicture.TEMPBITPATH + File.separator + "netPrint.txt",EpsonParseDemo.printHexString(buffer) + "\n");
//            MyTools.writeToFile(EpsonPicture.TEMPBITPATH + File.separator + "netPrintStr.txt",new String(buffer) + "\n");
            Channel channel = ctx.channel();
            channel.writeAndFlush(buffer);
            packageMessage.Import(buffer, 0, len);
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            ctx.close();
            ExceptionApplication.gLogger.info("NetPrinterHandle channelRead : read exception  -->"+ ctx.channel().id()+"  Exception : "+e.toString());
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
//        ExceptionApplication.gLogger.info("channelReadComplete : deblue service start sucess waitting for linked by client");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {//心跳检测还没有测试
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        Channel channel = ctx.channel();
        IdleStateEvent event = (IdleStateEvent) evt;
        if(event.state() == IdleState.ALL_IDLE)
        {
            channel.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NetPrinterChannelMap.Add(ctx.channel());
        //客户端连接
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NetPrinterChannelMap.Remove(ctx.channel().id().toString());
        if (packageMessage == null)
            return;
        packageMessage.Dispose();
    }
}
