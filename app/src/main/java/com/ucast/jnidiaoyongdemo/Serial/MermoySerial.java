package com.ucast.jnidiaoyongdemo.Serial;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Administrator on 2016/6/3.
 */
public class MermoySerial {
    private static Map<String, PADPort> map = new ConcurrentHashMap<String, PADPort>();

    public static void Add(PADPort channel) {

        map.put(channel.Name, channel);
    }

    public static PADPort GetChannel(String name) {

        return map.get(name);
    }

    public static void Remove(String key) {
        map.remove(key);
    }

    public static Set<Map.Entry<String, PADPort>> ToList() {
        return map.entrySet();
    }
}
