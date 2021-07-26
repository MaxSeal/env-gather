package com.briup.smart.env.client;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.entity.Environment;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

public class ClientImpl implements Client{

    private String ip  = "127.0.0.1";
    private int serverPort = 9999;

    @Override
    public void send(Collection<Environment> c) throws Exception {

        try (Socket client = new Socket(ip, serverPort)) {
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(c);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
