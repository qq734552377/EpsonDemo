package com.ucast.jnidiaoyongdemo.Serial;


import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.MermoyUsbSerial;
import com.ucast.jnidiaoyongdemo.mytime.MyTimeTask;
import com.ucast.jnidiaoyongdemo.mytime.MyTimer;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import android.os.Handler;

/**
 * Created by pj on 2016/6/6.
 */
public class UsbSerialRestart {

    private static MyTimer timer;

    private static boolean restart;

    public static void StartTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            public void run() {
                synchronized (UsbSerialRestart.class) {
                    try {
                        if (!restart)
                            return;
                        ExceptionApplication.gLogger.error("Usb serial error close!  We willl resart it.....");
                        UsbSerial oldUsbSerial = MermoyUsbSerial.GetChannel(Config.UsbSerialName);
                        Handler handler = oldUsbSerial.getHandler();
                        UsbSerial padSerialPort = new UsbSerial(Config.UsbSerial,handler);
                        boolean isOpen = padSerialPort.Open();
                        MermoyUsbSerial.Remove(Config.UsbSerialName);
                        MermoyUsbSerial.Add(padSerialPort);
                        restart = false;
                    } catch (Exception e) {
                        restart = false;
                    }
                }
            }
        }), 2000L, 4000L);
        timer.initMyTimer().startMyTimer();
    }

    public static void Check() {
        synchronized (UsbSerialRestart.class) {
            restart = true;
        }
    }
}
