package com.doubeye.commons.etl;

import com.doubeye.commons.utils.ProgressedRunnable;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/8/15.
 */
public class EtlCaseThreadRunner implements ProgressedRunnable {
    private EtlCase etlCase;
    public EtlCaseThreadRunner(EtlCase etlCase) {
        this.etlCase = etlCase;
    }
    @Override
    public void run(){
        try {
            etlCase.runCase();
        } catch (Exception e) {
            throw new RuntimeException("执行Etl方案错误，" + e.getMessage(), e);
        }
    }
    @Override
    public float getProgress() {
        return etlCase.getProgress();
    }

    @Override
    public String getProgressDescription() {
        return etlCase.getProgress() + "";
    }
    @Override
    public long getTotalRunCost() {
        return etlCase.getCostTime();
    }

    @Override
    public int getErrorCount() {
        return 0;
    }
}
