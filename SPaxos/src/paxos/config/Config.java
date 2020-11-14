package paxos.config;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * the config object
 */
public class Config {

    private String myName;
    private ServerConfig myServer;
    private List<ServerConfig> servers;

    private String dataPath;



    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public ServerConfig getMyServer() {
        return myServer;
    }


    public void setMyServer(ServerConfig myServer) {
        this.myServer = myServer;
    }

    public List<ServerConfig> getServers() {
        return servers;
    }

    public void setServers(List<ServerConfig> servers) {
        this.servers = servers;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
