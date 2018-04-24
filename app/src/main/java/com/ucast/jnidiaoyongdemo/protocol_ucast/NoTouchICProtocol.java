package com.ucast.jnidiaoyongdemo.protocol_ucast;

import com.ucast.jnidiaoyongdemo.Model.Common;

/**
 * Created by pj on 2018/4/3.
 */

public class NoTouchICProtocol {

    //设备打开
    public static byte[] getOpenNoTouchICProtocol(){
        byte[] res = new byte[]{0x02,'N','A',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //设备关闭
    public static byte[] getCloseNoTouchICProtocol(){
        byte[] res = new byte[]{0x02,'N','B',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }

    /**
     *  搜索卡片
     *
     *  设置卡片搜索方式并开始搜索卡片
         收到命令后，先回复 00 表示开始搜索
         第一项一位数据：0表示搜索所有卡片
         1表示搜索’A’卡
         2表示只搜索‘B’卡
         3表示只搜索‘C’卡
         第二项一位数据：0表示只搜索一张卡片
         1表示搜索所有卡片
         第三项4位数据：表示搜索时间，单位为毫秒，如果小于0则一直搜索，直到命令CONTACTLESS_CARD_SEARCH_TARGET_END下达结束搜索
     * */
    public static byte[] getSearchNoTouchICCardProtocol(){
        byte[] res = new byte[]{0x02,'N','C',0x03,
                                                  0x01,0x00,0x00,
                                                  0x01,0x00,0x01,
                                                  0x04, 0x00, (byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,
                                };
        return Common.pakageOneProtocol(res);
    }

    //结束搜索卡片
    public static byte[] getEndSearchNoTouchICCardProtocol(){
        byte[] res = new byte[]{0x02,'N','D',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }
    //连接4字节编码的卡片
    public static byte[] getConnectNoTouchICCardWithFourNumberProtocol(byte[] fourNum){
        byte[] res = new byte[]{0x02,'N','E',0x01,0x04,0x00,0x00,0x00,0x00,0x00};
        if (fourNum.length == 4){
            res[6] = (byte)(fourNum[0] & 0xFF);
            res[7] = (byte)(fourNum[1] & 0xFF);
            res[8] = (byte)(fourNum[2] & 0xFF);
            res[9] = (byte)(fourNum[3] & 0xFF);
        }
        return Common.pakageOneProtocol(res);
    }
    //断开连接卡片
    public static byte[] getDisconnectNoTouchICCardWithFourNumberProtocol(){
        byte[] res = new byte[]{0x02,'N','F',0x01,0x00,0x00};
        return Common.pakageOneProtocol(res);
    }



}
