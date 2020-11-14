package paxos;

import com.alibaba.fastjson.JSONObject;

public class Msg {
    private Boolean ok;
    private Integer acceptN;
    private String acceptV;

    public Msg() {

    }

    public Msg(Boolean ok) {
        this.ok = ok;
    }

    public Msg(Boolean ok, Integer acceptN, String acceptV) {
        this.ok = ok;
        this.acceptN = acceptN;
        this.acceptV = acceptV;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Integer getAcceptN() {
        return acceptN;
    }

    public void setAcceptN(Integer acceptN) {
        this.acceptN = acceptN;
    }

    public String getAcceptV() {
        return acceptV;
    }

    public void setAcceptV(String acceptV) {
        this.acceptV = acceptV;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
