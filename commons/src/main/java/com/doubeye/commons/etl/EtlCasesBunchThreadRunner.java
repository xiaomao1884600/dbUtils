package com.doubeye.commons.etl;

import com.doubeye.commons.utils.ProgressedRunnable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/8/15.
 */
public class EtlCasesBunchThreadRunner implements ProgressedRunnable {
    private List<EtlCase> etlCases;
    private String progressDescription = "";
    private EtlCase currentEtlCase;
    private float progressOverall = 0;
    private int index = 0;
    private JSONArray exceptions = new JSONArray();
    private JSONArray runResults = new JSONArray();

    public long getTotalRunCost() {
        return totalRunCost;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    private long totalRunCost = 0;

    public EtlCasesBunchThreadRunner(List<EtlCase> etlCases) {
        this.etlCases = etlCases;
    }

    @Override
    public void run() {

        for (int i = 0; i < etlCases.size(); i++) {
            EtlCase etlCase = etlCases.get(i);
            currentEtlCase = etlCase;
            try {
                etlCase.runCase();
                JSONObject runResult = new JSONObject();
                runResult.put(etlCase.getCaseName(), etlCase.getCostTime());
                totalRunCost += etlCase.getCostTime();
                runResults.add(runResult);
            } catch (Exception e) {
                JSONObject exception = new JSONObject();
                exception.put("name", etlCase.getCaseName() == null ? "没有获得etl，可能原因为指定了不正确的etl方案id" : etlCase.getCaseName());
                exception.put("info", e.getStackTrace());
                exceptions.add(exception);
                e.printStackTrace();
            }
            progressOverall = (i + 1) * 100 / etlCases.size() ;
            index = i + 1;
            totalRunCost += etlCase.getCostTime();
        }
        System.err.println(runResults.toString());
        if (exceptions.size() > 0) {
            System.err.println(exceptions.toString());
        } else {
            System.err.println("所有任务顺利完成");
        }
    }

    @Override
    public float getProgress() {
        if ((currentEtlCase == null) && (index == etlCases.size() - 1)) {
            return 100;
        } else {
            return progressOverall;
        }
    }

    @Override
    public String getProgressDescription() {
        if (currentEtlCase != null) {
            return "总体进度为" + progressOverall + ", 当前正在运行" + etlCases.size() + "个任务中的第" + index + "个，其进度为" + currentEtlCase.getProgress() + "，整体运行了" + totalRunCost + "秒";
        } else {
            return "所有任务已经运行完成，或者没有开始任何任务";
        }
    }
}
