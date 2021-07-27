package com.briup.smart.env.main;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.ClientImpl;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.client.GatherImpl;
import com.briup.smart.env.entity.Environment;
import java.util.Collection;

//客户端入口类
public class ClientMain {
	public static void main(String[] args) {

		Gather gather = new GatherImpl();
		Client client = new ClientImpl();

		try {
			Collection<Environment> c = gather.gather();
			client.send(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
