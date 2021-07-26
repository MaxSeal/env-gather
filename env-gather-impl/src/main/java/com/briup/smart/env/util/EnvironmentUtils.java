package com.briup.smart.env.util;

import com.briup.smart.env.entity.Environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentUtils {

    /**
     * @param oldObject 传入的对象
     * @param <T> 泛型
     * @return 返回一个和参数属性值一模一样的对象。
     * @throws Exception
     */
    public static <T> T copy(T oldObject) throws Exception{
        Class<T> c = (Class<T>) oldObject.getClass();

        // 获取无参构造器
        Constructor<T> constructor = c.getConstructor();

        // 调用无参构造器创建新的对象
        T newObject = constructor.newInstance();

        // 获取类中的所有属性
        Field[] fields = c.getDeclaredFields();

        for(Field field : fields) {
            // 获取属性名
            String name = field.getName();

            // 如果不是 final 属性就执行以下操作
            if (!Modifier.isFinal(field.getModifiers())) {

                // 拼接 getter 和 setter 方法名
                String firstLetter = name.substring(0, 1).toUpperCase();
                String getMethodName = "get" + firstLetter + name.substring(1);
                String setMethodName = "set" + firstLetter + name.substring(1);

                // 获取 getter 和 setter 方法
                Method getMethod = c.getMethod(getMethodName);
                Method setMethod = c.getMethod(setMethodName, field.getType());

                // oldObject 对象调用相应的属性的 get 方法获得对应的属性值
                Object value = getMethod.invoke(oldObject);

                // newObject 对象调用相应的属性的 set 方法获
                setMethod.invoke(newObject, value);
            }

        }
        return newObject;
    }


    /**
     * @param environment 传入 Environment 对象
     * @return 返回一个 Object 数值, 该数组的元素是传入参数的所有非 final 的属性值
     * @throws Exception
     */
    public static List<Object> gerAllField (Environment environment) throws Exception {
        List<Object> fieldValueList = new ArrayList<>();
        Class<? extends Environment> aClass = environment.getClass();

        // 获取 Environment 的所有属性
        Field[] fields = aClass.getDeclaredFields();

        for (Field field : fields) {

            // 获取属性名
            String name = field.getName();

            // 如果不是 final 属性就执行以下操作
            if (!Modifier.isFinal(field.getModifiers())) {

                // 拼接 getter 方法名
                String firstLetter = name.substring(0, 1).toUpperCase();
                String getMethodName = "get" + firstLetter + name.substring(1);

                // 获取 getter 方法
                Method getMethod = aClass.getMethod(getMethodName);

                // 调用相应的属性的 get 方法获得对应的属性值
                Object value = getMethod.invoke(environment);

                // 将属性值放入列表
                fieldValueList.add(value);
            }
        }
        return fieldValueList;
    }
}
