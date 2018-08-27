package com.ucast.jnidiaoyongdemo.tools;

import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;

import java.io.File;

/**
 * Created by Administrator on 2018/3/20.
 */

public class YinlianHttpRequestUrl {
//    public static final String HEART_BEAT_HOST = "http://192.168.0.30:12907";
//    public static final String HEART_BEAT_HOST = "http://192.168.0.56:12907";
    public static String ANALYZEHOST = "http://58.246.122.118:12103";
    public static String ANALYZEHOST_TEST = "http://58.246.122.118:12888";
    public static String HEART_BEAT_HOST = "http://58.246.122.118:12101";
    public static String HEART_BEAT_HOST_TEST = "http://58.246.122.118:12890";

    public static String BMPHOST = ANALYZEHOST;
    public static String HEARTHOST = HEART_BEAT_HOST;

    public static String UPLOADFILEURL = BMPHOST + File.separator + "api/PictureAnalysisUpload";
    public static String UPLOADBASE64URL = BMPHOST + File.separator + "api/TextAnalysisUpload";
    public static String TIMEUPDATEURL = HEARTHOST + File.separator + "Heart/HeartDetection";
    public static String ADVURL = HEARTHOST + File.separator + "Adv/ProvideAdv";


    public static String DIZUOUPDATEURL = HEARTHOST + File.separator + "Heart/HeartDetection";

    public static String TEST_RESULT_UPLOAD_URL = "http://192.168.0.124:11175/Defalut/Add";
    static {
        String isMainService = SavePasswd.getInstace().getIp(SavePasswd.ISMAINSERVICE,SavePasswd.OPEN);
        boolean isMain = isMainService.equals(SavePasswd.OPEN) ? true : false;
        String pic_host = SavePasswd.getInstace().readxml(SavePasswd.BMPUPLOADHOST,ANALYZEHOST);
        String heartbeat_host = SavePasswd.getInstace().readxml(SavePasswd.HEARTBEATHOST,HEART_BEAT_HOST);
        if (!pic_host.equals(ANALYZEHOST))
            ANALYZEHOST = pic_host;
        if (!heartbeat_host.equals(HEART_BEAT_HOST))
            HEART_BEAT_HOST = heartbeat_host;
        if (isMain){
            setMainServiceUrl();
        }else {
            setTestServiceUrl();
        }
    }

    public static void setUrl(String bmpHost,String heartHOST){
        UPLOADFILEURL = bmpHost + File.separator + "api/PictureAnalysisUpload";
        UPLOADBASE64URL = bmpHost + File.separator + "api/TextAnalysisUpload";
        TIMEUPDATEURL = heartHOST + File.separator + "Heart/HeartDetection";
        ADVURL = heartHOST + File.separator + "Adv/ProvideAdv";
    }

    public static void setMainServiceUrl(){
        setUrl(ANALYZEHOST,HEART_BEAT_HOST);
    }
    public static void setTestServiceUrl(){
        setUrl(ANALYZEHOST_TEST,HEART_BEAT_HOST_TEST);
    }

    public static void writeToTempFile(){
        MyTools.writeToFile(EpsonPicture.TEMPBITPATH + "/uploadUrl.txt","图片：" + YinlianHttpRequestUrl.UPLOADFILEURL);
        MyTools.writeToFile(EpsonPicture.TEMPBITPATH + "/uploadUrl.txt","文字：" + YinlianHttpRequestUrl.UPLOADBASE64URL);
        MyTools.writeToFile(EpsonPicture.TEMPBITPATH + "/uploadUrl.txt","心跳：" + YinlianHttpRequestUrl.TIMEUPDATEURL);
        MyTools.writeToFile(EpsonPicture.TEMPBITPATH + "/uploadUrl.txt","广告：" + YinlianHttpRequestUrl.ADVURL);
        MyTools.writeToFile(EpsonPicture.TEMPBITPATH + "/uploadUrl.txt","测试结果：" + YinlianHttpRequestUrl.TEST_RESULT_UPLOAD_URL);
    }
}
