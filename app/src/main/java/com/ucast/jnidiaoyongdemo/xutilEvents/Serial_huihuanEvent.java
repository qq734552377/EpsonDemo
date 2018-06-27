package com.ucast.jnidiaoyongdemo.xutilEvents;

/**
 * Created by pj on 2018/6/26.
 */
public class Serial_huihuanEvent {
    String msg ;
    boolean isFromSerial = true;

    public Serial_huihuanEvent(String msg) {
        this.msg = msg;
    }

    public Serial_huihuanEvent(String msg, boolean isFromSerial) {
        this.msg = msg;
        this.isFromSerial = isFromSerial;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isFromSerial() {
        return isFromSerial;
    }

    public void setFromSerial(boolean fromSerial) {
        isFromSerial = fromSerial;
    }
}
