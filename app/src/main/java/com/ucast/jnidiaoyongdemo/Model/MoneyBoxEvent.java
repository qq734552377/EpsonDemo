package com.ucast.jnidiaoyongdemo.Model;

/**
 * Created by pj on 2018/5/11.
 */
public class MoneyBoxEvent {
    boolean isShow = false;

    public MoneyBoxEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
