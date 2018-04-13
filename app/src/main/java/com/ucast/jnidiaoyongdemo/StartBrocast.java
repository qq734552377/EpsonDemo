package com.ucast.jnidiaoyongdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pj on 2016/11/21.
 */
public class StartBrocast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent ootStartIntent = new Intent(context, UpdateService.class);
        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(ootStartIntent);
    }
}
