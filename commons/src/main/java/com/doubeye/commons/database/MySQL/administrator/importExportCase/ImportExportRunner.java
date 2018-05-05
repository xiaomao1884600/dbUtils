package com.doubeye.commons.database.MySQL.administrator.importExportCase;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.MySQL.administrator.DumpWrapper;
import com.doubeye.commons.database.MySQL.administrator.MySQLWrapper;
import com.doubeye.commons.database.connection.bean.DataSource;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.runtime.RuntimeRunningResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/5/4.
 * 导入导出运行起
 * TODO 删除文件，下载文件，日志，仅导出
 */
public class ImportExportRunner {
    private static final String STATUS_NOT_STARTED = "尚未开始";
    private static final String STATUS_EXPORTING = "正在导出";
    private static final String STATUS_EXPORTED = "导出结束";
    private static final String STATUS_IMPORTING = "正在导入";
    private static final String STATUS_IMPORTED = "导入结束";
    private static final String STATUS_FINISHED = "导入结束";

    /**
     * 载入导入导出配置
     * @param caseId 方案编号
     * @param tmpDir 导出文件的目录
     * @throws SQLException SQL异常
     */
    public void init(int caseId, String tmpDir) throws Exception {
        importExportCase.setId(caseId);
        importExportCase.setConn(conn);
        importExportCase.initById();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        DataSource originDataSource = ConnectionManager.getDataSourceById(importExportCase.getOriginDatasourceId(), encryptKey);
        DataSource targetDataSource = ConnectionManager.getDataSourceById(importExportCase.getTargetDatasourceId(), encryptKey);
        String originSchema = importExportCase.getOriginDatabase();
        String mysqlBinDirectory = GlobalApplicationContext.getInstance().getStringParameter("localMySQLBinDirectory");

        String fileName = String.format(FILE_NAME_TEMPLATE, tmpDir, caseId, System.currentTimeMillis());

        dump.setDataSource(originDataSource);
        dump.setOriginSchema(originSchema);
        dump.setMysqlBinDirectory(mysqlBinDirectory);
        dump.setDumpFileName(fileName);
        dump.setTableNames(importExportCase.getOriginTableNames());


        mysql.setDataSource(targetDataSource);
        mysql.setMysqlBinDirectory(mysqlBinDirectory);
        mysql.setTargetDatabaseName(importExportCase.getTargetDatabase());
        mysql.setTargetDatabaseName(importExportCase.getTargetDatabase());
        mysql.setImportFileName(fileName);

    }

    /**
     * 运行导入导出方案
     * @throws IOException IO异常
     * @throws InterruptedException 中断异常
     */
    public void run() throws IOException, InterruptedException {
        status = STATUS_EXPORTING;
        try {
            dumpResult = dump.mysqlDump();
            if (onlyExport) {
                progress = 100f;
                status = STATUS_FINISHED;
                return;
            }
            status = STATUS_EXPORTED;
            progress = 50f;
            status = STATUS_IMPORTING;
            mysqlResult = mysql.mysql();
            status = STATUS_IMPORTED;
            status = STATUS_FINISHED;
            progress = 100f;
        } finally {
            status = STATUS_FINISHED;
            progress = 100f;
        }
    }

    /**
     * 导入导出方案
     */
    private ImportExportCaseEntity importExportCase = new ImportExportCaseEntity();
    /**
     * 数据库连接
     */
    private Connection conn;

    @SuppressWarnings("unused")
    public ImportExportCaseEntity getImportExportCase() {
        return importExportCase;
    }
    @SuppressWarnings("unused")
    public void setImportExportCase(ImportExportCaseEntity importExportCase) {
        this.importExportCase = importExportCase;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * 导出
     */
    private DumpWrapper dump = new DumpWrapper();
    /**
     * 导入
     */
    private MySQLWrapper mysql = new MySQLWrapper();
    /**
     * 导出运行结果
     */
    private RuntimeRunningResult dumpResult;
    /**
     * 导入运行结果
     */
    private RuntimeRunningResult mysqlResult;
    /**
     * 仅执行导入工作
     */
    private boolean onlyExport = false;
    /**
     * 运行进度
     */
    private float progress = 0f;
    /**
     * 已执行的时间
     */
    private long costTime = 0;
    /**
     * 运行状态
     */
    private String status = STATUS_NOT_STARTED;

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
    @SuppressWarnings("unused")
    public long getCostTime() {
        return costTime;
    }
    @SuppressWarnings("unused")
    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }
    @SuppressWarnings("unused")
    public RuntimeRunningResult getDumpResult() {
        return dumpResult;
    }
    @SuppressWarnings("unused")
    public void setDumpResult(RuntimeRunningResult dumpResult) {
        this.dumpResult = dumpResult;
    }
    @SuppressWarnings("unused")
    public RuntimeRunningResult getMysqlResult() {
        return mysqlResult;
    }
    @SuppressWarnings("unused")
    public void setMysqlResult(RuntimeRunningResult mysqlResult) {
        this.mysqlResult = mysqlResult;
    }
    @SuppressWarnings("unused")
    public boolean isOnlyExport() {
        return onlyExport;
    }

    public void setOnlyExport(boolean onlyExport) {
        this.onlyExport = onlyExport;
    }

    /**
     * 获得运行状态描述
     * @return 运行状态描述
     */
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        if (status.equals(STATUS_NOT_STARTED) || status.equals(STATUS_EXPORTING)) {
            buffer.append(status);
        } else if (status.equals(STATUS_EXPORTED) || status.equals(STATUS_IMPORTING) || (status.equals(STATUS_FINISHED) && onlyExport)) {
            buffer.append(status).append("\n");
            buffer.append("导出运行结果为：\n");
            buffer.append(getRunningResult(dumpResult));
        } else if (status.equals(STATUS_IMPORTED) || (status.equals(STATUS_FINISHED) && !onlyExport)) {
            buffer.append(status).append("\n");
            buffer.append("导出运行结果为：\n");
            buffer.append(getRunningResult(dumpResult));
            buffer.append("导入运行结果为：\n");
            buffer.append(getRunningResult(mysqlResult));
        }
        return buffer.toString();
    }

    /**
     * 获得运行结果
     * @param runtimeRunningResult 运行结果
     * @return 运行结果
     */
    @NotNull
    public static String getRunningResult(RuntimeRunningResult runtimeRunningResult) {
        StringBuilder buffer = new StringBuilder();
        if (runtimeRunningResult.getRunningResult()) {
            buffer.append("成功\n");
        } else {
            buffer.append("失败\n");
        }
        if (runtimeRunningResult.getMessage() != null && runtimeRunningResult.getMessage().size() > 0) {
            buffer.append("输出为：\n");
            buffer.append(CollectionUtils.split(runtimeRunningResult.getMessage(), "\n", 5));
        }
        if (runtimeRunningResult.getErrorMessage() != null && runtimeRunningResult.getErrorMessage().size() > 0) {
            buffer.append("错误输出为：\n");
            buffer.append(CollectionUtils.split(runtimeRunningResult.getErrorMessage(), "\n", 5));
        }
        return buffer.toString();
    }

    /**
     * 导出文件名模板
     */
    private static final String FILE_NAME_TEMPLATE = "%s/case_%s_%s.sql";
}
