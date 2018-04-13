package com.ucast.jnidiaoyongdemo.Model;

import android.app.Activity;
import android.content.SharedPreferences;

import com.ucast.jnidiaoyongdemo.tools.ExceptionApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/11.
 */
public class SeedNmber {

    public static Object syncLock = new Object();

    private static long current;

    private static long ceiling;

    private static SharedPreferences preference;

    private static SharedPreferences.Editor editor;

    private static boolean Initialized;


    public static void InitSeed() {
        synchronized (syncLock) {
            if (Initialized)
                return;
            current = 0;
            ceiling = current + 100;
            preference = ExceptionApplication.context.getSharedPreferences("Number", Activity.MODE_PRIVATE);
            editor = preference.edit();
            RestoreCeiling();
            Initialized = true;
        }
    }

    public static String GetNextNumber() {
        synchronized (syncLock) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            if (!Initialized)
                return null;
            if (current + 1 >= ceiling)
                UpdateCeiling();
            current++;
            return df.format(new Date()) + String.format("%010d", current);
        }
    }

    private static void UpdateCeiling() {
        ceiling += 100;
        editor.putLong("seed", ceiling);
        editor.commit();
    }

    private static void RestoreCeiling() {
        long readCeiling = preference.getLong("seed", 0);
        ceiling = readCeiling + 100;
        current = readCeiling;
        editor.putLong("seed", ceiling);
        editor.commit();
    }
}
