package com.doubeye.commons.utils.refactor;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author doubeye
 * @version 1.0.0
 * 反射相关的类
 */
public class RefactorUtils {
    /**
     * 日志
     */
    private static Logger logger = LogManager.getLogger(RefactorUtils.class);
    /**
     * 根据指定的类名返回类实例
     * @param className 类名
     * @return 类实例
     */
    public static Object getClassInstanceByName(String className) {
        try {
            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("没有找到需要反射的类：" + className, e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("没有找到默认的构造方法：" + className, e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("非法访问异常：" + className, e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("实例化异常：" + className, e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("调用目标异常：" + className, e);
        }
    }

    /**
     * 根据json的内容为Object对象赋值，兼容MapEntityBean
     * @param instance 要被赋值的对象
     * @param json 值
     * @param skipEmpty 如果JSONObject 对象中的属性为空字符串，则跳过赋值
     */
    public static void fillByJSON(Object instance, JSONObject json, boolean skipEmpty) {
        //获得实例的类信息
        Class<?> instanceClass = instance.getClass();
        //获得类的属性
        Field[] fields = instanceClass.getDeclaredFields();
        String setterMethodName;
        for(Object fieldNameObject : json.keySet()) {
            String fieldName = fieldNameObject.toString();
            for (Field field : fields) {
                //设置可以访问private属性
                field.setAccessible(true);
                if (field.getName().equals(fieldName)) {
                    Object o = json.get(fieldName);
                    //获得set方法的名称
                    setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
                    try {
                        //获得set方法
                        Method method = instanceClass.getDeclaredMethod(setterMethodName, field.getType());
                        try {
                            //设置属性
                            Class<?> fieldType = field.getType();
                            if (fieldType.isPrimitive()) {
                                setPrimitiveValue(instance, field, o);
                            } else {
                                if (o == null) {
                                    method.invoke(instance, new Object[] {null});
                                } else {
                                    if ("java.lang.String".equals(field.getType().getName())) {
                                        if (!(skipEmpty && StringUtils.isEmpty(o.toString()))) {


                                            // null值不做设置
                                            if (!"null".equalsIgnoreCase(o.toString().trim())) {
                                                method.invoke(instance, o.toString());
                                            }
                                        }

                                    } else {
                                        method.invoke(instance, field.getType().cast(o));
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        logger.warn("没有找到对应的setter方法：" + setterMethodName);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 根据json的内容为Object对象赋值，兼容MapEntityBean
     * @param instance 要被赋值的对象
     * @param json 值
     */
    public static void fillByJSON(Object instance, JSONObject json) {
        fillByJSON(instance, json, false);
    }


    /**
     * 设置对象的简单属性
     * @param instance 要被设置属性的对象
     * @param field 属性
     * @param obj 值对象
     */
    private static void setPrimitiveValue(Object instance, Field field, Object obj) throws IllegalArgumentException, IllegalAccessException {
        Class<?> fieldType =field.getType();
        if (fieldType.equals(boolean.class)) {
            if (TRUE.equalsIgnoreCase(obj.toString()) || FALSE.equalsIgnoreCase(obj.toString())) {
                field.setBoolean(instance, Boolean.parseBoolean(obj.toString()));
            } else {
                field.setBoolean(instance, !("0".equals(obj.toString())));
            }
        } else if (fieldType.equals(byte.class)) {
            field.setByte(instance, Byte.parseByte(obj.toString()));
        } else if (fieldType.equals(char.class)) {
            if (StringUtils.isEmpty(obj.toString())) {
                field.setChar(instance, ' ');
            } else {
                field.setChar(instance, obj.toString().charAt(0));
            }
        } else if (fieldType.equals(short.class)) {
            field.setShort(instance, Short.parseShort(obj.toString()));
        } else if (fieldType.equals(int.class)) {
            field.setInt(instance, Integer.parseInt(obj.toString()));
        } else if (fieldType.equals(long.class)) {
            field.setLong(instance, Long.parseLong(obj.toString()));
        } else if (fieldType.equals(float.class)) {
            field.setFloat(instance, Float.parseFloat(obj.toString()));
        } else if (fieldType.equals(double.class)) {
            field.setDouble(instance, Double.parseDouble(obj.toString()));
        }
    }

    /**
     * 获得实例中指定字段的简单值
     * @param instance 实例对象
     * @param field 指定的属性
     * @return 指定属性值
     * @throws IllegalAccessException 非法访问异常
     */
    private static String getPrimitiveValue(Object instance, Field field) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(boolean.class)) {
            return String.valueOf(field.getBoolean(instance));
        } else if (fieldType.equals(byte.class)) {
            return String.valueOf(field.getByte(instance));
        } else if (fieldType.equals(char.class)) {
            return String.valueOf(field.getChar(instance));
        } else if (fieldType.equals(short.class)) {
            return String.valueOf(field.getShort(instance));
        } else if (fieldType.equals(int.class)) {
            return String.valueOf(field.getInt(instance));
        } else if (fieldType.equals(long.class)) {
            return String.valueOf(field.getLong(instance));
        } else if (fieldType.equals(float.class)) {
            return String.valueOf(field.getFloat(instance));
        } else if (fieldType.equals(double.class)) {
            return String.valueOf(field.getDouble(instance));
        }
        return "";
    }

    /**
     * 以键值对的形式返回对象的字符串形式
     * @param object 要打印的对象
     * @return 对象的键值对表示形式
     * @throws IllegalAccessException 非法访问异常
     */
    public static String toString(Object object) throws IllegalAccessException {
        StringBuilder buffer = new StringBuilder();
        Class<?> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            //设置可以访问private属性
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                String value;
                //设置属性
                Class<?> fieldType = field.getType();
                if (fieldType.isPrimitive()) {
                    value = getPrimitiveValue(object, field);
                } else {
                    Object valueObject = field.get(object);
                    if (valueObject == null) {
                        value = null;
                    } else {
                        value = field.get(object).toString();
                    }
                }
                buffer.append(fieldName).append(" -- ").append(value).append("\n");
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    private static final String TRUE = "true";
    private static final String FALSE = "false";
}
