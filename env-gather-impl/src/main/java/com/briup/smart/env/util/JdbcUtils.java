package com.briup.smart.env.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class JdbcUtils {
    private static DataSource dataSource;


    // 初始化
    static {
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("env-gather-impl/src/main/resources/druid.properties");
            properties.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 获取数据库连接对象
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 释放资源
    public static void close(Connection connection, Statement statement, ResultSet resultSet){
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Connection connection, Statement statement){
        close(connection, statement, null);
    }

    public static void close(Connection connection){
        close(connection, null, null);
    }

    // 执行查询以外的 SQL 语句
    public static int executeUpdate(String sql) {
        Connection connection = null;
        Statement statement = null;
        int rows = 0;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            rows = statement.executeUpdate(sql);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            close(connection, statement);
        }
        return rows;
    }

    public static int executeUpdate(String sql, Object[] params){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for(int i = 0; i <= params.length-1; i++){
                preparedStatement.setObject(i+1, params[i]);
            }

            rows = preparedStatement.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement);
        }

        return rows;
    }


    public static <T> T queryForObject (String sql, Function<ResultSet, T> f){
        T result = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            result = f.apply(resultSet);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
        return result;
    }


    public static <T> List<T> queryForList(String sql, Class<T> tClass){
        List<T> result = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            List<ClassField> list = parseClass(tClass);
            T object = null;

            while (resultSet.next()){
                object = tClass.newInstance();

                for (ClassField classField : list){
                    if ("long".equals(classField.fieldType)){
                        long value = resultSet.getLong(classField.fieldName);
                        classField.invokeSetMethod(long.class, object, value);
                    }
                    else if ("String".equals(classField.fieldType)){
                        String value = resultSet.getString(classField.fieldName);
                        classField.invokeSetMethod(String.class, object, value);
                    }
                    else if ("int".equals(classField.fieldType)){
                        int value = resultSet.getInt(classField.fieldName);
                        classField.invokeSetMethod(int.class, object, value);
                    }

                }
                result.add(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }

        return result;
    }


    // 解析 tClass 获取它的所有属性
    private static <T> List<ClassField> parseClass(Class<T> tClass){
        List<ClassField> list = new ArrayList<>();

        // 通过反射获取到 tClass 的所有属性
        Field[] declaredFields = tClass.getDeclaredFields();

        // 遍历 declareFields, 获取属性的类型和名字来创建 classField 对象，并加入到 list 中。
        for (Field field : declaredFields) {
            String fieldType = field.getType().getSimpleName();
            String fieldName = field.getName();
            list.add(new ClassField(fieldType, fieldName));
        }
        return list;
    }


    // 静态内部类 ClassField

    private static class ClassField{
        private String fieldType;
        private String fieldName;

        public ClassField() {}

        public ClassField(String typeOfField, String nameOfField) {
            this.fieldType = typeOfField;
            this.fieldName = nameOfField;
        }

        public String intiCap(String name){
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public void invokeSetMethod(Class<?> tClass, Object object, Object value)  {
            // tClass 是当前属性所在的类。
            // object 调用 setXXX 方法的对象
            // value setXXX 方法的参数
            try {
                Method method = tClass.getDeclaredMethod("set" + intiCap(fieldName));
                method.invoke(object, value);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
