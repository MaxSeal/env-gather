package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerImpl implements Server {

    private int receiverPort = 9999;
    private int shutdownPort = 8888;

    private volatile boolean flag = true;

    @SuppressWarnings("unchecked")
    public void startReceiver()  {
        ServerSocket receiveSocket = null;
        Socket clientSocket = null;
        ObjectInputStream inputStream = null;
        DBStore dbStore = new DBStoreImpl();

        try {
            while (flag) {
                receiveSocket = new ServerSocket(receiverPort);
                clientSocket = receiveSocket.accept();
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                List<Environment> environmentList = (List<Environment>) inputStream.readObject();
                dbStore.saveDB(environmentList);
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if (inputStream != null){
                    inputStream.close();
                }
                if (receiveSocket != null){
                    receiveSocket.close();
                }
                if (clientSocket != null){
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭服务
    public void startShutdown() {
        Runnable run =() -> {
            ServerSocket shutdownServer = null;
            try {
                shutdownServer = new ServerSocket(shutdownPort);
                shutdownServer.accept();
                ServerImpl.this.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (shutdownServer != null){
                    try {
                        shutdownServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(run).start();
    }

    @Override
    public void receiver() throws Exception {
        startReceiver();
        startShutdown();

    }

    @Override
    public void shutdown() throws Exception {
        flag = false;
    }
}
