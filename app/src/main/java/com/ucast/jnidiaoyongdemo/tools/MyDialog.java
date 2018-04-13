package com.ucast.jnidiaoyongdemo.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import com.ucast.jnidiaoyongdemo.Model.AppInfo;
import com.ucast.jnidiaoyongdemo.Model.Common;


/**
 * Created by pj on 2016/11/24.
 */
public class MyDialog {
    private Context context;
    public MyDialog() {
    }

    public MyDialog(Context context) {
        this.context = context;
    }

    public static Dialog showIsUpdate(String msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(ExceptionApplication.getInstance());
        builder.setTitle("更新提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = "@s003,1$";
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


        return alertDialog;
    }

    public static Dialog showPadIsUpdate(final AppInfo info){
        AlertDialog.Builder builder=new AlertDialog.Builder(ExceptionApplication.getInstance());
        builder.setTitle("更新提示");
        builder.setMessage(info.getAppName() + "有新的版本,是否马上更新?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO 安装操作
                MyTools.detele(info.getPackageName());
                MyTools.install( Environment.getExternalStorageDirectory().toString()+"/"+info.getAppName()+".apk");
                Log.e("", "onClick  开始安装 ");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


        return alertDialog;
    }

    public static Dialog showUpdateResult(String msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(ExceptionApplication.getInstance());
        builder.setTitle("更新提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        return alertDialog;
    }

    public static Dialog showPadUpdateSuccess(AppInfo info){
        AlertDialog.Builder builder=new AlertDialog.Builder(ExceptionApplication.getInstance());
        builder.setTitle("更新结果");
        builder.setMessage(info.getAppName()+"更新成功了!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        return alertDialog;
    }
}
