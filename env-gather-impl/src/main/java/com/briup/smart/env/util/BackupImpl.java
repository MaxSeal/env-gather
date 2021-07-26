package com.briup.smart.env.util;

import java.io.*;

public class BackupImpl implements Backup {

    @Override
    public Object load(String fileName, boolean del) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();

        fileInputStream.close();
        objectInputStream.close();

        if (del){
            File file = new File(fileName);
            if (!file.isDirectory()) {
                boolean result = file.delete();
                System.out.println("是否删除文件" + result);
            }
        }

        return object;
    }

    @Override
    public void store(String fileName, Object obj, boolean append) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName, append);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        fileOutputStream.close();
        objectOutputStream.close();
    }
}
