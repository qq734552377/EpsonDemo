package com.ucast.jnidiaoyongdemo.Serial;

import com.ucast.jnidiaoyongdemo.Model.Common;
import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.ListPictureQueue;
import com.ucast.jnidiaoyongdemo.Model.MsCardProtocol;
import com.ucast.jnidiaoyongdemo.Model.PrinterProtocol;
import com.ucast.jnidiaoyongdemo.Model.SendPackage;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.tools.ArrayQueue;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by pj on 2016/1/22.
 */
public class KeyBoardSerial {
    private SerialPort ser;
    public OutputStream output;
    private String Path = Config.KeyboardSerial;

    public KeyBoardSerial(String path) {
        Path = path;
    }

    public boolean Open() {
        try {
            //实例串口
            ser = new SerialPort(new File(Path), SerialPort.KEYBOARD_TYPE ,0);
            //获取输出流
            output = ser.getOutputStream();
            ExceptionApplication.gLogger.info("keyboard serial "+Path+" open normally !");
            return true;
        } catch (IOException e) {
            ExceptionApplication.gLogger.info("keyboard serial open failed !");
            return false;
        }
    }



}
