package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.EnvironmentUtils;
import com.briup.smart.env.util.JdbcUtils;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public class DBStoreImpl implements DBStore{


    @Override
    public void saveDB(Collection<Environment> c) throws Exception {
        // 遍历 c
        c.forEach(environment -> {

            // 获取每条数据对应的天数
            Timestamp gather_date = environment.getGather_date();
            String day = gather_date.toString().substring(8, 10);

            // 编写 SQL 语句
            String sql = "insert into e_detail_" + Integer.parseInt(day) + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                // 获取 environment 对象的所有属性值，并将其转为数组
                List<Object> fieldList = EnvironmentUtils.gerAllField(environment);
                Object[] fieldArray = fieldList.toArray();

                // 执行 SQL 语句
                JdbcUtils.executeUpdate(sql, fieldArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
