package com.doubeye.commons.jsonBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 结果集中字段值获得接口
 */
public interface ColumnValueGetter {
    /**
     * 获得结果集中字段的值
     * @param rs 结果集
     * @param columnName 字段名
     * @return 字段值
     * @throws SQLException SQL异常
     */
    Object getColumnValue(ResultSet rs, String columnName) throws SQLException;
}
