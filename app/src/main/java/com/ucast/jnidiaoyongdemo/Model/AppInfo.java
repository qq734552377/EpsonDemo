package com.ucast.jnidiaoyongdemo.Model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ucast.jnidiaoyongdemo.tools.SavePasswd;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

/**
 * Created by pj on 2016/11/22.
 */
public class AppInfo {


    private String appName;
    private String packageName;
    private String version;
    private int versionCode;
    private int appFlag;
    private Drawable appIcon;
    private String url;

    public AppInfo(PackageInfo info, PackageManager pm) {

        packageName = info.packageName;
        version = info.versionName;
        versionCode = info.versionCode;
        ApplicationInfo applicationInfo = info.applicationInfo;
        appName = (String) applicationInfo.loadLabel(pm);
        appIcon = applicationInfo.loadIcon(pm);
        appFlag = applicationInfo.flags;

        String url = SavePasswd.getInstace().getIp("dizuoUpdateUrl", YinlianHttpRequestUrl.DIZUOUPDATEURL);

        int index = url.indexOf("GetVersionFile");

        String ip_head = url.substring(0, index);


        switch (info.packageName) {
            case ApkInfo.SERVICE_PACKAGENAME:
                this.url = ip_head + "GetVersionFile?merchant=zy&device=service";
                break;
            case ApkInfo.BLUE_PACKAGENAME:
                this.url = ip_head + "GetVersionFile?merchant=zy&device=blue";
                break;
            case ApkInfo.JIANKONG_PACKAGENAME:
                this.url = ip_head + "GetVersionFile?merchant=zy&device=jiankong";
                break;
            case ApkInfo.PADTEST_PACKAGENAME:
                this.url = ip_head + "GetVersionFile?merchant=zy&device=padtest";
                break;
        }

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getAppFlag() {
        return appFlag;
    }

    public void setAppFlag(int appFlag) {
        this.appFlag = appFlag;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public static String getVersionName(Context context, String packageName) {
        if (getPackageInfo(context, packageName) == null) {
            return "0.0";
        }
        return getPackageInfo(context, packageName).versionName;
    }

    //版本号
    public static int getVersionCode(Context context, String packageName) {
        return getPackageInfo(context, packageName).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
