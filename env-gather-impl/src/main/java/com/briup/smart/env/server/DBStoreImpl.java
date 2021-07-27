package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.EnvironmentUtils;
import com.briup.smart.env.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class DBStoreImpl implements DBStore{

    @Override
    public void saveDB(Collection<Environment> c) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 获取数据库连接对象
            connection = JdbcUtils.getConnection();

            // 设置不自动提交
            connection.setAutoCommit(false);

            // SQL 语句
            String sql = null;

            // i 用于计数，每 50 条数据提交一次
            int i = 0;

            // 表示上一个 dayOfMonth，因为第一次获取的 dayOfMonth 没有上一个，所以初始值为 -1
            int lastDayOfMonth = -1;

            // 创建 Calender 对象，用于获取时间戳里的日期
            Calendar calendar = Calendar.getInstance();


            for (Environment environment: c) {

                Timestamp gather_date = environment.getGather_date();
                calendar.setTime(gather_date);

                // 获取日期天数
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);


                // 第一次
                if (lastDayOfMonth == -1){

                    // 拼接 SQL 语句
                    sql = "insert into e_detail_" + dayOfMonth + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    preparedStatement = connection.prepareStatement(sql);

                } else {

                    // 上一条数据的天数和本条数据的天数不同
                    if (dayOfMonth != lastDayOfMonth){

                        // 提交上一天的SQL（未满50条的数据），并关闭 preparedStatement
                        preparedStatement.executeBatch();
                        preparedStatement.close();
                        i = 0;

                        // 重新拼接 SQL 语句，并在创建一个 preparedStatement 预处理该 SQL 语句
                        sql = "insert into e_detail_" + dayOfMonth + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        preparedStatement = connection.prepareStatement(sql);
                    }
                }

                // 设置参数
                preparedStatement.setString(1,environment.getName());
                preparedStatement.setString(2,environment.getSrcId());
                preparedStatement.setString(3,environment.getDesId());
                preparedStatement.setString(4,environment.getDevId());
                preparedStatement.setString(5,environment.getSersorAddress());
                preparedStatement.setInt(6,environment.getCount());
                preparedStatement.setString(7,environment.getCmd());
                preparedStatement.setInt(8,environment.getStatus());
                preparedStatement.setFloat(9,environment.getData());
                preparedStatement.setTimestamp(10,environment.getGather_date());

                // 添加到批处理
                preparedStatement.addBatch();
                i++;

                // 每 50 条数据提交一次
                if(i==50) {
                    preparedStatement.executeBatch();
                    i = 0;
                }

                lastDayOfMonth = dayOfMonth;
            }

            //最后在提交一次，保证没有遗漏数据
            preparedStatement.executeBatch();

            // 手动提交
            connection.commit();

        } finally {
            JdbcUtils.close(connection, preparedStatement);
        }
    }

}
