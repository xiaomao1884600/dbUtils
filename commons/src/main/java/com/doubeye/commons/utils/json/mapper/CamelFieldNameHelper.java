package com.doubeye.commons.utils.json.mapper;


import org.apache.commons.lang.StringUtils;


/**
 * @author doubeye
 * @version 1.0.0
 * 将字符串转Camel规则助手
 */
public class CamelFieldNameHelper {
    /**
     * 将驼峰命名法改为分隔符分隔的命名法，其中大写字母会变为小写字母
     * @param source 驼峰命名法的字符串
     * @param separator 分隔符
     * @return 转换后的字符串
     */
    public static String fromCamel(String source, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i ++) {
            char c = source.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                builder.append(separator).append(source.substring(i, i + 1).toLowerCase());
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * 将分隔符分隔的字符串转换为驼峰命名法
     * @param source 分隔符分隔的字符串
     * @param separator 分隔符
     * @return 驼峰命名法的字符串
     */
    public static String toCamel(String source, String separator) {
        if (!source.contains(separator)) {
            return source;
        }
        StringBuilder builder = new StringBuilder();
        String beforeSeparator, afterSeparator = "";
        while (source.contains(separator)) {
            beforeSeparator = StringUtils.substringBefore(source, separator);
            afterSeparator = StringUtils.substringAfter(source, separator);
            if (afterSeparator.length() > 1) {
                afterSeparator = afterSeparator.substring(0, 1).toUpperCase() + afterSeparator.substring(1, afterSeparator.length());
            } else {
                afterSeparator = afterSeparator.toUpperCase();
            }
            builder.append(beforeSeparator);
            source = afterSeparator;
        }
        builder.append(afterSeparator);
        return builder.toString();
    }
}
