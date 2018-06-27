package com.ucast.jnidiaoyongdemo.xutilEvents;

/**
 * Created by pj on 2018/6/26.
 */
public class MsCardEvent {
    public int type;
    public String msg;

    public MsCardEvent(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
