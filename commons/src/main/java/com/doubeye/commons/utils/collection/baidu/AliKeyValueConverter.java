package com.doubeye.commons.utils.collection.baidu;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 用来将JSON转换为阿里云推荐引擎使用的key-value格式
 * 具体格式参见https://help.aliyun.com/document_detail/30390.html?spm=5176.doc32462.2.2.z1kvJD 页面的注释1
 */
public class AliKeyValueConverter {
    /**
     * 将JSONObject 转为Ali key-value
     * @param source 原始对象
     * @return 阿里Key-Value字符串
     */
    public static String toAliKeyValue(JSONObject source) {
        return toAliKeyValue(source, KEY_SEPARATOR, VALUE_SEPARATOR);
    }

    /**
     * 将JSONObject 转为Ali key-value，加入key-key之间的分隔符和key-value分隔符用来做回调
     * @param source 要转换的JSONObject
     * @param keySeparator 键之间的分隔符
     * @param valueSeparator 键和值之间的分隔符
     * @return 阿里key-value字符串
     */
    @SuppressWarnings("unchecked")
    private static String toAliKeyValue(JSONObject source, char keySeparator, char valueSeparator) {
        StringBuilder result = new StringBuilder();
        if (source == null) {
            return "";
        }
        Set<?> keys = source.keySet();
        for(Object key : keys) {
            Object value = source.get(key);
            if (value instanceof JSONArray) {
                result.append(key);
                JSONArray array = (JSONArray) value;
                array.forEach(o -> result.append(VALUE_SEPARATOR).append(getValue(o)));
                /*
                for (int i = 0; i < array.size(); i ++) {
                    result.append(VALUE_SEPARATOR).append(getValue(array.get(i)));
                }
                 */
            //} else if (value instanceof JSONObject && !(value instanceof JSONArray)) {
            } else if (value instanceof JSONObject) {
                result.append(key).append(VALUE_SEPARATOR).append(toAliKeyValue((JSONObject) value, VALUE_SEPARATOR, KEY_VALUE_SEPARATOR));
            } else {
                result.append(key).append(valueSeparator).append(getValue(value));
            }
            result.append(keySeparator);
        }
        return result.toString().substring(0, result.length() - 1);
    }

    /**
     * 将ali key-value转换为JSONObject
     * @param source 阿里 key-value字符串
     * @return 被转换的结果，格式为JSONObject
     */
    public static JSONObject fromAliKeyValue(String source) {
        JSONObject result = new JSONObject();
        String[] firstProcessed = source.split(String.valueOf(KEY_SEPARATOR));
        for (String pair : firstProcessed) {
            int valueSeparatorOccurred = StringUtils.countMatches(pair, String.valueOf(VALUE_SEPARATOR));
            // 简单的keyValueVALUE_SEPARATOR只出现过1此
            if (valueSeparatorOccurred == 1) {
                String[] keyValue = pair.split(String.valueOf(VALUE_SEPARATOR));
                result.put(keyValue[0], keyValue[1]);
            } else if (valueSeparatorOccurred > 1){
                //多keyValue
                if (pair.contains(String.valueOf(KEY_VALUE_SEPARATOR))) {
                    String key = pair.substring(0, pair.indexOf(VALUE_SEPARATOR));
                    String value = pair.substring(pair.indexOf(VALUE_SEPARATOR) + 1, pair.length());
                    JSONObject valueObject = new JSONObject();
                    String[] valueArray = value.split(String.valueOf(VALUE_SEPARATOR));
                    for (String element : valueArray) {
                        String[] subKeyValuePair = element.split(String.valueOf(KEY_VALUE_SEPARATOR));
                        valueObject.put(subKeyValuePair[0], subKeyValuePair[1]);
                    }
                    result.put(key, valueObject);
                } else { //多值
                    String key = pair.substring(0, pair.indexOf(VALUE_SEPARATOR));
                    String values = pair.substring(pair.indexOf(VALUE_SEPARATOR) + 1, pair.length());
                    result.put(key, JSONArray.fromObject(values.split(String.valueOf(VALUE_SEPARATOR))));
                }
            }
        }
        return result;
    }

    /**
     * 键之间的分隔符
     */
    private static final char KEY_SEPARATOR = '\002';
    /**
     * 键与值之间的分隔
     */
    private static final char VALUE_SEPARATOR = '\003';
    /**
     * 多值形式下不同值之间的分隔
     */
    private static final char KEY_VALUE_SEPARATOR = '\004';

    /*
    // 为了能够观察输出，可以使用可读的字符做测试
    private static final char KEY_SEPARATOR = '!';
    private static final char VALUE_SEPARATOR = '-';
    private static final char KEY_VALUE_SEPARATOR = '=';
    */
    // TODO 放到单元测试
    public static void main(String[] args) {
        JSONObject test = new JSONObject();
        test.put("信用", 98);
        test.put("性别", "男");
        JSONObject categoryPreference = new JSONObject();
        categoryPreference.put(3298, 0.89);
        categoryPreference.put(3456, 0.98);
        test.put("类目偏好", categoryPreference);
        JSONArray tags = new JSONArray();
        tags.add("美包控");
        tags.add("准妈妈");
        test.put("标签", tags);
        String aliKeyValue = toAliKeyValue(test);
        System.out.println(aliKeyValue);
        JSONObject object = fromAliKeyValue(aliKeyValue);
        System.out.println(object.toString());
    }

    /**
     * 获得对象的字符串
     * @param object 对象
     * @return 对象的字符串形式
     */
    private static String getValue(Object object) {
        /*
        if (object instanceof Object) {
            return object.toString();
        } else {
            return String.valueOf(object);
        }
        */
        return object == null ? "" : object.toString();
    }
}


