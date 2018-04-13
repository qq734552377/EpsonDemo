package com.ucast.jnidiaoyongdemo.socket.Message;

/**
 * Created by Administrator on 2016/6/3.
 */
public class IcPass extends MessageBase {
    public int Total;
    public int CurrentPackt;
    public String data;
    public void Load(String[] str) {
        super.Load(str);
        Cmd = str[0];
        Total = Integer.parseInt(str[1]);
        CurrentPackt = Integer.parseInt(str[2]);
        data = str[3];
    }
}
