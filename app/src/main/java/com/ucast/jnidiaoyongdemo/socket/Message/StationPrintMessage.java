package com.ucast.jnidiaoyongdemo.socket.Message;

/**
 * Created by Administrator on 2016/2/17.
 */
public class StationPrintMessage extends MessageBase {

    public int Total;
    public int CurrentPackt;
    public String Data;
    public int Type;
    public String Number;
    public int Len;
    public void Load(String[] str) {
        super.Load(str);
        Cmd = str[0];
        Type = Integer.parseInt(str[1]);
        Number = str[2];
        Total = Integer.parseInt(str[3]);
        CurrentPackt = Integer.parseInt(str[4]);
        Len = Integer.parseInt(str[5]);
        Data = str[6];

    }
}
