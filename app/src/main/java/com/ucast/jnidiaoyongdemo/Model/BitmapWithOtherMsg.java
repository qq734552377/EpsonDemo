package com.ucast.jnidiaoyongdemo.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.netty.channel.Channel;

/**
 * Created by Administrator on 2018/3/19.
 */

public class BitmapWithOtherMsg {
    private Bitmap bitmap;
    private Channel channel;
    private String path;
    private boolean isCutPapper;

    public BitmapWithOtherMsg(String path) {
//        this.bitmap = BitmapFactory.decodeFile(path);
        this.path = path;
    }
    public BitmapWithOtherMsg(boolean isCutPapper) {
        this.isCutPapper = isCutPapper;
    }
    public BitmapWithOtherMsg(Bitmap bitmap, String path) {
        this.bitmap = bitmap;
        this.path = path;
    }
    public BitmapWithOtherMsg(Bitmap bitmap, Channel channel, String path) {
        this.bitmap = bitmap;
        this.channel = channel;
        this.path = path;
    }

    public boolean isCutPapper() {
        return isCutPapper;
    }

    public void setCutPapper(boolean cutPapper) {
        isCutPapper = cutPapper;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeFile(path);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
