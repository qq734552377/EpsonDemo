package com.ucast.jnidiaoyongdemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ucast.jnidiaoyongdemo.Model.UploadData;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;
import com.ucast.jnidiaoyongdemo.tools.SavePasswd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pj on 2018/3/29.
 */

public class UploadDBHelper {

    private static UploadDBHelper uploadDBHelper;
    private UploadOpenHelper openHelper;
    private SQLiteDatabase db;


    private UploadDBHelper() {
        if (openHelper == null) {
            openHelper = new UploadOpenHelper(ExceptionApplication.context);
        }
        db = openHelper.getWritableDatabase();
    }

    public static UploadDBHelper getInstance() {
        if (uploadDBHelper == null) {
            synchronized (UploadDBHelper.class) {
                if (uploadDBHelper == null) {
                    uploadDBHelper = new UploadDBHelper();
                }
            }
        }
        return uploadDBHelper;
    }

    /**
     * 查询数据库
     */
    public List<UploadData> selectAll() {
        List<UploadData> lists = new ArrayList<>();

        Cursor c = db.query(UploadOpenHelper.TABLENAME, null, UploadOpenHelper.UPLOAD_IS_UPLOAD_KEY + "=?", new String[]{UploadData.UPLOAD_FAIL + ""}, null, null, UploadOpenHelper.UPload_ID_KEY + " asc");
        while (c.moveToNext()) {
            UploadData one_data = new UploadData();
            one_data.setId(c.getInt(c.getColumnIndex(UploadOpenHelper.UPload_ID_KEY)));
            one_data.setType(c.getInt(c.getColumnIndex(UploadOpenHelper.UPload_TYPE_KEY)));
            one_data.setPath(c.getString(c.getColumnIndex(UploadOpenHelper.UPLOAD_PATH_KEY)));
            one_data.setData(c.getString(c.getColumnIndex(UploadOpenHelper.UPLOAD_DATA_KEY)));
            one_data.setMsg_create_time(c.getString(c.getColumnIndex(UploadOpenHelper.UPLOAD_MSG_CREATE_TIME)));
            one_data.setUploadSuccess(c.getInt(c.getColumnIndex(UploadOpenHelper.UPLOAD_IS_UPLOAD_KEY)) == UploadData.UPLOAD_FAIL ? false :true);
            lists.add(one_data);
        }
        return lists.size() > 0 ? lists : null;
    }

    /**
     *  更新数据库
     * */
    public int updateIsUploadById(boolean isUpload,int id){
        ContentValues values=new ContentValues();
        values.put(UploadOpenHelper.UPLOAD_IS_UPLOAD_KEY, isUpload ? UploadData.UPLOAD_SUCESSS : UploadData.UPLOAD_FAIL);
        int result = db.update(UploadOpenHelper.TABLENAME,values,UploadOpenHelper.UPload_ID_KEY + "=?",new String[]{id + ""});
        return result;
    }

    /**
     *  插入数据
     * */
    public long insertOneUploadData(UploadData oneData){
        ContentValues values=new ContentValues();
        int type = oneData.getType();
        values.put(UploadOpenHelper.UPload_TYPE_KEY , type);
        if (type == UploadData.PATH_TYPE){
            values.put(UploadOpenHelper.UPLOAD_PATH_KEY, oneData.getPath());
        }else{
            values.put(UploadOpenHelper.UPLOAD_PATH_KEY, oneData.getPath());
            values.put(UploadOpenHelper.UPLOAD_DATA_KEY, oneData.getData());
        }
        values.put(UploadOpenHelper.UPLOAD_MSG_CREATE_TIME,oneData.getMsg_create_time());
        values.put(UploadOpenHelper.UPLOAD_IS_UPLOAD_KEY , oneData.isUploadSuccess() ? UploadData.UPLOAD_SUCESSS : UploadData.UPLOAD_FAIL);

        long result = db.insert(UploadOpenHelper.TABLENAME,null,values);

        return result;
    }

    /**
     *  删除上传成功的行数据
     * */
    public void deleteUploadSuccessData(){
        db.delete(UploadOpenHelper.TABLENAME,UploadOpenHelper.UPLOAD_IS_UPLOAD_KEY + "=?",new String[]{UploadData.UPLOAD_SUCESSS + "" });
    }

}