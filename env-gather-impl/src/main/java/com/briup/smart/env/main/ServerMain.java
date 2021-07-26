package com.briup.smart.env.main;

import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.ServerImpl;

//服务器入口类
public class ServerMain {
	
	public static void main(String[] args) {
		Server server = new ServerImpl();
		try {
			server.receiver();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
