package com.easychat.test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SocketServer {
    public static void main(String[] args) {
        ServerSocket server = null;
        Map<String, Socket> CLENT_MAP = new HashMap<>();

        try {
            server = new ServerSocket(1025);
            System.out.println("服务启动，等待客户端连接");

            while (true) {
                Socket socket = server.accept();
                String ip = socket.getInetAddress().getHostAddress();
                System.out.println("有客户端连接ip：" + ip + "端口" + socket.getPort());
                String clientKey = ip + socket.getPort();
                CLENT_MAP.put(clientKey, socket);

                new Thread(() -> {
                    while (true) {
                        try {
                            InputStream inputStream = socket.getInputStream();
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String readData = bufferedReader.readLine();
                            System.out.println("收到客户端消息-> " + socket.getPort() + " :" + readData);

                            CLENT_MAP.forEach((k, v) -> {
                                try {
                                    OutputStream outputStream = v.getOutputStream();
                                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                                    printWriter.println(socket.getPort() + ":" + readData);
                                    printWriter.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}