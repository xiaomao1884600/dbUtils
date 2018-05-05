package com.doubeye.commons.utils.request;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.1
 * HttpRequest 助手类
 * 历史：
 *  1.0.1
 *      + 添加getJSONArray(Map<String, String[]> params, String parameterName) 方法
 */
@SuppressWarnings("unused")
public class RequestHelper {
    /**
     * 获得字符串参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @return 参数值
     */
    public static String getString(Map<String, String[]> params, String parameterName) {
        return getString(params, parameterName, "iso-8859-1");
    }

    /**
     * 获得字符串参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @param originCharset 原始内容中的字符串编码
     * @return 参数值
     */
    public static String getString(Map<String, String[]> params, String parameterName, String originCharset) {
        if (params.containsKey(parameterName)) {
            try {
                return new String(params.get(parameterName)[0].getBytes(originCharset), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
    /**
     * 获得int参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @return 参数值
     */
    public static int getInt(Map<String, String[]> params, String parameterName) {
        String value = getString(params, parameterName);
        if (StringUtils.isEmpty(value)) {
            return 0;
        } else {
            return Integer.valueOf(value);
        }
    }

    /**
     * 获得int参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @return 参数值
     */
    public static int getInt(Map<String, String[]> params, String parameterName, int defaultValue) {
        String value = getString(params, parameterName);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    /**
     * 获得boolean参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @return 参数值
     */
    public static boolean getBoolean(Map<String, String[]> params, String parameterName) {
        return Boolean.valueOf(getString(params, parameterName));
    }
    /**
     * 获得int参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static boolean getBoolean(Map<String, String[]> params, String parameterName, boolean defaultValue) {
        if (params.containsKey(parameterName)) {
            return Boolean.valueOf(getString(params, parameterName));
        } else {
            return defaultValue;
        }
    }

    /**
     * 获得JSONObject参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @return JSONObject 参数值
     */
    public static JSONObject getJSONObject(Map<String, String[]> params, String parameterName) {
        return JSONObject.fromObject(getString(params, parameterName));
    }
    /**
     * 获得JSONArray参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @return JSONArray 参数值
     */
    public static JSONArray getJSONArray(Map<String, String[]> params, String parameterName) {
        return JSONArray.fromObject(getString(params, parameterName));
    }

    /**
     * 获得JSONArray参数值
     * @param params 参数对象
     * @param parameterName 参数属性
     * @param originCharset 参数属性
     * @return JSONArray 参数值
     */
    public static JSONArray getJSONArray(Map<String, String[]> params, String parameterName, String originCharset) {
        return JSONArray.fromObject(getString(params, parameterName, originCharset));
    }

    /**
     * 处理提交的数据
     * @param request HttpServletRequest 对象
     * @return 处理后的参数对象
     * @throws IOException IO异常
     */
    public static Map<String, String[]> processFromData(ServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        JSONObject obj;
        try {
            obj = JSONObject.fromObject(URLDecoder.decode(stringBuilder.toString(), "utf-8"));
            if (obj.containsKey("data")) {
                obj = obj.getJSONObject("data");
            }
        } catch (Exception e) {
            obj = new JSONObject();
        }
        Map<String, String[]> parameters = new HashMap<>();
        Set keys = obj.keySet();
        for (Object key : keys) {
            String[] value = new String[1];
            value[0] = obj.getString(key.toString());
            parameters.put(key.toString(), value);
        }
        return parameters;
    }

    /**
     * 打印请求地址
     * @param params 参数
     * @return 转换为字符串的参数信息
     */
    public static String getRequestParameters(Map<String, String[]> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("-------PRINTING REQUEST PARAMETERS BEGIN\n");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String[] values = params.get(key);
            StringBuilder sb = new StringBuilder();
            for (String value : values) {
                sb.append(value).append(",");
            }
            builder.append("\t").append(key).append(" = ").append(sb.toString()).append("\n");
        }
        builder.append("-------PRINTING REQUEST PARAMETERS END\n");
        return builder.toString();
    }

    public static final String PROPERTY_NAME_PAGE_START = "start";
    public static final String PROPERTY_NAME_PAGE_SIZE = "size";
}
