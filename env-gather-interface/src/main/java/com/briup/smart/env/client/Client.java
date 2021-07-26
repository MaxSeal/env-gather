package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;

import java.util.Collection;

/**
 * Client接口是物联网数据中心项目网络模块(客户端)的规范
 * Client的作用就是与服务器进行通信传递信息
 */
public interface Client{
	public void send(Collection<Environment> c)throws Exception;
}
