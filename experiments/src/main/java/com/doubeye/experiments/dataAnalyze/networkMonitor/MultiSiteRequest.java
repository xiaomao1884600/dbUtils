package com.doubeye.experiments.dataAnalyze.networkMonitor;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MultiSiteRequest {
    private JSONArray requestData = new JSONArray();
    private List<String> logFileNames = new ArrayList<>();
    private List<String> keyPropertyNames = new ArrayList<>();
    private Map<String, List<Double>> intermediateResult = new Hashtable<>();

    public void addLogFile(String fileName) {
        logFileNames.add(fileName);
    }

    public void addKeyPropertyName(String keyPropertyName) {
        keyPropertyNames.add(keyPropertyName);
    }

    public void loadData() {
        logFileNames.forEach(fileName -> {
            try {
                requestData.addAll(CollectionUtils.toJSONArray(fileName));
                prepareLoadedData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void prepareLoadedData() {
        Date requestTime = new Date();
        for (int i = 0; i < requestData.size() ; i ++) {
            JSONObject obj = requestData.getJSONObject(i);
            try {
                requestTime = new Date(obj.getLong("request_time") * 1000);
            } catch (Exception e) {
                System.out.println(obj.toString());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(requestTime);
            obj.put("requestHour", DateTimeUtils.getDefaultFormattedDateTime(requestTime, "MM-dd HH"));
        }
    }

    public void computeIntermediate() {
        for (int i = 0; i < requestData.size() ; i ++) {
            JSONObject obj = requestData.getJSONObject(i);
            StringBuilder accumulatedKey = new StringBuilder();
            keyPropertyNames.forEach(element -> {
                accumulatedKey.append(obj.getString(element)).append("_");
                String key = accumulatedKey.substring(0, accumulatedKey.lastIndexOf("_"));
                List<Double> list = null;
                if (intermediateResult.containsKey(key)) {
                    list = intermediateResult.get(key);
                } else {
                    list = new ArrayList<>();
                    intermediateResult.put(key, list);
                }
                list.add(obj.getDouble("time"));
            });
        }

    }

    public JSONArray computeResult() {
        JSONArray result = new JSONArray();
        SummaryStatistics stats = new SummaryStatistics();
        intermediateResult.forEach((key, value) -> {
            JSONObject item = new JSONObject();
            stats.clear();
            value.forEach(stats::addValue);
            item.put("key", key);
            item.put("times", value.size());
            item.put("min", stats.getMin());
            item.put("max", stats.getMax());
            item.put("mean", stats.getMean());
            item.put("stdev", stats.getStandardDeviation());
            result.add(item);
        });
        return result;
    }

    private static void generateButchResult(String originDirectory) throws IOException {
        File path = new File(originDirectory);
        File[] logFiles = path.listFiles();
        if (logFiles != null) {
            for (File logFile : logFiles) {
                String originFileName = logFile.getAbsolutePath();
                String targetFileName = getTargetFile(originFileName);
                MultiSiteRequest multiSiteRequest = new MultiSiteRequest();
                multiSiteRequest.addKeyPropertyName("url");
                multiSiteRequest.addKeyPropertyName("requestHour");
                multiSiteRequest.addLogFile(originFileName);
                multiSiteRequest.loadData();
                multiSiteRequest.computeIntermediate();
                JSONArray result = multiSiteRequest.computeResult();
                JSONUtils.sortString(result, "key");
                com.doubeye.commons.utils.file.FileUtils.toFile(targetFileName, result);
            }
        }
    }

    private static String getTargetFile(String originFileName) {
        return originFileName.replace("log", "result").replace("logs", "result");
    }


    public static void main(String[] args) throws IOException {
        if (true) {
            generateButchResult("D:\\allNetworkMonitorLogs\\log\\zjx-wan");
            return;
        }
        MultiSiteRequest multiSiteRequest = new MultiSiteRequest();
        multiSiteRequest.addKeyPropertyName("url");
        multiSiteRequest.addKeyPropertyName("requestHour");

        /* 谢文豪 2017-07-27-31
        multiSiteRequest.addLogFile("d:/xwh/request_logs_2017-07-27.log");
        multiSiteRequest.addLogFile("d:/xwh/request_logs_2017-07-28.log");
        multiSiteRequest.addLogFile("d:/xwh/request_logs_2017-07-29.log");
        multiSiteRequest.addLogFile("d:/xwh/request_logs_2017-07-30.log");
        multiSiteRequest.addLogFile("d:/xwh/request_logs_2017-07-31.log");
        */
        /* 宰金鑫 2017-07-27-31
        multiSiteRequest.addLogFile("d:/zjx/request_logs_2017-07-27.log");
        multiSiteRequest.addLogFile("d:/zjx/request_logs_2017-07-28.log");
        multiSiteRequest.addLogFile("d:/zjx/request_logs_2017-07-29.log");
        multiSiteRequest.addLogFile("d:/zjx/request_logs_2017-07-30.log");
        multiSiteRequest.addLogFile("d:/zjx/request_logs_2017-07-31.log");
        */

        /*
        multiSiteRequest.addLogFile("d:/xwh_31_1/request_logs_2017-07-31.log");
        multiSiteRequest.addLogFile("d:/xwh_31_1/request_logs_2017-08-01.log");
        */

        /*
        multiSiteRequest.addLogFile("d:/zjx_31_1/net/request_logs_2017-07-31.log");
        multiSiteRequest.addLogFile("d:/zjx_31_1/net/request_logs_2017-08-01.log");
        */

        multiSiteRequest.addLogFile("d:/zjx_08_1_2/net/request_logs_2017-08-01.log");
        multiSiteRequest.addLogFile("d:/zjx_08_1_2/net/request_logs_2017-08-02.log");

        multiSiteRequest.loadData();
        multiSiteRequest.computeIntermediate();
        JSONArray result = multiSiteRequest.computeResult();
        JSONUtils.sortString(result, "key");
        System.out.println(result.toString());
    }
}
