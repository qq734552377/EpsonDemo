package com.ucast.jnidiaoyongdemo.xutilEvents;

/**
 * Created by pj on 2018/6/26.
 */
public class EthEvent {
  public boolean result = false;

    public EthEvent(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
