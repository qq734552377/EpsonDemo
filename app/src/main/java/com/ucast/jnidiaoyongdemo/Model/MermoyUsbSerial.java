package com.ucast.jnidiaoyongdemo.Model;

import com.ucast.jnidiaoyongdemo.Serial.UsbSerial;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Administrator on 2016/6/3.
 */
public class MermoyUsbSerial {
    private static Map<String, UsbSerial> map = new ConcurrentHashMap<String, UsbSerial>();

    public static void Add(UsbSerial channel) {
        map.put(Config.UsbSerialName, channel);
    }

    public static UsbSerial GetChannel(String name) {
        return map.get(name);
    }

    public static void Remove(String key) {
        map.remove(key);
    }

    public static Set<Map.Entry<String, UsbSerial>> ToList() {
        return map.entrySet();
    }
}
