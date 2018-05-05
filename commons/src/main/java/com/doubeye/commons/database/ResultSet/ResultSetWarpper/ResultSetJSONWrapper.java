package com.doubeye.commons.database.ResultSet.ResultSetWarpper;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.jsonBuilder.JSONWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.1.0
 * 根据语句来返回封装为JSON的结果集
 * history :
 *  1.1.0:
 *      + 为了生成的JSONObject能够正确区分字符串和数字，添加值获取器
 */
public class ResultSetJSONWrapper {
    /**
     * 根据查询返回封装为JSON的结果集
     * @param conn 数据库连接对象
     * @param sql 查询语句
     * @return 该语句返回的
     * @throws SQLException SQL异常
     */
    public static JSONArray getJSONArrayFromSQL(Connection conn, String sql) throws SQLException {
        try (ResultSet rs = SQLExecutor.executeQuery(conn, sql)){
            return JSONWrapper.getJSON(rs);
        }
    }

    /**
     * 根据SQL语句获得第一条记录
     * @param conn 数据库连接对象
     * @param sql sql语句
     * @return SQL语句返回的第一条记录的JSONObject格式
     * @throws SQLException SQL异常
     */
    public static JSONObject getJSONObjectFromSQL(Connection conn, String sql) throws SQLException {
        try (ResultSet rs = SQLExecutor.executeQuery(conn, sql)){
            return JSONWrapper.getOneRecord(rs);
        }
    }
}
