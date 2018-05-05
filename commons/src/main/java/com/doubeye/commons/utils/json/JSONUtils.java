package com.doubeye.commons.utils.json;


import com.doubeye.commons.utils.constant.CommonConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * JSON工具包
 */
public class JSONUtils {
    /**
     * 从JSON文件中获得JOSN对象
     * @param file 文件对象
     * @return JSON对象
     */
    public static JSONObject getJsonObjectFromFile(File file) {
        try(InputStream inputStream = new FileInputStream(file)) {
            return getJsonObjectFromInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(String.format(MESSAGE_CAN_NOT_READ_FROM_FILE, file.getName(), e.getMessage()));
        }
    }

    /**
     * 从输入流中获得JSONObject
     * @param inputStream 输入流
     * @return JSONObject
     * @throws IOException IO异常
     */
    public static JSONObject getJsonObjectFromInputStream(InputStream inputStream) throws IOException {
        String objString = IOUtils.toString(inputStream, "utf-8");
        return JSONObject.fromObject(objString);
    }

    /**
     * 从输入流中获得JSONArray
     * @param inputStream 输入流
     * @return JSONArray
     * @throws IOException IO异常
     */
    @SuppressWarnings("WeakerAccess")
    public static JSONArray getJsonArrayFromInputStream(InputStream inputStream) throws IOException {
        String objString = IOUtils.toString(inputStream, "utf-8");
        return JSONArray.fromObject(objString);
    }


