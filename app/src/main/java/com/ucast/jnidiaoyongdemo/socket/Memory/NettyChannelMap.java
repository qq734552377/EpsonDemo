package com.ucast.jnidiaoyongdemo.socket.Memory;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * Created by Administrator on 2016/2/3.
 */
public class NettyChannelMap {

    public static Map<String, Channel> map = new ConcurrentHashMap<String, Channel>();

    public static void Add(Channel channel) {

        map.put(channel.id().toString(), channel);
    }

    public static Channel GetChannel(String clientId) {

        return map.get(clientId);
    }

    public static void Remove(String key) {

        map.remove(key);
    }
    public static Set<Map.Entry<String, Channel>> ToList()
    {
        return map.entrySet();
    }
}
