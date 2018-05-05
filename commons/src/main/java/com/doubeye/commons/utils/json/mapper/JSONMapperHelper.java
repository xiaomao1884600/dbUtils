package com.doubeye.commons.utils.json.mapper;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 转换器助手，用来协助生成转换器配置
 */
@SuppressWarnings("unused | WeakerAccess")
public class JSONMapperHelper {
    /**
     * 根据一个JSONObject对象来生成转换器配置，配置规则为分隔符转换为驼峰命名法
     * @param source 生成转换器的JSONObject对象
     * @return 转换配置
     */
    public static NameMapConfig getToCamelMapper(JSONObject source) {
        NameMapConfig config = new NameMapConfig();
        for (Object key :source.keySet()) {
            config.addNameMap(key.toString(), CamelFieldNameHelper.toCamel(key.toString(), "_"));
        }
        return config;
    }
    /**
     * 根据一个JSONObject对象来生成转换器配置，配置规则为驼峰命名法转换为分隔符
     * @param source 生成转换器的JSONObject对象
     * @return 转换配置
     */
    public static NameMapConfig getFromCamelMapper(JSONObject source) {
        NameMapConfig config = new NameMapConfig();
        for (Object key :source.keySet()) {
            config.addNameMap(key.toString(), CamelFieldNameHelper.fromCamel(key.toString(), "_"));
        }
        return config;
    }

    /**
     * 根据BeanClass中的字段名获得转换器配置，配置规则为驼峰命名法转换为分隔符
     * @param beanClass 建立规则的类名
     * @param separator 分隔符
     * @return 转换配置
     */
    public static NameMapConfig getFromClass(Class beanClass, String separator) {
        NameMapConfig config = new NameMapConfig();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            config.addNameMap(fieldName, CamelFieldNameHelper.fromCamel(fieldName, separator));
        }
        return config;
    }

    /**
     * 根据BeanClass中的字段名获得转换器配置，配置规则为驼峰命名法转换为分隔符('_')
     * @param beanClass 建立规则的类名
     * @return 转换配置
     */
    public static NameMapConfig getFromClass(Class beanClass) {
        return getFromClass(beanClass, "_");
    }

    /**
     * 将JSONObject对象按照转换配置转换起属性名
     * @param origin 要转换的JSONObject对象
     * @param config 转换配置
     * @return 转换后的JSONObject对象
     */
    public static JSONObject doNameMapper(JSONObject origin, NameMapConfig config) {
        JSONObject result = new JSONObject();
        List<String> ignores = config.getIgnores();
        Map<String, String> nameMappers = config.getNameMappers();
        for (Object key : origin.keySet()) {
            String fieldName = key.toString();
            if (ignores.contains(fieldName)) {
                continue;
            }
            result.put(nameMappers.getOrDefault(fieldName, fieldName), origin.getString(fieldName));
        }
        return result;
    }
    /**
     * 将JSONArray对象按照转换配置转换起属性名
     * @param origin 要转换的JSONObject对象
     * @param config 转换配置
     * @return 转换后的JSONObject对象
     */
    public static JSONArray doNameMapper(JSONArray origin, NameMapConfig config) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < origin.size(); i ++) {
            result.add(doNameMapper(origin.getJSONObject(i), config));
        }
        return result;
    }

}
