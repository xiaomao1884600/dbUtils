package com.doubeye.commons.database.MySQL.administrator;


import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.runtime.RuntimeRunner;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.runtime.RuntimeRunningResult;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/11.
 * MySQLDump 执行类
 */
public class DumpWrapper extends MySQLCommand{
    Logger logger = LogManager.getLogger(DumpWrapper.class);
    /**
     * 数据源数据库名称
     */
    private String originSchema;
    /**
     * 导出的文件名称
     */
    private String dumpFileName;
    /**
     * 导出的数据库表列表，如果没有指定，则导出整库
     */
    private String tableNames = "";

    /**
     * 执行导出操作
     * @return 导出执行结果
     * @throws IOException IO异常
     * @throws InterruptedException 中断异常
     */
    public RuntimeRunningResult mysqlDump() throws IOException, InterruptedException {
        return RuntimeRunner.runCommand(getCommandLine());
    }

    /**
     * 获得导出的命令
     * @return 导出命令
     */
    private String[] getCommandLine() {
        return RuntimeRunner.getOSShellCommand(String.format(System.getProperty("os.name").toLowerCase().startsWith("win") ? WINDOWS_MYSQL_DUMP_COMMAND : LINUX_MYSQL_DUMP_COMMAND, mysqlBinDirectory, dataSource.getHost(), dataSource.getPort(), getOriginSchema(), tableNames, dataSource.getUserName(), dataSource.getPassword(), dumpFileName));
    }

    /**
     * 导出命令模板
     */
    private static final String WINDOWS_MYSQL_DUMP_COMMAND = "\"%s/mysqldump\" --host=%s --port=%s %s %s --user=%s --password=%s --set-gtid-purged=OFF  --extended-insert=FALSE --quick --skip-lock-tables > %s";
    private static final String LINUX_MYSQL_DUMP_COMMAND = "%s/mysqldump --host=%s --port=%s %s %s --user=%s --password=%s --extended-insert=FALSE --quick --skip-lock-tables > %s";
    @SuppressWarnings("unused")
    public String getDumpFileName() {
        return dumpFileName;
    }

    public void setDumpFileName(String dumpFileName) {
        this.dumpFileName = dumpFileName;
    }
    @SuppressWarnings("unused")
    public String getTableNames() {
        return tableNames;
    }
    @SuppressWarnings("WeakerAccess")
    public String getOriginSchema() {
        return StringUtils.isEmpty(originSchema) ? dataSource.getDefaultSchema() : originSchema;
    }

    public void setOriginSchema(String originSchema) {
        this.originSchema = originSchema;
    }

    /**
     * 设置导出的表名
     * @param tableNames 表名列表，格式为JSONArray
     */
    public void setTableNames(String tableNames) {
        try {
            JSONArray tableNamesArray = JSONArray.fromObject(tableNames);
            this.tableNames = CollectionUtils.split(tableNamesArray, " ");
        } catch (Exception e) {
            this.tableNames = tableNames;
        }
    }

    /*
     * todo 发放到UNITTEST


    public static void main(String[] args) throws IOException, InterruptedException {
        ApplicationContextInitiator.init();
        DataSource dataSource = GlobalApplicationContext.getInstance().getCoreDataSource();
        String mysqlBinDirectory = GlobalApplicationContext.getInstance().getStringParameter("localMySQLBinDirectory");
        DumpWrapper dumpWrapper = new DumpWrapper();

        dumpWrapper.setDataSource(dataSource);
        dumpWrapper.setMysqlBinDirectory(mysqlBinDirectory);
        dumpWrapper.setDumpFileName("d:/testDump_" + System.currentTimeMillis() + ".sql");
        RuntimeRunningResult result = dumpWrapper.mysqlDump();
        System.out.println(ImportExportRunner.getRunningResult(result));
    }
     */
}
