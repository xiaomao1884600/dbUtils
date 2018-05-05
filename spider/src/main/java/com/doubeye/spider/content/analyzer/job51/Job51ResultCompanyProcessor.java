package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.spider.content.analyzer.GeneralSpiderResultProcessor;
import com.doubeye.spider.content.analyzer.UrlIdGetter;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrResultCompanyProcessor;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧公司处理类
 */
public class Job51ResultCompanyProcessor extends GeneralSpiderResultProcessor {
    private Map<String, JSONObject> companies = new HashMap<>();
    @Override
    public void beforeSaveRecord(JSONObject jobObject) {

    }
    @Override
    public void onNewEntry(JSONObject newEntry) {
        companies.put(newEntry.getString("companyUrl"), newEntry);
    }

    @Override
    public void afterProcessed() throws IOException {
        String targetFileName = getTargetFileName() + ".companies";
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(targetFileName, true), "utf-8");
             BufferedWriter bufferedWriter = new BufferedWriter(writer);) {
            int index = 0;
            Set<String> keySet = companies.keySet();
            for (String key : keySet) {
                index ++;
                JSONObject company = companies.get(key);
                bufferedWriter.write(company.toString() + "\n");
                if ((index % 1000) == 0) {
                    bufferedWriter.flush();
                }
            }
            bufferedWriter.flush();
        }
    }
    @Override
    public void onDuplicatedEntry(JSONObject newEntry) {
        JSONObject oldEntry = companies.get(newEntry.getString("companyUrl"));
        JSONObject savedEntry = oldEntry;
        String post = oldEntry.getString("post");
        post += "," + newEntry.getString("post");
        savedEntry.put("post", post);
        companies.put(newEntry.getString("companyUrl"), savedEntry);
    }

    public static void main(String[] args) throws IOException {
        String resultFileName = "d:/spider/job51/processedJobs_10_24.txt";
        //FileUtils.deleteQuietly(new File(resultFileName));
        FileUtils.deleteQuietly(new File(resultFileName + ".companies"));
        GeneralSpiderResultProcessor processor = new Job51ResultCompanyProcessor();
        UrlIdGetter idGetter = new UrlIdGetter();
        idGetter.setStartWith("");
        idGetter.setEndWith(".html");
        idGetter.setIdPropertyName("companyUrl");

        processor.setIdGetter(idGetter);
        processor.addOriginFileName("d:/spider/job51/processedJobs_10_24.txt");
        processor.setTargetFileName(resultFileName);
        processor.setOriginFileCharset("utf-8");
        processor.removeDuplicate();
    }
}
