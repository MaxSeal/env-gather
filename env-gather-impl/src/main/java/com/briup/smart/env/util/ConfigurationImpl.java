package com.briup.smart.env.util;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.ClientImpl;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.client.GatherImpl;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.DBStoreImpl;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.ServerImpl;

public class ConfigurationImpl implements Configuration {
    @Override
    public Log getLogger() throws Exception {
        return new LogImpl();
    }

    @Override
    public Server getServer() throws Exception {
        return new ServerImpl();
    }

    @Override
    public Client getClient() throws Exception {
        return new ClientImpl();
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return new DBStoreImpl();
    }

    @Override
    public Gather getGather() throws Exception {
        return new GatherImpl();
    }

    @Override
    public Backup getBackup() throws Exception {
        return new BackupImpl();
    }
}
