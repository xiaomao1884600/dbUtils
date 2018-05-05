package com.doubeye.commons.database.MySQL;

import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.connection.bean.DataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/6.
 * MySQL连接对象助手
 */
@SuppressWarnings("unused")
public class MySQLConnectionHelper implements ConnectionHelper{
    /**
     * 数据库驱动
     */
    private static final String DATABASE_DRIVER_NAME = "com.mysql.jdbc.Driver";
    static {
        try {
            Class.forName(DATABASE_DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // private final static String CONNECTION_STRING = "jdbc:mysql://%s:%d/%s?serverTimezone=GMT%%2b8&verifyServerCertificate=false&useSSL=false"; TODO 过段时间看看这个6.0.x能用了吗
    // 加入useSSL=false参数，用来进制显示烦人的警告
    private final static String CONNECTION_STRING = "jdbc:mysql://%s:%d/%s?useSSL=false";
    @Override
    public Connection getConnection(DataSource dataSource) {
        try {
            return DriverManager.getConnection(String.format(CONNECTION_STRING, dataSource.getHost(), dataSource.getPort(), dataSource.getDefaultSchema()), dataSource.getUserName(), dataSource.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException("无法获得数据源:" + dataSource.toString().replace("\\", "\\\\")  + "\n" + CONNECTION_STRING + "\n" + e.getMessage());
        }
    }
}
