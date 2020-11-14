package test;

import paxos.Server;
import paxos.config.Config;
import paxos.config.ServerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerTest1 {

    public static void main(String[] args) throws IOException {
        Config config = new Config();
        config.setMyName("M2");
        config.setMyServer(new ServerConfig("127.0.0.1", 3001));
        config.setServers(getServers());
        config.setDataPath("D:\\ideaProjects\\SPaxos\\data");
        Server server = new Server(config);
        server.startServer();
    }

    static List<ServerConfig> getServers() {
        List<ServerConfig> list = new ArrayList<>();
        list.add(new ServerConfig("127.0.0.1", 3000));
        list.add(new ServerConfig("127.0.0.1", 3001));
        list.add(new ServerConfig("127.0.0.1", 3002));
        return list;
    }

}
