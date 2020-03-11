package com.zy.broker.utils;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class DevSessionManager {

    // 参考JT808，平台使用
    private static volatile DevSessionManager instance = null;
    // netty生成的sessionID和Session的对应关系
    private static Map<String, CommonSession> sessionIdMap = new ConcurrentHashMap<>();
    // 终端手机号和netty生成的sessionID的对应关 系
    private static Map<String, String> devIdMap = new ConcurrentHashMap<>();

    public static DevSessionManager getInstance() {
        if (instance == null) {
            synchronized (DevSessionManager.class) {
                if (instance == null) {
                    instance = new DevSessionManager();
                }
            }
        }
        return instance;
    }




    public static boolean containsDev(String devId) {
        return devIdMap.containsKey(devId);
    }

    public boolean containsKey(String sessionId) {
        return sessionIdMap.containsKey(sessionId);
    }

    public boolean containsSession(CommonSession session) {
        return sessionIdMap.containsValue(session);
    }

    public static CommonSession findBySessionId(String id) {
        return sessionIdMap.get(id);
    }

    public static CommonSession findByDevId(String devId) {
        String sessionId = devIdMap.get(devId);
        if (sessionId == null)
            return null;
        return findBySessionId(sessionId);
    }

    public synchronized CommonSession put(String key, CommonSession value) {
        if (value.getDevId() != null && !"".equals(value.getDevId().trim())) {
            this.devIdMap.put(value.getDevId(), value.getId());
        }
        return sessionIdMap.put(key, value);
    }

    public synchronized CommonSession removeBySessionId(String sessionId) {
        if (sessionId == null)
            return null;
        CommonSession session = sessionIdMap.remove(sessionId);
        if (session == null)
            return null;
        if (session.getDevId() != null)
            this.devIdMap.remove(session.getDevId());
        return session;
    }


    public Set<String> keySet() {
        return sessionIdMap.keySet();
    }

    public void forEach(BiConsumer<? super String, ? super CommonSession> action) {
        sessionIdMap.forEach(action);
    }

    public Set<Map.Entry<String, CommonSession>> entrySet() {
        return sessionIdMap.entrySet();
    }

    public List<CommonSession> toList() {
        return this.sessionIdMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

}
