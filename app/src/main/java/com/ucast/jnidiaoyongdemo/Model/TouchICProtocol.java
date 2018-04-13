package com.ucast.jnidiaoyongdemo.Model;

/**
 * Created by pj on 2018/4/3.
 */

public class TouchICProtocol {

    //设备初始化命令
    public static byte[] getInitTouchICProtocol(){
        byte[] res = new byte[]{0x02,'I','A',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //设备回收命令
    public static byte[] getRecycleTouchICProtocol(){
        byte[] res = new byte[]{0x02,'I','B',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //查询设备可用卡槽数
    public static byte[] getTouchICCAMNumberProtocol(){
        byte[] res = new byte[]{0x02,'I','C',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //打开设备的对应卡槽  通过给定的序号 从0开始
    public static byte[] getOpenTouchICCAMByIdProtocol(byte numId){
        byte[] res = new byte[]{0x02,'I','D',0x01,0x01,0x00,0x00};
        res[6] = (byte) (numId & 0xFF);
        return Common.pakageOneProtocol(res);
    }
    //关闭设备的对应卡槽  通过给定的序号 从0开始
    public static byte[] getCloseTouchICCAMByIdProtocol(byte numId){
        byte[] res = new byte[]{0x02,'I','E',0x01,0x01,0x00,0x00};
        res[6] = (byte) (numId & 0xFF);
        return Common.pakageOneProtocol(res);
    }
    //查询IC卡是否在设备对应卡槽上  通过给定的序号 从0开始
    public static byte[] getQueryTouchICCardIsOnCAMByIdProtocol(byte numId){
        byte[] res = new byte[]{0x02,'I','F',0x01,0x01,0x00,0x00};
        res[6] = (byte) (numId & 0xFF);
        return Common.pakageOneProtocol(res);
    }





    //给设备对应卡槽下电  通过给定的序号 从0开始
    public static byte[] getSwitchOffICCAMByIdProtocol(byte numId){
        byte[] res = new byte[]{0x02,'I','H',0x01,0x01,0x00,0x00};
        res[6] = (byte) (numId & 0xFF);
        return Common.pakageOneProtocol(res);
    }



}
