package paxos;

import com.alibaba.fastjson.JSONObject;
import paxos.config.Config;
import paxos.config.ServerConfig;
import paxos.util.SocketUtil;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * 服务端
 */
public class People {

    /**
     * self name of M1-9
     */
    private final String myName;
    private final String dataPath;

    /**
     * 当前接收到的提案号
     */
    private Integer maxN;
    /**
     * 当前已接受提案号
     */
    private Integer acceptedN;
    /**
     * 当前已接受提案值
     */
    private String acceptedV;

    private Integer learnN;
    private String learnV;

    private boolean isLeader;

    public People(String myName, String dataPath) {
        this.myName = myName;
        this.dataPath = dataPath;
    }


    /**
     * 准备阶段
     *
     * @param acceptN 提案编号
     */
    public synchronized Msg prepare(Integer acceptN) {
        System.out.println(myName + " prepare number:" + acceptN);
        if (this.maxN == null) {
            //之前没有接受过提案，可以接收提案
            return new Msg(true);
        }

        if (maxN >= acceptN) {
            //之前接收过提案，并且比收到的提案值大，则不可以再接收提案
            return new Msg(false, null, null);
        } else {
            this.maxN = acceptN;
            if (this.acceptedN == null) {
                return new Msg(true);
            } else {
                //如果之前同意过提案，返回最后同意的提案编号和提案值
                return new Msg(true, acceptedN, acceptedV);
            }
        }

    }

    /**
     * 接受阶段
     */
    public synchronized Msg accept(Integer acceptN, String acceptV) {
        //首先当前申请的提案号acceptN不能小于maxN
        if (null == acceptedN || acceptedN <= acceptN) {
            this.acceptedN = acceptN;
            this.acceptedV = acceptV;
            persistent();
            return new Msg(true, acceptN, acceptV);
        }
        return new Msg(false, this.acceptedN, this.acceptedV);
    }


    public synchronized Msg learn(Integer learnN, String learnV) {
        if ((acceptedN == null || acceptedN <= learnN)) {
            this.acceptedN = learnN;
            this.acceptedV = learnV;
            persistent();
        }
        if (this.learnN == null || this.learnN <= learnN) {
            this.learnN = learnN;
            this.learnV = learnV;
            if (myName.equals(learnV)) {
                this.isLeader = true;
                System.out.println(myName+": i'm the new leader!");
            }else {
                this.isLeader = false;
            }
            System.out.println("learn num:"+learnN+" value:"+learnV);
        }

        return new Msg(true, learnN, learnV);
    }

    public synchronized Msg startLearn() {
        if (isLeader) {
            return new Msg(true, acceptedN, acceptedV);
        }
        return new Msg(false);
    }

    private void persistent() {
        String fileName = myName+".json";
        JSONObject data = new JSONObject();
        data.put("num", this.acceptedN);
        data.put("name", this.acceptedV);
        File file = new File(dataPath, fileName);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(data.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SocketUtil.close(bw);
        }
    }

    /**
     * load from data file
     */
    public void loadData() {
        String fileName = myName+".json";

        File file = new File(dataPath, fileName);
        if (!file.exists()) {
            return;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder("");
            String line;
            while ((line = br.readLine()) != null) {
                if ("".equals(line.trim())){
                    continue;
                }
                sb.append(line);
            }
            if (!"".equals(sb.toString())) {
                JSONObject data = JSONObject.parseObject(sb.toString());
                this.acceptedN = data.getInteger("num");
                this.acceptedV = data.getString("name");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SocketUtil.close(br);
        }
    }


    public Integer getAcceptedN() {
        return acceptedN;
    }

    public String getAcceptedV() {
        return acceptedV;
    }
}
