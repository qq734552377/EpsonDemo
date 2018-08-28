package com.ucast.jnidiaoyongdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ucast.jnidiaoyongdemo.advAct.AdvActivity;
import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

/**
 * Created by pj on 2016/11/21.
 */
public class StartBrocast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent ootStartIntent = new Intent(context, UpdateService.class);
        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(ootStartIntent);
        Intent start_adv_intent = new Intent(ExceptionApplication.getInstance(), AdvActivity.class);
        start_adv_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败
        context.startActivity(start_adv_intent);
    }
}
