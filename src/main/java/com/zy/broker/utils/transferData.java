package com.zy.broker.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class transferData {
    public static int connectNum;
    public static String dataTransfer(String data) {
        JSONObject result = new JSONObject();
        JSONObject jsonData = JSON.parseObject(data);
        if (jsonData.get("devId") != null) {
            result.put("devId", jsonData.get("devId"));
            jsonData.remove("devId");
            result.put("type", "measurement");
            result.put("data", jsonData);
        }
        return result.toString();
    }
    public static String dataTransfer(JSONObject data) {
        JSONObject result = new JSONObject();
        if (data.get("devId") != null) {
            result.put("devId", data.get("devId"));
            data.remove("devId");
            result.put("data", data);
        }
        return result.toString();
    }
}
