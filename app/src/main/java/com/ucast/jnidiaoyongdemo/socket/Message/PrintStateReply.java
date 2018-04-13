package com.ucast.jnidiaoyongdemo.socket.Message;

/**
 * Created by Administrator on 2016/3/7.
 */
public class PrintStateReply extends MessageBase {
    public int Total;
    public int CurrentPackt;
    public int Type;
    public String Number;
    public String data;

    public String paper;

    public String temp;

    public void Load(String[] str) {
        super.Load(str);

            Cmd = str[0];
            Type = Integer.parseInt(str[1]);
            Number = str[2];
            Total = Integer.parseInt(str[3]);
            CurrentPackt = Integer.parseInt(str[4]);
            data = str[6];
            paper = data.substring(0, 1);
            temp = data.substring(1, data.length());

    }
}
