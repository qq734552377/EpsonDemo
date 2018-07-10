package com.ucast.jnidiaoyongdemo.jsonObject;

/**
 * Created by pj on 2018/4/17.
 */
public class BaseHttpResult {
    public static String SUCCESS = "Success";

    private String Data;
    private String Info;
    private String MsgType;
    private String Total;

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }
}
