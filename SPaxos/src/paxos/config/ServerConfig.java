package paxos.config;

import com.alibaba.fastjson.JSONObject;

/**
 * server config object
 */
public class ServerConfig {
    private String ip;
    private int port;

    public ServerConfig() {
    }

    public ServerConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
