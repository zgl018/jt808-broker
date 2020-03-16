package com.zy.broker.JT808.service;


import com.alibaba.fastjson.JSON;
import com.zy.broker.JT808.PackageData;
import com.zy.broker.JT808.codec.MsgEncoder;
import com.zy.broker.utils.CommonSession;
import com.zy.broker.utils.DevSessionManager;
import com.zy.broker.vo.req.LocationInfoUploadMsg;
import com.zy.broker.vo.req.TerminalAuthenticationMsg;
import com.zy.broker.vo.req.TerminalRegisterMsg;
import com.zy.broker.vo.resp.ServerCommonRespMsgBody;
import com.zy.broker.vo.resp.TerminalRegisterMsgRespBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TerminalMsgProcessService extends BaseMsgProcessService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private MsgEncoder msgEncoder;
    private final DevSessionManager devSessionManager;

    public TerminalMsgProcessService() {
        this.msgEncoder = new MsgEncoder();
        this.devSessionManager = DevSessionManager.getInstance();
    }

    public void processRegisterMsg(TerminalRegisterMsg msg) throws Exception {
        log.debug("终端注册:{}", JSON.toJSONString(msg,true));

        final String sessionId = CommonSession.buildId(msg.getChannel());
        CommonSession session = devSessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = CommonSession.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        // 用于鉴权注册的，暂未实现
        // session.setAuthenticated(true);
        // jt808中将手机号作为devId
        session.setDevId(msg.getMsgHeader().getTerminalPhone());
        devSessionManager.put(session.getId(), session);

        TerminalRegisterMsgRespBody respMsgBody = new TerminalRegisterMsgRespBody();
        respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.success);
        respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        // TODO 鉴权码暂时写死
        respMsgBody.setReplyToken("123");
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4TerminalRegisterResp(msg, respMsgBody, flowId);

        super.send2Client(msg.getChannel(), bs);
    }

    public void processAuthMsg(TerminalAuthenticationMsg msg) throws Exception {
        // TODO 暂时每次鉴权都成功

        System.out.println("终端鉴权:"+JSON.toJSONString(msg));

        final String sessionId = CommonSession.buildId(msg.getChannel());
        CommonSession session = devSessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = CommonSession.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        // 用于鉴权注册的，暂未实现
        // session.setAuthenticated(true);
        // jt808中将手机号作为devId
        session.setDevId(msg.getMsgHeader().getTerminalPhone());
        devSessionManager.put(session.getId(), session);

        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody();
        respMsgBody.setReplyCode(ServerCommonRespMsgBody.success);
        respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        respMsgBody.setReplyId(msg.getMsgHeader().getMsgId());
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
        super.send2Client(msg.getChannel(), bs);
    }

    public void processTerminalHeartBeatMsg(PackageData req) throws Exception {
        System.out.println("心跳信息:"+JSON.toJSONString(req));
        final PackageData.MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        super.send2Client(req.getChannel(), bs);
    }

    public void processTerminalLogoutMsg(PackageData req) throws Exception {
        System.out.println("终端注销:"+JSON.toJSONString(req));
        final PackageData.MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        super.send2Client(req.getChannel(), bs);
    }

    public void processLocationInfoUploadMsg(LocationInfoUploadMsg req) throws Exception {
        log.debug("位置 信息:{}", JSON.toJSONString(req, true));
        final PackageData.MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),
                ServerCommonRespMsgBody.success);
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        super.send2Client(req.getChannel(), bs);
    }
}
