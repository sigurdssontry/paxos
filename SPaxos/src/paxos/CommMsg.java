package paxos;

import com.alibaba.fastjson.JSONObject;

/**
 * 通信用
 */
public class CommMsg {
    private String msgType;
    private JSONObject msg;

    public CommMsg() {
    }

    public CommMsg(String msgType, JSONObject msg) {
        this.msgType = msgType;
        this.msg = msg;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public JSONObject getMsg() {
        return msg;
    }

    public void setMsg(JSONObject msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
