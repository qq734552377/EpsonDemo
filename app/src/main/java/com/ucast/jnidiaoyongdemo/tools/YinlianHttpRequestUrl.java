package com.ucast.jnidiaoyongdemo.tools;

import java.io.File;

/**
 * Created by Administrator on 2018/3/20.
 */

public class YinlianHttpRequestUrl {
//    public static final String HOST = "http://192.168.0.30:12907";
//    public static final String HOST = "http://192.168.0.56:12907";
    public static final String HOST = "http://58.246.122.118:12103";
    public static final String UPLOADFILEURL = HOST + File.separator + "api/PictureAnalysisUpload";
    public static final String UPLOADBASE64URL = HOST + File.separator + "api/TextAnalysisUpload";
    public static final String TIMEUPDATEURL = HOST + File.separator + "Heart/HeartDetection";
    public static final String DIZUOUPDATEURL = HOST + File.separator + "api/TextAnalysisUpload";
}
