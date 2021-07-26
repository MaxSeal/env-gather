package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;

import java.util.Collection;

/**
 * DBStore接口是物联网数据中心项目入库模块的规范
 * 该模块负责对Environment集合进行持久化操作
 */
public interface DBStore{
	public void saveDB(Collection<Environment> c)throws Exception;
}
