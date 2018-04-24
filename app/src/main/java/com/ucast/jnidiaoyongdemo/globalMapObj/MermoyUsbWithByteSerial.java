package com.ucast.jnidiaoyongdemo.globalMapObj;

import com.ucast.jnidiaoyongdemo.Model.Config;
import com.ucast.jnidiaoyongdemo.Serial.UsbWithByteSerial;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Administrator on 2016/6/3.
 */
public class MermoyUsbWithByteSerial {
    private static Map<String, UsbWithByteSerial> map = new ConcurrentHashMap<String, UsbWithByteSerial>();

    public static void Add(UsbWithByteSerial channel) {
        map.put(Config.UsbWithByteSerialName, channel);
    }

    public static UsbWithByteSerial GetChannel(String name) {
        return map.get(name);
    }

    public static void Remove(String key) {
        map.remove(key);
    }

    public static Set<Map.Entry<String, UsbWithByteSerial>> ToList() {
        return map.entrySet();
    }
}
