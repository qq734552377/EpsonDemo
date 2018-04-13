package com.ucast.jnidiaoyongdemo.Model;

/**
 * Created by pj on 2018/3/16.
 */

public class PrinterProtocol {

    //给打印机上电
    public static byte[] getPrinterSwitchOnProtocol(){
        byte[] res = new byte[]{0x02,'P','A',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //给打印机下电
    public static byte[] getPrinterSwitchOffProtocol(){
        byte[] res = new byte[]{0x02,'P','B',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //使打印机上能开始接受打印数据
    public static byte[] getPrinterStartPrinterProtocol(){
        byte[] res = new byte[]{0x02,'P','D',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //使打印机上不能接收数据
    public static byte[] getPrinterCancelPrinterProtocol(){
        byte[] res = new byte[]{0x02,'P','E',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //打印机切纸命令
    public static byte[] getPrinterCutPaperProtocol(){
        byte[] res = new byte[]{0x02,'P','C','2',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //查询打印机状态
    public static byte[] getPrinterStateProtocol(){
        byte[] res = new byte[]{0x02,'P','C','3',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }

}
