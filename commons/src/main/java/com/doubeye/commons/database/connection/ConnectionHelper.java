package com.doubeye.commons.database.connection;

import com.doubeye.commons.database.connection.bean.DataSource;

import java.sql.Connection;

/**
 * @author  doubeye
 * @version 1.0.0
 * 连接助手
 */
public interface ConnectionHelper {
    /**
     * 关闭 Connection, ResultSet, Statement对象的统一方法
     * @param closeable Statement对象
     */
    static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得数据库连接对象
     * @param dataSource 数据源
     * @return 数据库连接
     */
    Connection getConnection(DataSource dataSource);
}
