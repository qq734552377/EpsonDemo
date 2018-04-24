package com.ucast.jnidiaoyongdemo.socket.net_print;

import android.os.SystemClock;
import android.util.Log;


import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Model.SendPackage;
import com.ucast.jnidiaoyongdemo.mytime.MyTimeTask;
import com.ucast.jnidiaoyongdemo.mytime.MyTimer;
import com.ucast.jnidiaoyongdemo.socket.Memory.NettyClientMap;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;


/**
 * Created by pj on 2016/4/21.
 */
public class WhileCheckClient {

    private static Object obj = new Object();

    private static MyTimer timer;

    private static int TotalNumber = 1;

    private static long heartTime;

    public static void StartTimer() {
        timer = new MyTimer(new MyTimeTask(new Runnable() {
            public void run() {
                synchronized (obj) {
                    NioTcpClient client = NettyClientMap.GetChannel(Config.NETPrintName);
                    if (client == null)
                        return;
                    long second = (SystemClock.elapsedRealtime() - heartTime) / 1000;
                    if (second >= 12) {
                        client.Dispose();
                    } else {
                        boolean result = WhetherCconnect(client);
                        if (result)
                            return;
                        ExceptionApplication.gLogger.info("Client No Ip:" + client.Ip + " Ssid:" + client.SSid);
                    }
                    NettyClientMap.Remove(Config.NETPrintName);
                    TotalNumber++;
                    ClinetRun(client.SSid, client.Password, client.Ip, false);
                }
            }
        }), 3000L, 3000L);
        timer.initMyTimer().startMyTimer();
    }


    public static void HeartbeatTimeUpdate() {
        synchronized (obj) {
            heartTime = SystemClock.elapsedRealtime();
            System.out.println("------心跳发送--回来了------");
        }
    }

    public static void UpTotalNumber() {
        synchronized (obj) {
            TotalNumber = 1;
        }
    }

    private static boolean WhetherCconnect(NioTcpClient client) {
        if (client.WaitChannel == 0) {
            return true;
        } else if (client.WaitChannel == 1) {
            boolean success = client.f.isSuccess();
            SendHead();
            return true;
        }
        return false;
    }

    public static void Run(String ssid, String password, String ip) {
        synchronized (obj) {
            NioTcpClient client = NettyClientMap.GetChannel(Config.NETPrintName);
            if (client == null) {
                TotalNumber = 1;
                ClinetRun(ssid, password, ip, true);
                ExceptionApplication.gLogger.info("First Connect Ip:" + ip + " Ssid:" + ssid);
                return;
            }
            if (client.WaitChannel == 2 || client.WaitChannel == 0) {
                client.Dispose();
                Log.e("calm", "Being and connection error " + ip + " Ssid:" + ssid);
                NettyClientMap.Remove(Config.NETPrintName);
                TotalNumber = 1;
                ClinetRun(ssid, password, ip, true);
                ExceptionApplication.gLogger.info("Reset Connect Ip:" + ip + " Ssid:" + ssid);
                return;
            }
            if (ssid.equals(client.SSid) && ip.equals(client.Ip)) {
                if (client.Old) {
                    Log.e("calm", "Old Connection close " + ip + " Ssid:" + ssid);
                    return;
                }
                ExceptionApplication.gLogger.info("Same Connect Ip:" + ip + " Ssid:" + ssid);
                return;
            }
            client.Dispose();
            Log.e("calm", "Connection close " + ip + " Ssid:" + ssid);
            NettyClientMap.Remove(Config.NETPrintName);
            TotalNumber = 1;
            ClinetRun(ssid, password, ip, true);

        }
    }

    private static void ClinetRun(String ssid, String password, String ip, boolean old) {
        NioTcpClient clientFor = new NioTcpClient(ssid, password, ip, Config.NET_PRINT_PORT, old);
        heartTime = SystemClock.elapsedRealtime();
        NettyClientMap.Add(clientFor);
        new Thread(clientFor).start();
    }

    private static void SendHead() {
        String heart = "@1105,123456789$";
        SendPackage.sendDataToNetPrintPort(heart.getBytes());
    }
}
