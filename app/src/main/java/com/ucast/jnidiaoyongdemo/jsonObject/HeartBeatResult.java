package com.ucast.jnidiaoyongdemo.jsonObject;

/**
 * Created by pj on 2018/5/12.
 */
public class HeartBeatResult {
    public String ServerTime;
    public String IsOpenPrintModel;
    public String IsNetPrintUploadToService;

    public String getServerTime() {
        return ServerTime;
    }

    public void setServerTime(String serverTime) {
        ServerTime = serverTime;
    }

    public String getIsOpenPrintModel() {
        return IsOpenPrintModel;
    }

    public void setIsOpenPrintModel(String isOpenPrintModel) {
        IsOpenPrintModel = isOpenPrintModel;
    }

    public String getIsNetPrintUploadToService() {
        return IsNetPrintUploadToService;
    }

    public void setIsNetPrintUploadToService(String isNetPrintUploadToService) {
        IsNetPrintUploadToService = isNetPrintUploadToService;
    }
}
