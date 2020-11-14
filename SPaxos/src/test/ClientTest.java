package test;

import com.alibaba.fastjson.JSONObject;
import paxos.CommMsg;
import paxos.MsgType;
import paxos.util.SocketUtil;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTest {


    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService service = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new SynchronousQueue<>());

        final int totalPeoples = 3;
        final int totalProposers = 10;
        String dataPath = "D:\\ideaProjects\\SPaxos\\data";

        AtomicInteger num = new AtomicInteger(loadCacheNumber(dataPath));

        final CountDownLatch countDownLatch = new CountDownLatch(totalProposers);
        for (int i = 1; i <= totalProposers; i++) {
            service.execute(() -> {
                long startTime = System.currentTimeMillis();

                try {
                    CommMsg commMsg = new CommMsg();
                    commMsg.setMsgType(MsgType.SERVER.toString());
                    JSONObject msg = new JSONObject();
                    msg.put("num", num.incrementAndGet());
                    msg.put("name", getRandomPeople(totalPeoples));

                    commMsg.setMsg(msg);

                    Socket socket = new Socket("127.0.0.1", 3000);
                    SocketUtil.sendMsg(socket.getOutputStream(), commMsg.toString());
                    SocketUtil.recvMsg(socket.getInputStream());
                    SocketUtil.close(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("total cast time:"+(endTime-startTime));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        writeCacheNumber(dataPath, num.get());
        service.shutdown();



    }

    private static String getRandomPeople(int total) {
        int i= new Random().nextInt(total)+1;

        return "M"+i;
    }

    private static Integer loadCacheNumber(String dataPath) {
        File file = new File(dataPath, "cachenum");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            if (line == null||"".equals(line.trim())){
                return 1;
            } else {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SocketUtil.close(br);
        }
        return 1;
    }

    private static void writeCacheNumber(String dataPath, Integer cachenum) {
        File file = new File(dataPath, "cachenum");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(cachenum+"");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SocketUtil.close(bw);
        }
    }
}
