package com.ucast.jnidiaoyongdemo.Model;

import android.os.Environment;

import com.ucast.jnidiaoyongdemo.Serial.SerialPort;
import com.ucast.jnidiaoyongdemo.tools.CrashHandler;
import com.ucast.jnidiaoyongdemo.tools.MyTools;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/1/20.
 */
public class Config {
    public static String DEVICE_ID = MyTools.getMacAddress();
    public final static String PrinterSerialName = "ttymxc4";
    public final static String UsbWithByteSerialName = "g_print0";
    public final static String NETPrintName = "ucast_net_print";
    public final static String KeyboardSerialName = "hidg0";
    public final static String PrinterSerial = "/dev/ttymxc4";
    public final static int PrinterSerialType = SerialPort.PRINTER_TYPE;
    public final static String KeyboardSerial = "/dev/hidg0";
    public final static String UsbSerial = "/dev/g_printer0";
    public final static int PRINT_BAIDRATE = 115200 * 4;
    public final static int USB_BAIDRATE = 115200;
    public final static int NET_PRINT_PORT = 9100;
    public static final String PICPATHDIR =  CrashHandler.ALBUM_PATH + "/pic";
    public static final String TESTRESULTDIR = Environment.getExternalStorageDirectory().toString()+"/Ucast";

}
