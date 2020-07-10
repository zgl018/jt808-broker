package com.zy.broker.JT808.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ChatHeartbeatHandler extends ChannelInboundHandlerAdapter {
  private Logger logger = LogManager.getLogger();

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      logger.info("设备由于未上传数据时间过久被强制断开连接");
      ctx.close();
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}
