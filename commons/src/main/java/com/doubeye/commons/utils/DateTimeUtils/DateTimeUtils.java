package com.doubeye.commons.utils.DateTimeUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * @author doubeye
 * @version 1.0.0
 * 时间日期工具
 */
@SuppressWarnings("unused")
public class DateTimeUtils {
    /**
     * 获得格式化后的当前时间
     * @return 格式化的时间，格式为yyyy年MM月dd日 HH时mm分ss秒
     */
    public static String getCurrentDisplayTime() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date());
    }

    /**
     * 获得默认格式的当前时间
     * @return 默认格式的当前时间
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat(DEFAULT_FORMAT).format(new Date());
    }

    /**
     * 获得指定格式的当前时间
     * @param format 格式
     * @return 指定格式的当前时间
     */
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 获得默认时间格式
     * @return 默认的时间格式
     */
    public static SimpleDateFormat getDefaultTimeFormat() {
        return new SimpleDateFormat(DEFAULT_FORMAT);
    }

    /**
     * 用默认格式格式化指定时间
     * @param date 时间
     * @return 被格式化的时间
     */
    public static String getDefaultFormattedDateTime(Date date) {
        return new SimpleDateFormat(DEFAULT_FORMAT).format(date);
    }

    /**
     * 用指定格式格式化指定的时间
     * @param date 时间
     * @param format 格式字符串
     * @return 被格式化的时间
     */
    public static String getDefaultFormattedDateTime(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date getDateFromDefaultFormat(String source) {
        return getDate(source, DEFAULT_FORMAT);
    }
    public static Date getDate(String source, String formatPatten) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(formatPatten);
        try {
            return format.parse(source);
        } catch (ParseException e) {
            return null;
        }
    }


    public static int compare(String firstTime, String secondTime) {
        Calendar first = Calendar.getInstance();
        first.setTime(DateTimeUtils.getDateFromDefaultFormat(firstTime));
        Calendar second = Calendar.getInstance();
        second.setTime(DateTimeUtils.getDateFromDefaultFormat(secondTime));
        return first.compareTo(second);
    }

    public static long timeDiff(String firstTime, String secondTime) {
        Date first = DateTimeUtils.getDateFromDefaultFormat(firstTime);
        Date second = DateTimeUtils.getDateFromDefaultFormat(secondTime);
        return first.getTime() - second.getTime();
    }

    public static String today() {
        return today(DEFAULT_DATE_FORMAT);
    }

    public static String tomorrow() {
        return tomorrow(DEFAULT_DATE_FORMAT);
    }

    public static String today(String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(new Date());
    }

    public static String tomorrow(String dateFormat) {
        /* TODO delete
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat(dateFormat).format(tomorrow.getTime());
        */
        return new SimpleDateFormat(dateFormat).format(timeDiff(new Date(), Calendar.DAY_OF_MONTH, 1));
    }

    /**
     * 时间计算
     * @param baseDate 基准时间
     * @param unit 单位
     * @param value 值
     * @return 计算结果
     */
    public static Date timeDiff(Date baseDate, int unit, int value) {
        Calendar result = Calendar.getInstance();
        result.setTime(baseDate);
        result.add(unit, value);
        return result.getTime();
    }

    /**
     * 获得与JavaScript new Date().toUTCString()的时间
     * @param date 日期对象
     * @return GMT格式化过的时间
     */
    public static String toGMTString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(GMT_DATE_TIME_FORMAT, Locale.UK);
        formatter.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return formatter.format(date);
    }

    public static final long SECOND_PER_DAY = 60 * 60 * 24;
    /**
     * 默认日期时间格式
     */
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String GMT_DATE_TIME_FORMAT = "E, dd MMM yyyy HH:mm:ss z";
}
