package com.doubeye.log.analyzer;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


import java.io.*;

import java.util.*;

public class EControllerLogFileAnalyzer {

    public static JSONObject processLogEntry(String line) {
        JSONObject result = new JSONObject();
        List<String> entry = CollectionUtils.split(line, " \\| ");

        result.put("id", entry.get(0));
        result.put("request_action", entry.get(1));
        result.put("request_data", entry.get(2));
        result.put("userid", entry.get(3));
        result.put("ip", entry.get(4));
        result.put("sendtime", entry.get(5));
        result.put("request_starttime", entry.get(6));
        result.put("request_endtime", entry.get(7));

        return result;
    }

    public static String processLogEntryOnlyConvertTimestamp(String line) {
        Calendar calendar = Calendar.getInstance();
        String firstTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringAfterLast(line, " | ").trim();
        calendar.setTime(new Date(Long.parseLong(firstTimestamp) * 1000));
        String firstTime = DateTimeUtils.getDefaultFormattedDateTime(calendar.getTime());
        String otherThenTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringBeforeLast(line, " |");
        String secondTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringAfterLast(otherThenTimestamp, "| ").trim();
        calendar.setTime(new Date(Long.parseLong(secondTimestamp) * 1000));
        String secondTime = DateTimeUtils.getDefaultFormattedDateTime(calendar.getTime());
        otherThenTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringBeforeLast(otherThenTimestamp, " |");
        return otherThenTimestamp + " | " + secondTime + " | " + firstTime;
    }

    public static String processLogEntryOnlyConvertTimestampAppend(String line) {
        Calendar calendar = Calendar.getInstance();
        String firstTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringAfterLast(line, " | ").trim();
        calendar.setTime(new Date(Long.parseLong(firstTimestamp) * 1000));
        String firstTime = DateTimeUtils.getDefaultFormattedDateTime(calendar.getTime());
        String otherThenTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringBeforeLast(line, " |");
        String secondTimestamp = com.doubeye.commons.utils.string.StringUtils.getStringAfterLast(otherThenTimestamp, "| ").trim();
        calendar.setTime(new Date(Long.parseLong(secondTimestamp) * 1000));
        String secondTime = DateTimeUtils.getDefaultFormattedDateTime(calendar.getTime());
        return line + " | " + secondTime + " | " + firstTime + " | " + (Long.parseLong(firstTimestamp) - Long.parseLong(secondTimestamp));
    }


    private static void compareSlowAndNormal() throws IOException {
        String noSlowFileName = "d:/requestlog_20170728.txt";
        String slowFileName = "d:/requestlog_20170731.txt";
        int noSlowDate = 28;
        int slowDate = 31;

        JSONArray noSlowResult = compute(noSlowFileName, noSlowDate);
        JSONArray slowResult = compute(slowFileName, slowDate);

        JSONArray results = new JSONArray();

        for (int i = 0; i < slowResult.size(); i ++) {
            JSONObject slow = slowResult.getJSONObject(i);
            JSONObject noSlow = null;
            for (int j = 0; j < noSlowResult.size(); j ++) {
                noSlow = noSlowResult.getJSONObject(j);
                if (slow.getString("url").equals(noSlow.getString("url"))) {

                    String suspicious = "";
                    if (slow.getInt("times") > noSlow.getInt("times")) {
                        suspicious += "times,";
                    }
                    if (slow.getInt("mean") > noSlow.getInt("mean")) {
                        System.err.println("mean happens");
                        suspicious += "mean,";
                    }
                    if (slow.getInt("max") > noSlow.getInt("max")) {
                        suspicious += "max,";
                    }
                    if (slow.getInt("stdev") > noSlow.getInt("stdev")) {
                        suspicious += "stdev,";
                    }

                    if (!StringUtils.isEmpty(suspicious)) {
                        JSONObject result = new JSONObject();
                        result.put("nowSlow", noSlow);
                        result.put("slow", slow);
                        result.put("suspicious", suspicious);
                        results.add(result);
                    }
                    break;
                }
            }
            if (noSlow == null) {
                JSONObject result = new JSONObject();
                result.put(slow.getString("url"), "not in noslow");
                results.add(result);
            }
        }
        System.out.println("------------final result goes from here");
        System.out.println(results);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(processLogEntryOnlyConvertTimestamp("1ABD17ED-4443-23E4-F560-F2D7ACB8476C | App\\Http\\Controllers\\Remind\\RemindController@anyGetnoreadassign | {\"v\":\"1500566445523\",\"data\":{\"userid\":\"524\"},\"type\":\"2\",\"token\":\"jzakkklrftgdv07b483348c9ce17e5206941718d8e31d\"} | 524 | 10.2.20.163 | 0 | 1500566409 | 1500566412"));
    }
    private static JSONArray compute(String fileName, int date) throws IOException {
        //fileName = "d:/requestlog_20170731.txt";
        long slowStartTime = DateTimeUtils.getDateFromDefaultFormat("2017-07-" + date + " 14:43:00").getTime();
        long slowEndTime = DateTimeUtils.getDateFromDefaultFormat("2017-07-" + date + " 14:47:00").getTime();


        List<String> users = new ArrayList<>();
        Map<String, List<Double>> urlData = new Hashtable<>();

        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            String line;
            long totalRequestCounts = 0;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> entry = CollectionUtils.split(line, " \\| ");
                // System.out.println(entry.toString());
                try {
                    String url = entry.get(1);
                    String userId = entry.get(3);
                    long startTimeLong = Long.parseLong(entry.get(6)) * 1000;
                    long endTimeLong = Long.parseLong(entry.get(7)) * 1000;
                    Date startTime = new Date(startTimeLong);
                    int requestHour = Integer.parseInt(DateTimeUtils.getDefaultFormattedDateTime(startTime, "mm"));
                    double cost = (endTimeLong - startTimeLong) / 1000;

                    if (startTimeLong >= slowStartTime && startTimeLong <= slowEndTime) {
                        totalRequestCounts ++;
                        if (!users.contains(userId)) {
                            users.add(userId);
                        }
                        List<Double> data = null;
                        if (urlData.containsKey(url)) {
                            data = urlData.get(url);
                        } else {
                            data = new ArrayList<>();
                            urlData.put(url, data);
                        }
                        data.add(cost);
                    }
                } catch (Exception e) {
                    System.out.println(totalRequestCounts + "    " + line);
                }

            }

            JSONArray result = new JSONArray();
            SummaryStatistics stats = new SummaryStatistics();
            urlData.forEach((key, value) -> {
                JSONObject item = new JSONObject();
                stats.clear();
                value.forEach(stats::addValue);
                item.put("url", key);
                item.put("times", value.size());
                item.put("min", stats.getMin());
                item.put("max", stats.getMax());
                item.put("mean", stats.getMean());
                item.put("stdev", stats.getStandardDeviation());
                result.add(item);
            });
            JSONUtils.sort(result, "times");
            return result;
        }
    }
}
