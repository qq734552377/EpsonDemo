package com.ucast.jnidiaoyongdemo.Model;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/1/20.
 */
public class Config {
    public static String STATION_ID = "SN888888";
    public final static String PrinterSerialName = "ttyGS0";
    public final static String UsbSerialName = "g_print0";
    public final static String PrinterSerial = "/dev/ttymxc4";
    public final static String UsbSerial = "/dev/g_printer0";
    public final static int PRINT_BAIDRATE = 115200 * 4;
    public final static int USB_BAIDRATE = 115200;

    public static String Password_ID = "STATION_IDYL87451";

    public static int STATION_PORT = 43708;

    public static int isSettingAP = 0; //是否正在设置AP 0,准备，1.正在设置AP  2.设置结束

    public static String STATION_SERIAL_PORT;

    public static List<List<byte[]>> base_list = new ArrayList<>(); //所有接受完整的图片

}
