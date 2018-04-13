package com.ucast.jnidiaoyongdemo.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/16.
 */
public class ReadPictureManage {

    private List<ReadPicture> _dispatches = new ArrayList<>();

    private static ReadPictureManage _manage;

    public static Object Lock = new Object();

    private int _index = 0;

    public ReadPictureManage() {
        for (int i = 0; i < 1; i++) {
            _dispatches.add(new ReadPicture());
        }
    }
    public static ReadPictureManage GetInstance() {
        if (_manage == null) {
            synchronized (Lock) {
                if (_manage == null) {
                    return _manage = new ReadPictureManage();
                }
            }
        }
        return _manage;
    }
    public int GetDispatchId() {
        synchronized (_dispatches) {
            _index++;
            if (_index >= 1) {
                _index = 0;
            }
            return _index;
        }
    }
    public void Dispose() {
        for (int i = 0; i < 1; i++) {
            ReadPicture picture = _dispatches.get(i);
            if (picture == null)
                continue;
            picture.Dispose();
        }
    }

    public ReadPicture GetReadPicture(int setIndex) {
        if (setIndex < 0 || setIndex >= 1) {
            return null;
        }
        return _dispatches.get(setIndex);
    }

}
