package test;

import paxos.Server;
import paxos.config.Config;
import paxos.config.ServerConfig;

import java.util.ArrayList;
import java.util.List;

public class ServerAllTest {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            throw new Exception("args length invalid, there must be 4");
        }
        String dataPath = args[0];
        String myName = args[1];
        String myServer = args[2];
        String allServers = args[3];

        Config config = new Config();
        config.setMyName(myName);
        String[] myServerArr = myServer.split(":");
        config.setMyServer(new ServerConfig(myServerArr[0], Integer.parseInt(myServerArr[1])));
        config.setServers(getServers(allServers));
        config.setDataPath(dataPath);

        Server server = new Server(config);
        server.startServer();
    }

    static List<ServerConfig> getServers(String allServers) {
        String[] array = allServers.split(",");
        List<ServerConfig> list = new ArrayList<>();
        for (String server : array) {
            String[] temp = server.split(":");
            list.add(new ServerConfig(temp[0], Integer.parseInt(temp[1])));
        }

        return list;
    }

}
