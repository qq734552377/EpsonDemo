package com.ucast.jnidiaoyongdemo.bmpTools;

import android.graphics.Typeface;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2018/1/8.
 */

public class PrintAndDatas {
    //文字数据
    public String datas = "";
    public int FONT_SIZE = 24 ;
    public int LINE_STRING_NUMBER = SomeBitMapHandleWay.PRINT_WIDTH / (FONT_SIZE / 2) ;
    public int OFFSET_X = 0 ;
    public int OFFSET_Y = 40 ;
    public int FONT_SIZE_TIMES = 1 ;
    public int LINE_HEIGHT = 40 ;
    public int FONT_SIZE_TYPE = Typeface.NORMAL ;
    public String FONT = "simsun.ttf" ;

    //是否是位图
    public boolean isBit = false;

    //位图的宽度倍率
    public int bitWidthRate = 1;
    //位图的高度倍率
    public int bitHeightRate = 1;
    //位图的宽度
    public int bitWidth = 48;
    //位图的高度
    public int bitHeight = 0;
    //位图数据
    public byte[] bitDatasByte ;

    public PrintAndDatas() {
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public void addDatas(String addStr) {
        this.datas += addStr;
    }
    public byte[] getBitDatasByte() {
        return bitDatasByte;
    }

    public void setBitDatasByte(byte[] bitDatasByte) {
        this.bitDatasByte = bitDatasByte;
    }

    public int getBitWidth() {
        return bitWidth * bitWidthRate;
    }

    public void setBitWidth(int bitWidth) {
        this.bitWidth = bitWidth;
    }

    public int getBitHeight() {
        return bitHeight * bitHeightRate;
    }

    public void setBitHeight(int bitHeight) {
        this.bitHeight = bitHeight;
    }
}
