package com.ucast.jnidiaoyongdemo.tools;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/6/12.
 */
public class ExceptionApplication extends Application {

    public static Context context;



    public void onCreate() {
        super.onCreate();


        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        context=this;
    }
    public static Context getInstance(){
        return context;
    }

}
