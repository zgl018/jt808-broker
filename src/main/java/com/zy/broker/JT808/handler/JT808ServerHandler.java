package com.zy.broker.JT808.handler;

import com.alibaba.fastjson.JSON;
import com.zy.broker.JT808.PackageData;
import com.zy.broker.JT808.TPMSConsts;
import com.zy.broker.JT808.codec.MsgDecoder;
import com.zy.broker.JT808.service.TerminalMsgProcessService;
import com.zy.broker.JT808.util.JT808ProtocolUtils;
import com.zy.broker.utils.CommonSession;
import com.zy.broker.utils.DevSessionManager;
import com.zy.broker.vo.req.LocationInfoUploadMsg;
import com.zy.broker.vo.req.TerminalAuthenticationMsg;
import com.zy.broker.vo.req.TerminalRegisterMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class JT808ServerHandler extends ChannelInboundHandlerAdapter {

    private final MsgDecoder decoder;
    private final DevSessionManager devSessionManager;
    private TerminalMsgProcessService msgProcessService;
    private JT808ProtocolUtils protocolUtils = new JT808ProtocolUtils();

    public JT808ServerHandler() {
        this.devSessionManager = DevSessionManager.getInstance();
        this.decoder = new MsgDecoder();
        this.msgProcessService = new TerminalMsgProcessService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException { // (2)
        try {
            ByteBuf buf = (ByteBuf) msg;
            if (buf.readableBytes() <= 0) {
                // ReferenceCountUtil.safeRelease(msg);
                return;
            }

            byte[] bs = new byte[buf.readableBytes()];
            buf.readBytes(bs);
            // 消息转义
            bs = this.protocolUtils.doEscape4Receive(bs, 0, bs.length);
            // 字节数据转换为针对于808消息结构的实体类
            PackageData pkg = this.decoder.bytes2PackageData(bs);
            // 引用channel,以便回送数据给硬件
            pkg.setChannel(ctx.channel());
            this.processPackageData(pkg);
        } catch (Exception e) {
            log.error("",e);
        } finally {
            release(msg);
        }
    }

    /**
     *
     * 处理业务逻辑
     *
     * @param packageData
     *
     */
    private void processPackageData(PackageData packageData) {
        final PackageData.MsgHeader header = packageData.getMsgHeader();

        // 1. 终端心跳-消息体为空 ==> 平台通用应答
        if (TPMSConsts.msg_id_terminal_heart_beat == header.getMsgId()) {
            log.info(">>>>>[收到终端心跳],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            try {
                this.msgProcessService.processTerminalHeartBeatMsg(packageData);
                log.info("<<<<<[终端心跳回复],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                log.error("<<<<<[终端心跳]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }

        // 5. 终端鉴权 ==> 平台通用应答
        else if (TPMSConsts.msg_id_terminal_authentication == header.getMsgId()) {
            log.info(">>>>>[收到终端鉴权],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            try {
                TerminalAuthenticationMsg authenticationMsg = new TerminalAuthenticationMsg(packageData);
                this.msgProcessService.processAuthMsg(authenticationMsg);
                log.info("<<<<<[终端鉴权回复],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                log.error("<<<<<[终端鉴权]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 6. 终端注册 ==> 终端注册应答
        else if (TPMSConsts.msg_id_terminal_register == header.getMsgId()) {
            log.info(">>>>>[收到终端注册],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            try {
                TerminalRegisterMsg msg = this.decoder.toTerminalRegisterMsg(packageData);
                this.msgProcessService.processRegisterMsg(msg);
                log.info("<<<<<[终端注册回复],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                log.error("<<<<<[终端注册]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 7. 终端注销(终端注销数据消息体为空) ==> 平台通用应答
        else if (TPMSConsts.msg_id_terminal_log_out == header.getMsgId()) {
            log.info(">>>>>[收到终端注销],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            try {
                this.msgProcessService.processTerminalLogoutMsg(packageData);
                log.info("<<<<<[终端注销回复],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                log.error("<<<<<[终端注销]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 3. 位置信息汇报 ==> 平台通用应答
        else if (TPMSConsts.msg_id_terminal_location_info_upload == header.getMsgId()) {
            log.info(">>>>>[收到位置信息],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            try {
                LocationInfoUploadMsg locationInfoUploadMsg = this.decoder.toLocationInfoUploadMsg(packageData);

                System.out.println("位置信息:"+JSON.toJSONString(locationInfoUploadMsg));

                this.msgProcessService.processLocationInfoUploadMsg(locationInfoUploadMsg);
                log.info("<<<<<[位置信息回复],电话={},流水号={}", header.getTerminalPhone(), header.getFlowId());
            } catch (Exception e) {
                log.error("<<<<<[位置信息]处理错误,phone={},flowid={},err={}", header.getTerminalPhone(), header.getFlowId(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        // 其他情况
        else {
            log.error(">>>>>>[未知消息类型],phone={},msgId={},package={}", header.getTerminalPhone(), header.getMsgId(),
                    packageData);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        log.error("发生异常:{}", cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CommonSession session = CommonSession.buildSession(ctx.channel());
        devSessionManager.put(session.getId(), session);
        log.info("JT808终端连接:{}", session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String sessionId = ctx.channel().id().asLongText();
        CommonSession session = devSessionManager.findBySessionId(sessionId);
        this.devSessionManager.removeBySessionId(sessionId);
        log.info("JT808终端断开连接:{}", session);
        ctx.channel().close();
        // ctx.close();
    }

    private void release(Object msg) {
        try {
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}