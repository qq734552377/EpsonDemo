package com.ucast.jnidiaoyongdemo.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import com.ucast.jnidiaoyongdemo.Model.AppInfo;
import com.ucast.jnidiaoyongdemo.Model.BitmapWithOtherMsg;
import com.ucast.jnidiaoyongdemo.Model.Common;
import com.ucast.jnidiaoyongdemo.Model.ReadPictureManage;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonParseDemo;
import com.ucast.jnidiaoyongdemo.bmpTools.EpsonPicture;


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

    public static Dialog showDialogWithMsg(String msg, final int money){
        AlertDialog.Builder builder=new AlertDialog.Builder(ExceptionApplication.getInstance());
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String printMsg = "您消费"+money+"元，免费获得"+(money/100)+".0小时停车券。欢迎下次光临\n" +
                        "\n" ;
                Bitmap b = EpsonPicture.getBitMapByStringReturnBigBitmap(printMsg);
                String path = Environment.getExternalStorageDirectory().getPath() + "/ums.bmp";
                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(b,false));
                ReadPictureManage.GetInstance().GetReadPicture(0).Add(new BitmapWithOtherMsg(BitmapFactory.decodeFile(path),true));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);


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
