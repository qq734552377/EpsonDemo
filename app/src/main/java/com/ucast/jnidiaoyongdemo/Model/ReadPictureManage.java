package com.ucast.jnidiaoyongdemo.Model;

import com.ucast.jnidiaoyongdemo.queue_ucast.ReadPicture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/16.
 */
public class ReadPictureManage {

    private List<ReadPicture> _dispatches = new ArrayList<>();

    private static ReadPictureManage _manage;

    public ReadPictureManage() {
        for (int i = 0; i < 1; i++) {
            _dispatches.add(new ReadPicture());
        }
    }
    public static ReadPictureManage GetInstance() {
        if (_manage == null) {
            synchronized (ReadPictureManage.class) {
                if (_manage == null) {
                    return _manage = new ReadPictureManage();
                }
            }
        }
        return _manage;
    }

    public ReadPicture GetReadPicture(int setIndex) {
        if (setIndex < 0 || setIndex >= 1) {
            return null;
        }
        return _dispatches.get(setIndex);
    }

}
