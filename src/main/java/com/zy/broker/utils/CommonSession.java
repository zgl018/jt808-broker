package com.zy.broker.utils;

import io.netty.channel.Channel;

import java.net.SocketAddress;

public class CommonSession {
    // session id
    private String id;
    // dev id
    private String devId;
    // channel
    private Channel channel = null;
    // private ChannelGroup channelGroup = new
    // DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 客户端上次的连接时间，该值改变的情况:
    // 1. terminal --> server 心跳包
    // 2. terminal --> server 数据包
    private long lastCommunicateTimeStamp = 0l;

    public CommonSession() {
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static String buildId(Channel channel) {
        return channel.id().asLongText();
    }

    public static CommonSession buildSession(Channel channel) {
        return buildSession(channel, null);
    }

    public static CommonSession buildSession(Channel channel, String devId) {
        CommonSession session = new CommonSession();
        session.setChannel(channel);
        session.setId(buildId(channel));
        session.setDevId(devId);
        session.setLastCommunicateTimeStamp(System.currentTimeMillis());
        return session;
    }

    public long getLastCommunicateTimeStamp() {
        return lastCommunicateTimeStamp;
    }

    public void setLastCommunicateTimeStamp(long lastCommunicateTimeStamp) {
        this.lastCommunicateTimeStamp = lastCommunicateTimeStamp;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommonSession other = (CommonSession) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Session [id=" + id + ", devId=" + devId + ", channel=" + channel + "]";
    }

}
