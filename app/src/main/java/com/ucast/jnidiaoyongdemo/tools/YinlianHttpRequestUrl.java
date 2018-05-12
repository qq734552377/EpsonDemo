package com.ucast.jnidiaoyongdemo.tools;

import java.io.File;

/**
 * Created by Administrator on 2018/3/20.
 */

public class YinlianHttpRequestUrl {
//    public static final String HOST = "http://192.168.0.30:12907";
//    public static final String HOST = "http://192.168.0.56:12907";
    public static final String ANALYZEHOST = "http://58.246.122.118:12103";
//    public static final String ANALYZEHOST = "http://58.246.122.118:12888";
    public static final String HOST = "http://58.246.122.118:12101";
    public static final String UPLOADFILEURL = ANALYZEHOST + File.separator + "api/PictureAnalysisUpload";
    public static final String UPLOADBASE64URL = ANALYZEHOST + File.separator + "api/TextAnalysisUpload";
    public static final String TIMEUPDATEURL = HOST + File.separator + "Heart/HeartDetection";
    public static final String DIZUOUPDATEURL = ANALYZEHOST + File.separator + "api/TextAnalysisUpload";
}
