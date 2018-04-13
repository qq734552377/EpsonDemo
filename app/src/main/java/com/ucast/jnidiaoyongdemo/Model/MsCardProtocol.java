package com.ucast.jnidiaoyongdemo.Model;

/**
 * Created by pj on 2018/4/3.
 */

public class MsCardProtocol {

    //磁卡设备打开
    public static byte[] getOpenMsCardProtocol(){
        byte[] res = new byte[]{0x02,'M','A',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //磁卡设备关闭
    public static byte[] getCloseMsCardProtocol(){
        byte[] res = new byte[]{0x02,'M','B',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //注册刷卡
    public static byte[] getRegisterMsCardProtocol(){
        byte[] res = new byte[]{0x02,'M','C',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //取消刷卡
    public static byte[] getUnregisterMsCardProtocol(){
        byte[] res = new byte[]{0x02,'M','D',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //下发轨道号获取磁道数据长度 轨道编号从0-2
    public static byte[] getGetDataLenthMsCardByNumProtocol(byte numId){
        byte[] res = new byte[]{0x02,'M','F',0x01,0x01,0x00,0x00};
        res[6] = (byte) (numId & 0xFF);
        return Common.pakageOneProtocol(res);
    }
    //下发轨道号获取磁道数据 轨道编号从0-2
    public static byte[] getGetDataMsCardByNumProtocol(byte numId){
        byte[] res = new byte[]{0x02,'M','G',0x01,0x01,0x00,0x00};
        res[6] = (byte) (numId & 0xFF);
        return Common.pakageOneProtocol(res);
    }



}
