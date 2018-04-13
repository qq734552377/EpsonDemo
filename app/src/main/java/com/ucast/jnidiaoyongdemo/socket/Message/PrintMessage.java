package com.ucast.jnidiaoyongdemo.socket.Message;

/**
 * Created by Administrator on 2016/2/3.
 */
public class PrintMessage extends MessageBase {
    public int Total;
    public int CurrentPackt;
    public String Data;

    public void Load(String[] str) {
        super.Load(str);

            Cmd = str[0];
            Total = Integer.parseInt(str[1]);
            CurrentPackt = Integer.parseInt(str[2]);
            Data = str[3];

    }
}


