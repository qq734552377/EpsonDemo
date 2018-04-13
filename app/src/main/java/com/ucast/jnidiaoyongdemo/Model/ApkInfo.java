package com.ucast.jnidiaoyongdemo.Model;

import android.os.Environment;

/**
 * Created by pj on 2016/11/22.
 */
public class ApkInfo {
    private String version;
    public static final String apkPath = Environment.getExternalStorageDirectory().toString()+"/service.apk";;
    private long apkSize;


    public static final String SERVICE_PACKAGENAME="com.project.services";
    public static final String BLUE_PACKAGENAME="com.example.zxc.blue";
    public static final String DIZUO_PACKAGENAME="jni.ucab.ucast.deblue";
    public static final String JIANKONG_PACKAGENAME="ucast.com.dizuo_connect_test";
    public static final String PADTEST_PACKAGENAME="ucast.com.ucast_test_pad";

    public ApkInfo(String version, int apkSize) {
        this.version = version;
        this.apkSize = apkSize;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }



}
