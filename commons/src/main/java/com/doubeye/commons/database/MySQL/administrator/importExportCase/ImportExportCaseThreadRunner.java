package com.doubeye.commons.database.MySQL.administrator.importExportCase;

import com.doubeye.commons.utils.ProgressedRunnable;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/5/14.
 * 以线程的形式运行的导入导出方案
 */
public class ImportExportCaseThreadRunner implements ProgressedRunnable{
    /**
     * 导入导出实际工作的类
     */
    private ImportExportRunner importExportRunner;
    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 构造函数
     * @param importExportRunner 导入导出的工作类
     */
    public ImportExportCaseThreadRunner(ImportExportRunner importExportRunner) {
        this.importExportRunner = importExportRunner;
    }

    @Override
    public float getProgress() {
        return importExportRunner.getProgress();
    }

    @Override
    public String getProgressDescription() {
        return importExportRunner.getDescription();
    }

    @Override
    public long getTotalRunCost() {
        return (System.currentTimeMillis() - startTime / 1000);
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public void run() {
        try {
            startTime = System.currentTimeMillis();
            importExportRunner.run();
        } catch (Exception e) {
            throw new RuntimeException("执行导入导出方案错误，" + e.getMessage(), e);
        }
    }
}
