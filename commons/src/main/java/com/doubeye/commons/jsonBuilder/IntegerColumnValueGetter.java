package com.doubeye.commons.jsonBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 整数数值型字段值获得类，支持长整型
 */
public class IntegerColumnValueGetter implements ColumnValueGetter{
    @Override
    public Object getColumnValue(ResultSet rs, String columnName) throws SQLException {
        return rs.getLong(columnName);
    }
}
