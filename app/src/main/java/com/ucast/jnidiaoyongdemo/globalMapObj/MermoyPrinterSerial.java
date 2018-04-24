package com.ucast.jnidiaoyongdemo.globalMapObj;

import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Serial.OpenPrint;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Administrator on 2016/6/3.
 */
public class MermoyPrinterSerial {
    private static Map<String, OpenPrint> map = new ConcurrentHashMap<String, OpenPrint>();

    public static void Add(OpenPrint channel) {
        map.put(Config.PrinterSerialName, channel);
    }

    public static OpenPrint GetChannel(String name) {
        return map.get(name);
    }

    public static void Remove(String key) {
        map.remove(key);
    }

    public static Set<Map.Entry<String, OpenPrint>> ToList() {
        return map.entrySet();
    }
}
