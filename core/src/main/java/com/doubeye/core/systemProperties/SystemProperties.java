package com.doubeye.core.systemProperties;

import com.doubeye.commons.database.sql.SQLExecutor;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 系统属性
 */
@SuppressWarnings("unused")
public class SystemProperties {
    /**
     * 获得整形属性值
     * @param conn 数据库连接
     * @param propertyName 属性名
     * @return 指定属性的整形值
     * @throws SQLException SQL异常
     */
    public static int getInt(Connection conn, String propertyName) throws SQLException {
        String value = getString(conn, propertyName);
        return StringUtils.isEmpty(value) ? 0 : Integer.valueOf(value);
    }

    /**
     * 获得指定属性的字符串值
     * @param conn 数据库连接
     * @param propertyName 属性名
     * @return 指定属性的字符串属性
     * @throws SQLException SQL异常
     */
    public static String getString(Connection conn, String propertyName) throws SQLException {
        try (ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_PROPERTY_BY_IDENTIFIER, propertyName))) {
            if (rs.next()) {
                return rs.getString("value");
            }
            return null;
        }
    }

    /**
     * 将指定属性的属性值+1
     * @param conn 数据库连接
     * @param propertyName 属性名
     * @throws SQLException SQL异常
     */
    public static void increasePropertyValue(Connection conn, String propertyName) throws SQLException {
        increasePropertyValue(conn, propertyName, 1);
    }
    /**
     * 将指定属性的属性值增加指定的数值
     * @param conn 数据库连接
     * @param propertyName 属性名
     * @param step 增加的值
     * @throws SQLException SQL异常
     */
    @SuppressWarnings("WeakerAccess")
    public static void increasePropertyValue(Connection conn, String propertyName, int step) throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_INCREASE_VALUE_BY_IDENTIFIER, step, propertyName));
    }

    public static void setValue(Connection conn, String propertyName, String value) throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_VALUE_VALUE_BY_IDENTIFIER, value, propertyName));
    }

    public static void saveValue(Connection conn, String propertyName, String description, String value) throws SQLException {
        try (ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_PROPERTY_BY_IDENTIFIER, propertyName))) {
            if (rs.next()) {
                SQLExecutor.execute(conn, String.format(SQL_VALUE_VALUE_BY_IDENTIFIER, value, propertyName));
            } else {
                SQLExecutor.execute(conn, String.format(SQL_INSERT_VALUE, propertyName, description, value));
            }
        }

    }

    /**
     * 获得指定的属性值
     */
    private static final String SQL_SELECT_PROPERTY_BY_IDENTIFIER = "SELECT identifier, name, value FROM core_properties WHERE identifier = '%s'";
    /**
     * 将指定属性增加一定的值
     */
    private static final String SQL_INCREASE_VALUE_BY_IDENTIFIER = "UPDATE core_properties SET value = CAST(value AS UNSIGNED) + %d WHERE identifier = '%s'";
    /**
     * 指定属性值
     */
    private static final String SQL_VALUE_VALUE_BY_IDENTIFIER = "UPDATE core_properties SET value = '%s' WHERE identifier = '%s'";
    /**
     * 添加指定属性
     */
    private static final String SQL_INSERT_VALUE = "INSERT INTO  core_properties(identifier, name, value) VALUES('%s', '%s', '%s')";
}
