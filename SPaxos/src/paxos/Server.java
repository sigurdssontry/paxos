package paxos;

import com.alibaba.fastjson.JSONObject;
import paxos.config.Config;
import paxos.config.ServerConfig;
import paxos.util.SocketUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * server object
 */
public class Server {
    private final Config config;
    private final People people;

    public Server(Config config) {
        this.config = config;
        this.people = new People(config.getMyName(), config.getDataPath());
    }


    public void startServer() throws IOException {
        people.loadData();
        startLean();

        ServerConfig myServer = config.getMyServer();
        ServerSocket ss = new ServerSocket(myServer.getPort());
        System.out.println(config.getMyName()+":"+config.getMyServer().getPort()+ " is online");
        //noinspection InfiniteLoopStatement
        while (true) {
            Socket socket = ss.accept();
            new Thread(() -> {
                runJob(socket);
            }).start();
        }
    }

    public void runJob(Socket so) {

        try {
            String recvMsg = SocketUtil.recvMsg(so.getInputStream());
            CommMsg commMsg = JSONObject.parseObject(recvMsg, CommMsg.class);
            MsgType msgType = MsgType.valueOf(commMsg.getMsgType());
            String returnMsg = "";
            JSONObject msgContent = commMsg.getMsg();
            switch (msgType) {
                case PREPARE:
                    Msg msg = people.prepare(msgContent.getInteger("num"));
                    returnMsg = new CommMsg(MsgType.PREPARE.toString(), (JSONObject)JSONObject.toJSON(msg)).toString();
                    break;

                case LEARN:
                    Msg learnMsg = people.learn(msgContent.getInteger("num"), msgContent.getString("name"));
                    returnMsg = new CommMsg(MsgType.LEARN.toString(), (JSONObject)JSONObject.toJSON(learnMsg)).toString();

                    break;

                case ACCEPT:
                    Integer num = msgContent.getInteger("num");
                    String name = msgContent.getString("name");

                    Msg acceptMsg = people.accept(num, name);
                    returnMsg = new CommMsg(MsgType.ACCEPT.toString(), (JSONObject)JSONObject.toJSON(acceptMsg)).toString();

                    break;

                case SERVER:
                    submitPropose(msgContent);
                    break;

                case START_LEARN:
                    Msg learnResultMsg = people.startLearn();
                    returnMsg = new CommMsg(MsgType.START_LEARN.toString(), (JSONObject)JSONObject.toJSON(learnResultMsg)).toString();
                    break;
                default:
                    break;
            }
            SocketUtil.sendMsg(so.getOutputStream(), returnMsg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SocketUtil.close(so);
        }
    }

    private String submitPropose(JSONObject msgContent) {
        int count = 0;
        for (ServerConfig server : config.getServers()) {
            try {
                Socket client = new Socket(server.getIp(), server.getPort());
                CommMsg commMsg = new CommMsg(String.valueOf(MsgType.PREPARE), msgContent);
                System.out.println(config.getMyName()+" submit propose:"+commMsg);
                SocketUtil.sendMsg(client.getOutputStream(), commMsg.toString());
                String returnStr = SocketUtil.recvMsg(client.getInputStream());
                System.out.println(returnStr);
                CommMsg returnMsg = JSONObject.parseObject(returnStr, CommMsg.class);
                JSONObject returnContent = returnMsg.getMsg();
                Boolean ok = returnContent.getBoolean("ok");
                if (ok) {
                    count++;
                }
            } catch (IOException e) {
                System.out.println("the " + server.getIp() + ":" + server.getPort() + " server is offline");
            }
        }
        //超过半数收到提议就可以进入下一阶段
        if (isMoreThanHalf(count)) {
            System.out.println("more than half People give proposer Response");
            submitAccept(msgContent);
        } else {
            System.out.println("less than half People give proposer Response, the propose is down");
        }
        return "";
    }

    private void submitAccept(JSONObject msgContent) {
        int count = 0;
        for (ServerConfig server : config.getServers()) {
            Socket client=null;
            try {
                client = new Socket(server.getIp(), server.getPort());
                CommMsg commMsg = new CommMsg(String.valueOf(MsgType.ACCEPT), msgContent);
                SocketUtil.sendMsg(client.getOutputStream(), commMsg.toString());
                String returnStr = SocketUtil.recvMsg(client.getInputStream());
                System.out.println(returnStr);
                CommMsg returnMsg = JSONObject.parseObject(returnStr, CommMsg.class);
                JSONObject returnContent = returnMsg.getMsg();
                Boolean ok = returnContent.getBoolean("ok");
                if (ok) {
                    count++;
                }
            } catch (IOException e) {
                System.out.println("the " + server.getIp() + ":" + server.getPort() + " server is offline");
            } finally {
                SocketUtil.close(client);
            }
        }

        //超过半数收到提议就可以进入下一阶段
        if (isMoreThanHalf(count)) {
            System.out.println("vote successful");
            submitLearn(msgContent);
        } else {
            System.out.println("vote fail");
        }
    }

    private void submitLearn(JSONObject msgContent) {

        for (ServerConfig server : config.getServers()) {
            Socket client= null;
            try {
                client = new Socket(server.getIp(), server.getPort());
                CommMsg commMsg = new CommMsg(String.valueOf(MsgType.LEARN), msgContent);
                SocketUtil.sendMsg(client.getOutputStream(), commMsg.toString());
                String returnStr = SocketUtil.recvMsg(client.getInputStream());
                System.out.println(returnStr);
                CommMsg returnMsg = JSONObject.parseObject(returnStr, CommMsg.class);
                JSONObject returnContent = returnMsg.getMsg();
            } catch (IOException e) {
                System.out.println("the " + server.getIp() + ":" + server.getPort() + " server is offline");
            } finally {
                SocketUtil.close(client);
            }
        }
        System.out.println("notify all learner");
    }

    private void startLean() {
        for (ServerConfig server : config.getServers()) {
            Socket client= null;
            try {
                if (server.getIp().equals(config.getMyServer().getIp())&& server.getPort() == config.getMyServer().getPort()){
                    continue;
                }
                client = new Socket(server.getIp(), server.getPort());
                CommMsg commMsg = new CommMsg(String.valueOf(MsgType.START_LEARN), new JSONObject());
                SocketUtil.sendMsg(client.getOutputStream(), commMsg.toString());
                String returnStr = SocketUtil.recvMsg(client.getInputStream());
                System.out.println(returnStr);
                CommMsg returnMsg = JSONObject.parseObject(returnStr, CommMsg.class);
                JSONObject returnContent = returnMsg.getMsg();
                if (returnContent.getBoolean("ok")) {
                    Integer leaderNum = returnContent.getInteger("acceptN");
                    String leaderName = returnContent.getString("acceptV");
                    if (this.people.getAcceptedN() == null|| this.people.getAcceptedN()<=leaderNum){
                        this.people.accept(leaderNum, leaderName);
                        break;
                    }
                }
            } catch (IOException e) {
//                System.out.println("the " + server.getIp() + ":" + server.getPort() + " server is offline");
            } finally {
                SocketUtil.close(client);
            }
        }
    }

    private boolean isMoreThanHalf(int count) {
        return count >= (config.getServers().size() / 2 + 1);
    }


}
