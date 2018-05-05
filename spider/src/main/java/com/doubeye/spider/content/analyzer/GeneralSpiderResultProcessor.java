package com.doubeye.spider.content.analyzer;

import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrResultProcessor;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrResultProcessor;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public abstract class GeneralSpiderResultProcessor {

    private List<String> originFileNames = new ArrayList<>();
    private String targetFileName;

    private String originFileCharset = "utf-8";
    private String targetFileCharset = "utf-8";

    private RecordIdGetter idGetter;

    private Set<String> jobList = new HashSet<>();
    Map<String, Integer> duplicatedInfo = new HashMap<>();

    private static Logger logger = LogManager.getLogger(GeneralSpiderResultProcessor.class);

    public void removeDuplicate() {
        originFileNames.forEach(originFileName -> {
            try {
                removeDuplicate(originFileName);
            } catch (IOException e) {
                logger.error("为文件" + originFileName + "去重失败," + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void removeDuplicate(String originFileNames) throws IOException {

        JSONObject jobObject = new JSONObject();
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(originFileNames), originFileCharset);
                BufferedReader bufferedReader = new BufferedReader(reader);
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(targetFileName, true), targetFileCharset);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
        ) {
            String line;
            int i = 0;
            int errorCount = 0;
            int duplicatedCount = 0;
            long lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber ++;
                try {
                    jobObject = JSONObject.fromObject(line);
                } catch (Exception e) {
                    System.err.println(line);
                    errorCount ++;
                    if (errorCount >= 10) {
                        break;
                    }
                }
                String jobId = idGetter.getId(jobObject);
                if (!jobList.contains(jobId)) {
                    jobList.add(jobId);
                    String condition = jobObject.getString("condition");
                    String[] conditionArray = condition.split("\\|\\|\\|");
                    jobObject.put("jobType", conditionArray[0]);
                    if (conditionArray.length >= 2) {
                        jobObject.put("province", conditionArray[1]);
                    }
                    beforeSaveRecord(jobObject);
                    bufferedWriter.write(jobObject.toString() + "\n");
                    i ++;
                    if (i % 5000 == 0) {
                        System.out.println("duplicated = " + duplicatedCount);
                        System.out.println("lineNumber = " + lineNumber);
                        bufferedWriter.flush();
                    }
                    onNewEntry(jobObject);
                } else {
                    Integer count;
                    if (duplicatedInfo.containsKey(jobId)) {
                        count = duplicatedInfo.get(jobId);
                        count = count + 1;
                        duplicatedInfo.put(jobId, count);
                    } else {
                        count = 2;
                        duplicatedInfo.put(jobId, count);
                    }
                    duplicatedCount ++;

                    onDuplicatedEntry(jobObject);
                }
            }
            bufferedWriter.flush();
            System.out.println("duplicated = " + duplicatedCount);
            System.out.println("lineNumber = " + lineNumber);
            System.out.println(duplicatedInfo);
            System.out.println(duplicatedInfo.size());
            afterProcessed();
        }
    }

    /**
     * 当发现新的数据记录是调用的方法
     * @param newEntry 新记录对象
     */
    public void onNewEntry(JSONObject newEntry) {

    }
    /**
     * 当发现重复的数据记录是调用的方法
     * @param newEntry 新记录对象
     */
    public void onDuplicatedEntry(JSONObject newEntry) {

    }

    /**
     * 当所有记录处理结束是的回调
     */
    public void afterProcessed() throws IOException {

    }

    /**
     * 在保存没条记录前需要进行的处理
     * todo 做成一个类
     */
    public void beforeSaveRecord(JSONObject jobObject) {
        if (!jobObject.containsKey("postedAt")) {
            jobObject.put("postedAt", "");
        }
        if (!jobObject.containsKey("postTypeFromPage")) {
            jobObject.put("postTypeFromPage", "");
        }
        if (!jobObject.containsKey("companyUrl")) {
            jobObject.put("companyUrl", jobObject.getString("company"));
        }
        if (!jobObject.containsKey("experience")) {
            jobObject.put("experience", "不限");
        }
        if (!jobObject.containsKey("degree")) {
            jobObject.put("degree", "不限");
        }
        if (!jobObject.containsKey("count")) {
            jobObject.put("count", 1);
        }

        if (!jobObject.containsKey("city")) {
            jobObject.put("city", "");
        }

        String jobType = jobObject.getString("jobType");
        String province = jobObject.getString("province");
        String city = StringUtils.substringBefore(jobObject.getString("city"), "-");
        String postTypeFromPage = jobObject.getString("postTypeFromPage");
        String warning = "";
        if (!jobType.equals(postTypeFromPage)) {
            warning += "jobType,";
        }
        if (!city.contains(province)) {
            warning += "province,";
        }
        warning = StringUtils.substringBeforeLast(warning, ",");
        jobObject.put("warning", warning);
    }


    public static void main(String[] args) throws IOException, SQLException {
        String job = "{\"detailPageUrl\":\"http://www.chinahr.com/job/5797344598854147.html\",\"post\":\"火热招聘化妆助理（提供食宿）\",\"postedAt\":\"今天\",\"company\":\"中毓（北京）文化传媒有限公司\",\"city\":\"四川\",\"experience\":\" 应届生\",\"degree\":\"其他\",\"salary\":\"4000-8000\",\"industry\":\"公关/市场/会展\",\"companyProperty\":\"民营/私企\",\"companySize\":\"51－100人\",\"condition\":\"摄影师|||四川\",\"date\":\"2017-09-01\",\"jobType\":\"摄影师\",\"province\":\"四川\",\"postTypeFromPage\":\"\",\"companyUrl\":\"中毓（北京）文化传媒有限公司\",\"count\":1}";
        JSONObject jobObject = JSONObject.fromObject(job);
        ChinaHrResultProcessor processor = new ChinaHrResultProcessor();
        processor.beforeSaveRecord(jobObject);
        System.out.println(jobObject.toString());
    }

    public void addOriginFileName(String originFileName) {
        this.originFileNames.add(originFileName);
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public void setIdGetter(RecordIdGetter idGetter) {
        this.idGetter = idGetter;
    }

    public void setOriginFileCharset(String originFileCharset) {
        this.originFileCharset = originFileCharset;
    }

    protected String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileCharset(String targetFileCharset) {
        this.targetFileCharset = targetFileCharset;
    }

}
