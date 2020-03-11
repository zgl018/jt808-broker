package com.zy.broker.JT808.init;

import com.zy.broker.JT808.TPMSConsts;
import com.zy.broker.JT808.codec.Decoder4LoggingOnly;
import com.zy.broker.JT808.handler.JT808ServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


public class JT808Initializer extends ChannelInitializer<Channel>{

    private final JT808ServerHandler jt808ServerHandler = new JT808ServerHandler();

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                //xx秒没有向客户端发送消息就发生心跳
                .addLast("idleStateHandler",
                        new IdleStateHandler(TPMSConsts.tcp_client_idle_minutes, 0, 0, TimeUnit.MINUTES))
                //每次收到消息，会打印ip和消息字段，以16进制显示
                .addLast(new Decoder4LoggingOnly())
                //以0x7e 和 0x7e,0x7e作为分隔符
                .addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[] { 0x7e }),
                        Unpooled.copiedBuffer(new byte[] { 0x7e, 0x7e })))
                //自定义协议解析handler
                .addLast(jt808ServerHandler);

    }
}
