package paxos.util;

import java.io.*;

public class SocketUtil {

    public static String recvMsg(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);
        int len = dis.readInt();
        byte[] datas = new byte[len];
        dis.read(datas);
        String data = new String(datas);
        return data;
    }

    public static void sendMsg(OutputStream outputStream, String msg) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        byte[] datas = msg.getBytes();
        int len = datas.length;
        dos.writeInt(len);
        dos.write(datas);
    }

    public static void close(Closeable closeable) {
        if (null == closeable) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
