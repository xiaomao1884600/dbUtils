package com.hxsd.monitor.network;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.string.StringUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.*;

public class NetworkMonitorService {
    /**
     * 获得监控数据
     * @param parameters 参数对象，包括
     *                   url : 监控的url
     *                   startTime : 开始时间
     *                   endTime : 结束时间
     * @return 满足条件的监控数据，格式为
     * axis : 横坐标，根据时间范围计算，JOSNObject
     * data : 返回数据格式为
     *                   label : 监控的数据节点
     *                   data : 该节点的监控数据
     */
    public JSONObject getMonitorData(Map<String, String[]> parameters) {
        String startTime = RequestHelper.getString(parameters, "startTime");
        String endTime = RequestHelper.getString(parameters, "endTime");
        String url = RequestHelper.getString(parameters, "url");
        List<String> axisKeys = computeAxisKeys(startTime, endTime);
        List<String> resultFiles = getFileList(startTime, endTime);

        JSONObject result = ResponseHelper.getSuccessObject();
        result.put("axis", axisKeys);
        result.put("data", generateAllMonitorData(url, axisKeys, resultFiles, MONITOR_NODES));
        return result;
    }

    private static JSONArray generateAllMonitorData(String url, List<String> axisKeys, List<String> fileList, String[][] monitorNodes) {
        JSONArray result = new JSONArray();
        for (String[] monitorNode : monitorNodes) {
            String nodeName = monitorNode[0];
            String resultDirectory = monitorNode[1];
            result.add(generateMonitorData(url, axisKeys, fileList, nodeName, resultDirectory));
        }
        return result;
    }

    private static JSONObject generateMonitorData(String url, List<String> axisKeys, List<String> fileList, String nodeName, String resultDirectory) {
        JSONObject result = new JSONObject();
        double[] means = new double[axisKeys.size()];
        fileList.forEach(file -> {
            String resultFileName = resultDirectory + file;
            File f = new File(resultFileName);
            if (f.exists()) {
                try {
                    fillResultData(url, axisKeys, f, file, means);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            result.put("label", nodeName);
            result.put("data", means);
        });
        return result;
    }

    private static void fillResultData(String url, List<String> axisKeys, File file, String date, double[] data) throws IOException {
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(url)) {
                    JSONObject entry = JSONObject.fromObject(line);
                    String key = entry.getString("key");
                    String time = StringUtils.getStringAfterLast(StringUtils.getStringBefore(date, "-"), "_") + "-" + StringUtils.getStringAfterLast(key, "_");
                    if (axisKeys.contains(time)) {
                        data[axisKeys.indexOf(time)] = entry.getDouble("mean");
                    }
                }
            }
        }
    }

    private static List<String> computeAxisKeys(String startTime, String endTime) {
        return getDateList(startTime, endTime, Calendar.HOUR_OF_DAY, "yyyy-MM-dd HH");
    }

    private static List<String> getFileList(String startTime, String endTime) {
        List<String> dates = getDateList(startTime, endTime, Calendar.DATE, "yyyy-MM-dd");
        for (int i = 0; i < dates.size(); i ++) {
            dates.set(i, String.format(MONITOR_RESULT_FILE_TEMPLATE, dates.get(i)));
        }
        return dates;
    }

    private static List<String> getDateList(String startTime, String endTime, int unit, String format) {
        List<String> keys = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(DateTimeUtils.getDateFromDefaultFormat(startTime));
        Calendar end = Calendar.getInstance();
        end.setTime(DateTimeUtils.getDateFromDefaultFormat(endTime));
        keys.add(DateTimeUtils.getDefaultFormattedDateTime(start.getTime(), format));
        while (start.compareTo(end) <= 0) {
            start.add(unit, 1);
            keys.add(DateTimeUtils.getDefaultFormattedDateTime(start.getTime(), format));
        }
        return keys;
    }

    public static void main(String[] args) {
        Map<String, String[]> parameters = new Hashtable<>();
        parameters.put("url", new String[]{"www.baidu.com"});
        parameters.put("startTime", new String[]{"2017-07-31 15:20:32"});
        parameters.put("endTime", new String[]{"2017-08-01 15:20:32"});
        JSONObject result = new NetworkMonitorService().getMonitorData(parameters);
        System.out.println(result.toString());
    }

    private static final String[][] MONITOR_NODES = {
            {"xwh", "D:/allNetworkMonitorLogs/result/xwh/"},
            {"zjx-wan", "D:/allNetworkMonitorLogs/result/zjx-wan/"},
            {"zjx-net", "D:/allNetworkMonitorLogs/result/zjx-net/"},
    };

    private static final String MONITOR_RESULT_FILE_TEMPLATE = "request_results_%s.result";
}
