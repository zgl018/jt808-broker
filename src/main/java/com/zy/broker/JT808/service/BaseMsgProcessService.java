package com.zy.broker.JT808.service;


import com.zy.broker.utils.CommonSession;
import com.zy.broker.utils.DevSessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseMsgProcessService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected DevSessionManager devSessionManager;

	public BaseMsgProcessService() {
		this.devSessionManager = DevSessionManager.getInstance();
	}

	protected ByteBuf getByteBuf(byte[] arr) {
		ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(arr.length);
		byteBuf.writeBytes(arr);
		return byteBuf;
	}

	public void send2Client(Channel channel, byte[] arr) throws InterruptedException {
		ChannelFuture future = channel.writeAndFlush(Unpooled.copiedBuffer(arr)).sync();
		if (!future.isSuccess()) {
			log.error("发送数据出错:{}", future.cause());
		}
	}

	protected int getFlowId(Channel channel, int defaultValue) {
		CommonSession session = this.devSessionManager.findBySessionId(CommonSession.buildId(channel));
		if (session == null) {
			return defaultValue;
		}

		return session.currentFlowId();
	}

	protected int getFlowId(Channel channel) {
		return this.getFlowId(channel, 0);
	}

}
