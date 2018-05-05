package com.doubeye.commons.jsonBuilder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 将数据库结果集疯转为JSON对象
 *  * history :
 *  1.1.0:
 *      + 为了生成的JSONObject能够正确区分字符串和数字，添加值获取器
 */
public class JSONWrapper {
    private static Logger logger = LogManager.getLogger(JSONWrapper.class);
    /**
     * 将结果集封装为JSONArray
     * @param rs 数据库结果集
     * @return 结果JSONArray
     * @throws SQLException SQL异常
     */
    public static JSONArray getJSON(ResultSet rs) throws SQLException {
        JSONArray result = new JSONArray();
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, ColumnValueGetter> columns = getColumnMetaData(meta);
        while (rs.next()) {
            JSONObject obj = new JSONObject();
            for (String columnName : columns.keySet()) {
                obj.put(columnName, columns.get(columnName).getColumnValue(rs, columnName));
            }
            result.add(obj);
        }
        return result;
    }

    /**
     * 将结果集的第一条数据封装为JSONObject
     * @param rs 数据库结果集
     * @return JSONObject
     * @throws SQLException SQL异常
     */
    public static JSONObject getOneRecord(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, ColumnValueGetter> columns = getColumnMetaData(meta);
        return getOneRecord(rs, columns);
    }

    /**
     * 获得结果集的列信息
     * @param meta ResultSetMetaData
     * @return 结果集的所有列以及列对应的值获得类
     * @throws SQLException SQL异常
     */
    public static Map<String, ColumnValueGetter> getColumnMetaData(ResultSetMetaData meta) throws SQLException {
        Map<String, ColumnValueGetter> result = new HashMap<>();
        int columnCount = meta.getColumnCount();
        for (int i = 1; i <= columnCount; i ++) {
            ColumnValueGetter columnValueGetter = getColumnValueGetterByType(meta.getColumnType(i));
            result.put(meta.getColumnLabel(i), columnValueGetter);
        }
        return result;
    }

    /**
     * 获得结果集中的第一条数据，并封装为JSONObject
     * @param rs 结果集
     * @param columns 列名的字符串列表
     * @return 封装的结果，格式为JSONObject
     * @throws SQLException SQL异常
     */
    private static JSONObject getOneRecord(ResultSet rs, Map<String, ColumnValueGetter> columns) throws SQLException {
        if (rs.next()) {
            JSONObject obj = new JSONObject();
            for (String columnName : columns.keySet()) {
                // logger.trace(columnName + " " + columns.get(columnName).getClass().getName());
                obj.put(columnName, columns.get(columnName).getColumnValue(rs, columnName));
            }
            return obj;
        } else {
            return new JSONObject();
        }
    }

    private static ColumnValueGetter getColumnValueGetterByType(int type) {
        if (type == Types.BIT || type == Types.TINYINT || type == Types.SMALLINT || type == Types.INTEGER ||
                type == Types.BIGINT || type == Types.BOOLEAN) {
            return new IntegerColumnValueGetter();
        } else if (type == Types.FLOAT || type == Types.REAL || type == Types.DOUBLE || type == Types.NUMERIC ||
                type == Types.DECIMAL) {
            return new DecimalColumnValueGetter();
        } else {
            return new StringColumnValueGetter();
        }
    }
}