    /**
     * 从JSON数组文件中获得JSON数组
     * @param file 文件对象
     * @return JSON数组
     */
    public static JSONArray getJsonArrayFromFile(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return getJsonArrayFromInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(String.format(MESSAGE_CAN_NOT_READ_FROM_FILE, file.getName(), e.getMessage()));
        }
    }

    public static JSONArray getJsonArrayFromFile(File file, String charset) throws IOException {
        if ("utf-8".equalsIgnoreCase(charset)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(file))))) {
                return getJsonArrayFromReader(reader);
            }
        } else {
            return getJsonArrayFromFile(file);
        }
    }

    public static JSONObject getJsonObjectFromFile(File file, String charset) throws IOException {
        if ("utf-8".equalsIgnoreCase(charset)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(file))))) {
                return JSONObject.fromObject(IOUtils.toString(reader));
            }
        } else {
            return getJsonObjectFromFile(file);
        }
    }

    private static JSONArray getJsonArrayFromReader(BufferedReader reader) throws IOException {
        String content = IOUtils.toString(reader);
        return JSONArray.fromObject(content);
    }

    /**
     * 根据指定的path，定位到JSONObject中的对象，path之间用'/'分隔
     * 如果path中有部分不存在，会在原来的jsonObject内创建相应的key
     * @param jsonObject jsonObject
     * @param path 路径
     * @return 返回的对象
     */
    public static JSONObject findResultObjectByPathAndKey(JSONObject jsonObject, final String path) {
        String[] nodes = path.split("/");
        JSONObject tempResult = jsonObject;
        for (String node : nodes) {
            if (!node.equals("")) {
                if (!tempResult.containsKey(node)) {
                    tempResult.put(node, new JSONObject());
                }
                tempResult = tempResult.getJSONObject(node);
            }
        }
        return tempResult;
    }
    // TODO sort方法需要更为优美的实现
    public static void sort(JSONArray array, String sortPropertyName) {
        if (array.size() <= 1) {
            return;
        }
        for (int i = 0; i < array.size(); i ++) {
            JSONObject first = array.getJSONObject(i);
            for (int j = i + 1; j < array.size(); j ++) {
                JSONObject second = array.getJSONObject(j);
                int firstValue = first.getInt(sortPropertyName);
                int secondValue = second.getInt(sortPropertyName);
                if (secondValue > firstValue) {
                    array.set(i, second);
                    array.set(j, first);
                    first = array.getJSONObject(i);
                }
            }
        }
    }

    public static void sortString(JSONArray array, String sortPropertyName) {
        if (array.size() <= 1 || StringUtils.isEmpty(sortPropertyName)) {
            return;
        }
        for (int i = 0; i < array.size(); i ++) {
            JSONObject first = array.getJSONObject(i);
            for (int j = i + 1; j < array.size(); j ++) {
                JSONObject second = array.getJSONObject(j);
                String firstValue = first.getString(sortPropertyName);
                String secondValue = second.getString(sortPropertyName);
                if (secondValue.compareTo(firstValue) < 0) {
                    array.set(i, second);
                    array.set(j, first);
                    first = array.getJSONObject(i);
                }
            }
        }
    }


    public static void merge(JSONObject target, JSONObject toBeMerged) {
        for (Object key : toBeMerged.keySet()) {
            target.put(key, toBeMerged.get(key));
        }
    }

    /**
     * 查找数组中满足条件的第一个元素
     * @param array 要查找的数组
     * @param condition 条件对象
     * @return 返回array中满足条件的对象数组，如果没有满足条件的元素，则返回空null
     */
    public static JSONObject findFirst(JSONArray array, JSONObject condition) {
        JSONArray resultArray = find(array, condition, true);
        return (resultArray.size() > 0) ? resultArray.getJSONObject(0) : null;
    }

    /**
     * 查找数组中满足条件的元素
     * @param array 要查找的数组
     * @param condition 条件对象
     * @return 返回array中满足条件的对象数组，如果没有满足条件的元素，则返回空JSONArray数组
     */
    public static JSONArray findAll(JSONArray array, JSONObject condition) {
        return find(array, condition, false);
    }

    /**
     * 查找数组中满足条件的元素
     * @param array 要查找的数组
     * @param condition 条件对象
     * @param onlyFirst 是否仅返回第一个
     * @return 满足条件的Array，如果onlyFirst == true，则结果中仅包括满足条件的第一个元素（如果有），如果没有满足条件
     * 的元素，则返回空JSONArray数组
     */
    private static JSONArray find(JSONArray array, JSONObject condition, boolean onlyFirst) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < array.size(); i ++) {
            JSONObject obj = array.getJSONObject(i);
            if (equalByCondition(obj, condition)) {
                result.add(obj);
                if (onlyFirst) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 判断JSONObject是否满足给定的条件
     * @param object 被检查的JSONObject
     * @param condition 条件
     * @return 如果符合则返回true，否则返回false
     */
    public static boolean equalByCondition(JSONObject object, JSONObject condition) {
        if (condition == null) {
            return true;
        }
        Set keys = condition.keySet();
        for (Object key : keys) {
            if (!object.containsKey(key) || (!object.getString(key.toString()).equals(condition.getString(key.toString())))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将JSONObject转化为JSONArray
     * @param source 要转化的JSONObject
     * @param keyPropertyName 对象key的值在数组元素对象的属性
     * @param valuePropertyName 对象value的值在数组元素对象的属性
     * @return 由JSONObject转化的JSONArray
     */
    public static JSONArray objectToArray(JSONObject source, String keyPropertyName, String valuePropertyName) {
        JSONArray result = new JSONArray();
        for (Object key : source.keySet()) {
            JSONObject element = new JSONObject();
            element.put(keyPropertyName, key);
            element.put(valuePropertyName, source.get(key));
            result.add(element);
        }
        return result;
    }

    /**
     * 将key-value形式的JSONArray转换为JSONObject
     * @param source 原始的JSONArray对象
     * @param keyPropertyName 作为JSONObject键的属性 如果kePropertyName不存在，则跳过此元素
     * @param valuePropertyName 作为键的值的属性，如果valuePropertyName不存在，则将整个Object作为value
     * @return 转换后的JSONObject对象
     */
    public static JSONObject arrayToObject(JSONArray source, String keyPropertyName, String valuePropertyName) {
        JSONObject result = new JSONObject();
        for (int i = 0; i < source.size(); i ++) {
            JSONObject object = source.getJSONObject(i);
            if (object.containsKey(keyPropertyName)) {
                if (object.containsKey(valuePropertyName)) {
                    result.put(object.get(keyPropertyName), object.get(valuePropertyName));
                } else {
                    result.put(object.get(keyPropertyName), object);
                }
            }
        }
        return result;
    }

    /**
     * 将key-value形式的JSONArray转换为JSONObject
     * @param source 原始的JSONArray对象
     * @param keyPropertyName 作为JSONObject键的属性 如果kePropertyName不存在，则跳过此元素。如果存在，整个JSONObject作为值
     * @return 转换后的JSONObject对象
     */
    public static JSONObject arrayToObject(JSONArray source, String keyPropertyName) {
        JSONObject result = new JSONObject();
        for (int i = 0; i < source.size(); i ++) {
            JSONObject object = source.getJSONObject(i);
            if (object.containsKey(keyPropertyName)) {
                result.put(object.get(keyPropertyName), object);
            }
        }
        return result;
    }

    /**
     * 按照指定的对象属性路径将指定值追加到最后的属性上
     * @param object 要赋值的对象
     * @param path 值的xpath，如果路径不存在，则自动填充
     * @param value 值
     * @param separator 路径path的分隔符
     */
    public static void appendValue(JSONObject object, String path, Object value, String separator) {
        String[] properties = path.split(separator);
        JSONObject entry = object;
        if (properties.length > 0) {
            for (int i = 0; i < properties.length - 1; i ++) {
                if (!entry.containsKey(properties[i])) {
                    entry.put(properties[i], new JSONObject());
                }
                entry = entry.getJSONObject(properties[i]);
            }
            if (!entry.containsKey(properties[properties.length - 1])) {
                entry.put(properties[properties.length - 1], new JSONArray());
            }
            entry.getJSONArray(properties[properties.length - 1]).add(value);
        }
    }

    /**
     * 格式化JSONObject 对象
     * @param object 要格式话化的对象
     * @param retraction 缩进字符串
     * @return 格式化后的字符串
     */
    public static String format(JSONObject object, String retraction) {
        StringBuilder result = new StringBuilder();
        format(object, retraction, result, 0);
        return result.toString();
    }

    /**
     * 格式化JSONObject 对象
     * @param object 要格式话化的对象
     * @param retraction 缩进字符串
     */
    private static void format(JSONObject object, String retraction, StringBuilder result, int level) {
        Set<?> keys = object.keySet();
        for (Object key : keys) {
            result.append(getLeveledRetraction(retraction, level)).append(key.toString());
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                result.append("\r");
                format((JSONObject) value, retraction, result, level + 1);
            } else if (value instanceof JSONArray){
                result.append(" - ");
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.size() - 1; i ++) {
                    result.append(array.getString(i)).append(",");
                }
                result.append(array.getString(array.size() - 1));
                result.append("\r");
            } else {
                result.append(" - ").append(value.toString()).append("\r");
            }
        }
    }

    private static String getLeveledRetraction(String retraction, int level) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= level; i ++) {
            result.append(retraction);
        }
        return result.toString();
    }


    public static String toUrl(JSONObject parameters) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        Set<?> keys = parameters.keySet();
        for (Object key : keys) {
            //builder.append("&").append(key.toString()).append("=").append(parameters.getString(key.toString()));
            builder.append("&").append(key.toString()).append("=").append(URLEncoder.encode(parameters.getString(key.toString()), "utf-8"));
        }
        String paramsPart = builder.toString();
        return StringUtils.replaceOnce(paramsPart, "&", "?");
    }


    /**
     * 无法从文件中获得JSON的报错信息
     */
    private static final String MESSAGE_CAN_NOT_READ_FROM_FILE = "无法从文件 %s 中获得JSON，%s";
}
