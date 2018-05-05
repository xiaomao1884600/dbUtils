package com.doubeye.commons.database.MySQL.administrator.log;

import com.doubeye.commons.database.sql.SQLExecutor;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 获得MySQL错误日志文件大小
 */
public class ErrorLogSizeGetter {
    /**
     * 数据库连接对象
     */
    private Connection conn;

    /**
     * 错误日志文件大小，单位为byte,如果没有设置错误日志，或错误日志文件不为空，返回-1
     * @return 错误文件日志
     */
    public long getErrorLogSize() throws SQLException, IOException {
        String errorLogFileName = getErrorLogFileName(conn);
        if (StringUtils.isEmpty(errorLogFileName)) {
            return -1;
        }
        File file = new File(errorLogFileName);
        if (file.exists() && file.isFile()) {
            try (FileInputStream input = new FileInputStream(file);
                 FileChannel channel = input.getChannel()) {
                return channel.size();
            }

        } else {
            return -1;
        }
    }

    private static String getErrorLogFileName(Connection conn) throws SQLException {
        try (ResultSet rs = SQLExecutor.executeQuery(conn, SQL_GET_ERROR_LOG_FILE_NAME)){
            if (rs.next()) {
                return rs.getString("Value");
            } else {
                return null;
            }
        }
    }

    private static final String SQL_GET_ERROR_LOG_FILE_NAME = "SHOW VARIABLES LIKE 'log_error'";

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
