package com.ucast.jnidiaoyongdemo.bmpTools;

import android.graphics.Bitmap;

import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.MyTools;
import com.ucast.jnidiaoyongdemo.tools.YinlianHttpRequestUrl;

import java.util.List;

/**
 * Created by pj on 2018/4/23.
 */
public class HandleEpsonDataByUcastPrint {

    public static void serialString(byte[] string) {
        try {
            if(isContainByteArr(string, EpsonParseDemo.STARTEPSONBYTE)){
                List<String> paths = EpsonParseDemo.parseEpsonBitData(string);
                String p = SomeBitMapHandleWay.compoundOneBitPic(paths);
                MyTools.uploadFileByQueue(p);
                return;
            }
            printOne(string);
        } catch (Exception e) {
            ExceptionApplication.gLogger.info("paser bitmap error ");
            e.printStackTrace();
        }
    }

    public static String printOne(byte[] data){
        String path = "" ;
        List<byte[]> byteList = EpsonParseDemo.getEpsonFromByteArr(data);
        try {
            List<PrintAndDatas> printdatas = EpsonParseDemo.parseEpsonByteList(byteList);

            List<PrintAndDatas> goodPrintdatas = EpsonParseDemo.makeListIWant(printdatas);

            List<Bitmap> bmps = EpsonParseDemo.parseEpsonBitDataAndStringReturnBitmap(goodPrintdatas);

            path = SomeBitMapHandleWay.compoundOneBitPicWithBimaps(bmps);
            if (path != null && path != ""){
                MyTools.uploadDataAndFileWithURLByQueue(goodPrintdatas.get(0).datas,path , YinlianHttpRequestUrl.UPLOADBASE64URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return path;
        }

    }
    private static boolean isContainByteArr(byte[] src,byte[] item){
        if (item.length < 3){
            return false;
        }
        boolean isContain = false;
        for (int i = 0; i < src.length; i++) {
            if (src[i] == item[2] && i > 1){
                if(src[i-1] == item[1] && src[i-2] == item[0]){
                    isContain = true;
                    return isContain;
                }
            }
        }
        return isContain;
    }
}
