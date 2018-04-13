package com.ucast.jnidiaoyongdemo.socket;


import com.ucast.jnidiaoyongdemo.socket.Memory.NettyChannelMap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import com.ucast.jnidiaoyongdemo.socket.MessageProtocol.Package;

/**
 * Created by Administrator on 2016/2/3.
 */
public class TcpServerHandle extends ChannelInboundHandlerAdapter {

    public Package packageMessage;

    public TcpServerHandle(Package _packageMessage) {
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
            packageMessage.Import(buffer, 0, len);
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            ctx.channel().close();
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
        //System.out.println("服务器异常退出" + cause.getMessage());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.Add(ctx.channel());
        //客户端连接
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.Remove(ctx.channel().id().toString());
        if (packageMessage == null)
            return;
        packageMessage.Dispose();
    }
}
