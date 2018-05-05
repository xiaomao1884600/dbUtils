package com.doubeye.commons.database.MySQL.administrator;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.bean.DataSource;
import com.doubeye.commons.utils.runtime.RuntimeRunner;
import com.doubeye.commons.utils.runtime.RuntimeRunningResult;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/12.
 * MySQL 导入数据执行类
 */
public class MySQLWrapper extends MySQLCommand{
    /**
     * 导入文件名
     */
    private String importFileName;
    /**
     * 目标数据库的名称，如不指定，则使用dataSource中的默认数据库
     */
    private String targetDatabaseName = "";

    /**
     * 运行导入命令
     * @return 运行结果
     * @throws IOException IO异常
     * @throws InterruptedException 终端异常
     */
    public RuntimeRunningResult mysql() throws IOException, InterruptedException {
        return RuntimeRunner.runCommand(getCommandLine());
    }

    /**
     * 获得目标数据源数据库名，如果没有显示指定，使用数据源的默认数据库
     * @return 目标数据库数据源
     */
    private String getTargetDatabase() {
        if (StringUtils.isEmpty(targetDatabaseName)) {
            return dataSource.getDefaultSchema();
        } else {
            return targetDatabaseName;
        }
    }

    /**
     * 导入命令模板
     * host, port, userName, password,targetDatabaseName, importFileName
     */
    private static final String WINDOWS_MYSQL_COMMAND = "\"%s/mysql\" --host=%s --port=%s --user=%s --password=%s --default-character-set=utf8 %s < %s";
    private static final String LINUX_MYSQL_COMMAND = "\"%s/mysql\" --host=%s --port=%s --user=%s --password=%s --default-character-set=utf8 %s < %s";

    /**
     * 获得导入命令
     * @return 导入命令
     */
    private String[] getCommandLine() {
        return RuntimeRunner.getOSShellCommand(String.format(System.getProperty("os.name").toLowerCase().startsWith("win") ? WINDOWS_MYSQL_COMMAND : LINUX_MYSQL_COMMAND, mysqlBinDirectory, dataSource.getHost(), dataSource.getPort(), dataSource.getUserName(), dataSource.getPassword(), getTargetDatabase(), importFileName));
    }
    @SuppressWarnings("unused")
    public String getImportFileName() {
        return importFileName;
    }

    public void setImportFileName(String importFileName) {
        this.importFileName = importFileName;
    }
    @SuppressWarnings("unused")
    public String getTargetDatabaseName() {
        return targetDatabaseName;
    }

    public void setTargetDatabaseName(String targetDatabaseName) {
        this.targetDatabaseName = targetDatabaseName;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ApplicationContextInitiator.init();
        DataSource dataSource = GlobalApplicationContext.getInstance().getCoreDataSource();
        String mysqlBinDirectory = GlobalApplicationContext.getInstance().getStringParameter("localMySQLBinDirectory");
        MySQLWrapper mySQLWrapper = new MySQLWrapper();

        mySQLWrapper.setDataSource(dataSource);
        mySQLWrapper.setMysqlBinDirectory(mysqlBinDirectory);
        mySQLWrapper.setTargetDatabaseName("importtest");
        mySQLWrapper.setImportFileName("d:/testD50936934.sql");
        mySQLWrapper.mysql();
    }
}
