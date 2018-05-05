package com.doubeye.commons.database.MySQL.administrator;

import com.doubeye.commons.database.connection.bean.DataSource;

/**
 * MySQL命令基类
 */
public class MySQLCommand {
    /**
     * 数据源
     */
    protected DataSource dataSource;
    /**
     * MySQL bin 文件路径
     */
    protected String mysqlBinDirectory;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getMysqlBinDirectory() {
        return mysqlBinDirectory;
    }

    public void setMysqlBinDirectory(String mysqlBinDirectory) {
        this.mysqlBinDirectory = mysqlBinDirectory;
    }
}
