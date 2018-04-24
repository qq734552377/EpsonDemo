package com.ucast.jnidiaoyongdemo.globalMapObj;

import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Serial.KeyBoardSerial;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Administrator on 2016/6/3.
 */
public class MermoyKeyboardSerial {
    private static Map<String, KeyBoardSerial> map = new ConcurrentHashMap<String, KeyBoardSerial>();

    public static void Add(KeyBoardSerial channel) {
        map.put(Config.KeyboardSerialName, channel);
    }

    public static KeyBoardSerial GetChannel(String name) {
        return map.get(name);
    }

    public static void Remove(String key) {
        map.remove(key);
    }

    public static Set<Map.Entry<String, KeyBoardSerial>> ToList() {
        return map.entrySet();
    }
}
