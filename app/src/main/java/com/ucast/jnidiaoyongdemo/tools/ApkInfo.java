package com.ucast.jnidiaoyongdemo.tools;

import android.os.Environment;

/**
 * Created by pj on 2016/11/22.
 */
public class ApkInfo {
    private String version;
    public static final String apkPath = Environment.getExternalStorageDirectory().toString()+"/dizuo.apk";;
    private long apkSize;

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
