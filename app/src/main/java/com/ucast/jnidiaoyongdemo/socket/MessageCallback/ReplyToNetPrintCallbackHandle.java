package com.ucast.jnidiaoyongdemo.socket.MessageCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Common;
import com.ucast.jnidiaoyongdemo.Model.ReadPicture;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.Model.SendPackage;
import com.ucast.jnidiaoyongdemo.socket.Message.Heartbeat;
import com.ucast.jnidiaoyongdemo.socket.Message.MessageBase;
import com.ucast.jnidiaoyongdemo.socket.Message.PrintMessage;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import java.io.File;

import io.netty.channel.Channel;

/**
 * Created by Administrator on 2016/2/4.
 */
public class ReplyToNetPrintCallbackHandle implements IMsgCallback {
    public static final String LOG_PATH = Environment.getExternalStorageDirectory().toString() + "/Ucast";

    public void Receive(Channel _channel, Object obj) {
        if (obj == null)
            return;
        if (!(obj instanceof MessageBase))
            return;
        MessageBase msg = (MessageBase) obj;

        if (msg instanceof Heartbeat) {
            Heartbeat heartbeat = (Heartbeat) msg;
            if (heartbeat == null)
                return;
            heartBeatForClient(_channel, heartbeat.data);
        }

        if (msg instanceof PrintMessage) {
            printPicture(_channel, (PrintMessage) msg);
            return;
        }


    }

    private void heartBeatForClient(Channel channel, String dataStr) {
        byte[] buffer = Common.decode(dataStr);
        if (buffer == null) {
            channel.close();
            return;
        }
        String str = new String(buffer);
        if (!str.equals("Version.1.0")) {
            channel.close();
            return;
        }
        SendPackage.sendChannelHeartbeatData(channel);
    }

    private void printPicture(Channel channel,PrintMessage msg){
        try {
            String path = msg.Data;
            File file = new File(path);
            if (!file.exists()) {
                //回复错误
                return;
            }
            String filename = file.getName();
            int index = filename.lastIndexOf(".");
            if (index <= -1) {
                //回复错误
                return;
            }
            String format = filename.substring(index + 1, filename.length());
            if (!format.toUpperCase().equals("BMP")) {
                //回复错误
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                ExceptionApplication.gLogger.info("bitmap Data Null:" + path);
                return;
            }
            if (bitmap.getWidth() != 384) {
                return;
            }
            ExceptionApplication.gLogger.info("Data File Image Path Name:" + path);
            ExceptionApplication.gLogger.info("Data File Image Channel:" + channel.id().toString());
            ReadPicture picture = ReadPictureManage.GetInstance().GetReadPicture(0);
            if (picture == null)
                return;
            picture.Add(new BitmapWithOtherMsg(bitmap, channel, filename));
        } catch (Exception e) {

        }
    }
}
