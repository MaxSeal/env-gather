package com.briup.smart.env.client;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.ConfigurationImpl;
import com.briup.smart.env.util.EnvironmentUtils;
import com.briup.smart.env.util.Log;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


public class GatherImpl implements Gather, PropertiesAware, ConfigurationAware {
    // 存有以上两个路径的资源文件
    private static final Properties properties = new Properties();

    // 初始化资源文件
    static {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("env-gather-impl/src/main/resources/backup-config.properties");
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 数据文件的路径
    private String dataSource;
    // 备份文件的路径
    private String backupFile;
    // 备份模块对象
    private Backup backup;
    // 日志模块对象
    private Log logger;

    public static void main(String[] args) throws Exception {
        GatherImpl gather = new GatherImpl();
        gather.gather();
    }

    /**
     * @param properties 记录文件路径的资源文件
     * 对 dataSource 和 backupFile 两个属性做初始化
     */
    @Override
    public void init(Properties properties) throws Exception {
        dataSource = properties.getProperty("dataSource");
        backupFile = properties.getProperty("backupFile");
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        logger = configuration.getLogger();
        backup = configuration.getBackup();
    }

    /**
     * @return 返回一个 Environment 类型的列表，列表中的元素是数据文件中的一条记录
     */
    @Override
    public Collection<Environment> gather() throws Exception {
        init(properties);
        Configuration configuration = new ConfigurationImpl();
        setConfiguration(configuration);

        File dataFile = new File(dataSource);
        BufferedReader reader = new BufferedReader(new FileReader(dataFile));

        String line = null;
        List<String> dataList = new ArrayList<>();

        logger.info("开始读取数据");
        while ((line = reader.readLine()) != null) {
            dataList.add(line);
        }
        logger.info("读取完成");

        logger.info("开始处理数据");
        Collection<Environment> environmentList = processingData(dataList);
        logger.info("数据处理完成");

        logger.info("开始备份");
        backup.store(backupFile, environmentList, true);
        logger.info("备份完成");

        return processingData(dataList);
    }

    /**
     * @param dataList 从数据文件中读取的数据，列表中的每一个元素是数据文件中的一天记录
     * @return 返回一个 Environment 类型的列表，列表中的元素是数据文件中的一条记录
     */
    private Collection<Environment> processingData(List<String> dataList) throws Exception {

        List<Environment> environmentList = new ArrayList<>();

        for (String data : dataList) {
            String[] field = data.split("\\|");

            // 如果长度不是 9 则数据有误
            if (field.length != 9) {
                System.out.println("数据有误");
                continue;
            }

            // 环境种类名称
            String name = null;

            // 环境值
            float environmentData = 0;

            // 发送端id
            String srcId = field[0];

            // 树莓派系统id
            String desId = field[1];

            // 实验箱区域模块id(1-8)
            String devId = field[2];

            // 模块上传感器地址
            String sersorAddress = field[3];

            // 传感器个数
            int count = Integer.parseInt(field[4]);

            // 发送指令标号 3表示接收数据 16表示发送命令
            String cmd = field[5];

            // 状态 默认1表示成功
            int status = Integer.parseInt(field[7]);

            // 采集时间
            Timestamp gather_date = new Timestamp(Long.parseLong(field[8]));

            // 处理环境数据
            switch (sersorAddress) {
                case "16":
                    String name1 = "温度";
                    float temperature = (Integer.parseInt(field[6].substring(0, 4), 16) * (0.00268127F)) - 46.85F;
                    Environment environment1 = new Environment(name1, srcId, desId, devId, sersorAddress, count, cmd, status, temperature, gather_date);
                    environmentList.add(environment1);

                    String name2 = "湿度";
                    float humidity = (Integer.parseInt(field[6].substring(4, 8), 16) * 0.00190735F) - 6;
                    Environment environment2 = EnvironmentUtils.copy(environment1);
                    environment2.setData(humidity);
                    environment2.setName(name2);
                    environmentList.add(environment2);
                    break;

                case "256": {
                    name = "光照强度";
                    environmentData = Integer.parseInt(field[6].substring(0, 4), 16);
                    Environment environment = new Environment(name, srcId, desId, devId, sersorAddress, count, cmd, status, environmentData, gather_date);
                    environmentList.add(environment);
                    break;
                }
                case "1280": {
                    name = "二氧化碳";
                    environmentData = Integer.parseInt(field[6].substring(0, 4), 16);
                    Environment environment = new Environment(name, srcId, desId, devId, sersorAddress, count, cmd, status, environmentData, gather_date);
                    environmentList.add(environment);
                    break;
                }
            }
        }
        return environmentList;
    }


}
