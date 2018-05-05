package com.doubeye.commons.utils.collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * @author doubeye
 * @version 1.0.0
 * 集合工具类
 */
public class CollectionUtils {
    /**
     * 16进制字符
     */
    private static final String HEXES = "0123456789ABCDEF";

    /**
     * 将列表转化为指定分隔符分隔的字符串
     *
     * @param list      列表对象
     * @param separator 分隔符
     * @return 以分隔符分隔的列表对象
     */
    public static String split(List<?> list, String separator) {
        if (list.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        list.forEach(o -> sb.append(o.toString()).append(separator));
        return sb.substring(0, sb.lastIndexOf(separator));
    }

    /**
     * 将列表转化为指定分隔符分隔的字符串
     *
     * @param list      列表对象
     * @param separator 分隔符
     * @param limit     转换个数限制，只转换限制内的内容，如果超过个数，则添加...
     * @return 指定分隔符的字符串，如果超过转换个数，末尾添加...
     */
    public static String split(List<?> list, String separator, int limit) {
        if (list.size() == 0) {
            return "";
        }
        int count = 1;
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o.toString()).append(separator);
            count++;
            if (count == limit) {
                sb.append("...").append(separator);
                break;
            }
        }
        return sb.substring(0, sb.lastIndexOf(separator));
    }



    /**
     * 将列表转化为指定分隔符分隔的字符串.每个元素用指定的elementWrapper包裹
     *
     * @param list           列表对象
     * @param separator      分隔符
     * @param elementWrapper 每个元素的包裹符号，比如将字符串用'包裹
     * @return 被包裹的，以指定字符分隔的字符串
     */
    public static String split(List<?> list, String separator, String elementWrapper) {
        if (list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        list.forEach(o -> sb.append(elementWrapper).append(o.toString()).append(elementWrapper).append(separator));
        return sb.substring(0, sb.lastIndexOf(separator));
    }

    /**
     * 将字符串按指定字符串进行分割
     *
     * @param origin    原始字符串
     * @param separator 分隔符
     * @return 字符串列表
     */
    public static List<String> split(String origin, String separator) {
        String[] array = origin.split(separator);
        return Arrays.asList(array);
    }

    /**
     * 将对象数组转变为分隔符分隔的字符串
     *
     * @param array     对象数组
     * @param separator 分隔符
     * @return 指定分隔符的字符串
     */
    public static String toString(Object[] array, String separator) {
        StringBuilder result = new StringBuilder();
        for (Object object : array) {
            result.append(object.toString()).append(separator);
        }
        return result.substring(0, result.lastIndexOf(separator));
    }

    public static String toString(List<?> list, String separator) {
        StringBuilder result = new StringBuilder();
        for (Object object : list) {
            result.append(object.toString());
            if (!StringUtils.isEmpty(separator)) {
                result.append(separator);
            }
        }
        if (StringUtils.isEmpty(separator)) {
            return result.toString();
        } else {
            return result.substring(0, result.lastIndexOf(separator));
        }
    }

    /**
     * 将指定指定字符串按指定分隔符转换为JSONArray
     *
     * @param source    要转换的字符串
     * @param separator 分隔符
     * @return 转换的JSONArray
     */
    public static JSONArray toJSONArray(String source, String separator) {
        JSONArray result = new JSONArray();
        if (StringUtils.isEmpty(source)) {
            return result;
        } else {
            List<String> list = split(source, separator);
            return JSONArray.fromObject(list);
        }
    }

    /**
     * 从文件中载入JSONArray，每行为一个JSONObject
     * @param fileName 文件名
     * @return JSONArray
     */
    public static JSONArray toJSONArray(String fileName) throws IOException {
        JSONArray result = new JSONArray();
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(JSONObject.fromObject(line));
            }
        }
        return result;
    }


    /**
     * 将字节数组转化为十六进制字符串
     * based on a suggestion by Lew on usenet-cljp
     *
     * @param raw 要转换的数字
     * @return 16进制表示的数组
     */
    public static String toHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * 从文件中读入字符串列表
     * @param fileName 文件名
     * @return 字符串列表，每行为一个元素
     * @throws IOException IO异常
     */
    public static List<String> loadFromFile(String fileName) throws IOException {
        List<String> result = new ArrayList<>();
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }

    /**
     * 将JSONArray转换为Vector<String>
     * @param array JSONArray 对象
     * @return Vector对象
     */
    public static Vector<String> getVectorFromJSONArray(JSONArray array) {
        Vector<String> vector = new Vector<>();
        for (Object element : array) {
            vector.add(element.toString());
        }
        return vector;
    }

    /**
     * 将JSONArray转换为Vector<String>
     * @param array JSONArray 对象
     * @return Vector对象
     */
    public static List<String> getListFromJSONArray(JSONArray array) {
        List<String> vector = new ArrayList<>();
        for (Object element : array) {
            vector.add(element.toString());
        }
        return vector;
    }

    public static double[] splitDouble(JSONArray array) {
        double[] result = new double[array.size()];
        for (int i = 0; i < array.size(); i ++) {
            result[i] = array.getDouble(i);
        }
        return result;
    }
}
