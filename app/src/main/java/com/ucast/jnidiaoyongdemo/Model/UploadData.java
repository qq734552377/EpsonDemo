package com.ucast.jnidiaoyongdemo.Model;

/**
 * Created by Administrator on 2018/3/29.
 */

public class UploadData {
    private int id = -1;
    private int type ;
    private String path = null;
    private String upLoadURL = null;
    private String data = null;
    private String msg_create_time = null;
    private boolean isUploadSuccess = false;

    public static final int UPLOAD_FAIL = 0;
    public static final int UPLOAD_SUCESSS = 1;
    public static final int PATH_TYPE = 0;
    public static final int DATA_TYPE = 1;

    public String getUpLoadURL() {
        return upLoadURL;
    }

    public void setUpLoadURL(String upLoadURL) {
        this.upLoadURL = upLoadURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isUploadSuccess() {
        return isUploadSuccess;
    }

    public void setUploadSuccess(boolean uploadSuccess) {
        isUploadSuccess = uploadSuccess;
    }

    public String getMsg_create_time() {
        return msg_create_time;
    }

    public void setMsg_create_time(String msg_create_time) {
        this.msg_create_time = msg_create_time;
    }
}
