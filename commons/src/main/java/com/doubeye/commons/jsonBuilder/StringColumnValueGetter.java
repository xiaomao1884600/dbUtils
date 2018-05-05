package com.doubeye.commons.jsonBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 字符串型字段值获得类
 */
public class StringColumnValueGetter implements ColumnValueGetter{
    @Override
    public Object getColumnValue(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }
}
