package com.ucast.jnidiaoyongdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ucast.jnidiaoyongdemo.Model.UploadData;

public class UploadOpenHelper extends SQLiteOpenHelper {
	public static String TABLENAME="upload";

	public static String DBNAME = "ucastupload";

	public static String UPload_ID_KEY = "_id";
	public static String UPload_TYPE_KEY = "type";
	public static String UPLOAD_PATH_KEY = "path";
	public static String UPLOAD_DATA_KEY = "data";
	public static String UPLOAD_IS_UPLOAD_KEY = "is_upload";

	private SQLiteDatabase db;
	public UploadOpenHelper(Context context) {
		super(context, DBNAME, null, 1);
		db=getWritableDatabase();
	}

	/**创建数据库*/
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table " + TABLENAME + " ("
				+ UPload_ID_KEY +" INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ UPload_TYPE_KEY + " INTEGER,"
				+ UPLOAD_PATH_KEY +" VARCHAR(100),"
				+ UPLOAD_DATA_KEY +" TEXT,"
				+ UPLOAD_IS_UPLOAD_KEY +" INTEGER)");

		//准备数据
//		for (int i = 1; i < 11; i++) {
//			ContentValues values=new ContentValues();
//
//			values.put(UPload_TYPE_KEY, i % 2 == UploadData.PATH_TYPE ? UploadData.PATH_TYPE : UploadData.DATA_TYPE);
//			if (i % 2 == UploadData.PATH_TYPE){
//				values.put(UPLOAD_PATH_KEY, "无名氏");
//			}else{
//				values.put(UPLOAD_DATA_KEY,"这是测试数据" + i);
//			}
//			values.put(UPLOAD_IS_UPLOAD_KEY,(i+1) % 2 == UploadData.UPLOAD_FAIL ? UploadData.UPLOAD_FAIL : UploadData.UPLOAD_SUCESSS);
//
//			db.insert(TABLENAME, null, values);
//		}

	}


	/**更新数据库*/
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
  
	
}
