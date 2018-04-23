package com.ucast.jnidiaoyongdemo.socket.net_print;


import com.ucast.jnidiaoyongdemo.socket.MessageProtocol.Package;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Administrator on 2016/2/4.
 */
public class TcpClientHandle extends ChannelInboundHandlerAdapter {

    public Package packageMessage;

    public TcpClientHandle(Package _packageMessage) {
        packageMessage = _packageMessage;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ExceptionApplication.gLogger.info("TcpClientHandle exceptionCaught : get Exception -->"+cause.getMessage());
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ExceptionApplication.gLogger.info("TcpClientHandle channelActive : link is coming -->"+ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (packageMessage == null)
            return;
        packageMessage.Dispose();
        ExceptionApplication.gLogger.info("TcpClientHandle channelInactive : link is breaked -->"+ctx.channel().id());
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
            ctx.close();
            ExceptionApplication.gLogger.info("TcpClientHandle channelRead : read exception  -->"+ ctx.channel().id()+"  Exception : "+e.toString());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;
        if(event.state() == IdleState.READER_IDLE) {
            ctx.close();
            ExceptionApplication.gLogger.info("TcpClientHandle userEventTriggered : -->"+ ctx.channel().id());
        }
    }
}
