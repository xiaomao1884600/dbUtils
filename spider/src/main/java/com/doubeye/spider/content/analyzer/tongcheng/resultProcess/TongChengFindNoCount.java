package com.doubeye.spider.content.analyzer.tongcheng.resultProcess;

import net.sf.json.JSONObject;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 查找同城网中无count属性的记录，这些记录有可能由于频繁获取数据造成，需要重新获取
 */
public class TongChengFindNoCount {
    private String originFileName;
    private String noCountRecordFileName;

    public void doSearch() throws IOException {
        File originFile = new File(originFileName);
        File targetFile = new File(noCountRecordFileName);
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(originFile)), "utf-8"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            long recordCount = 0, lineNumber = 0;
            String line;
            Set<String> conditions = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                JSONObject jobObject = JSONObject.fromObject(line);
                if (!jobObject.containsKey("count")) {

                    lineNumber ++;

                    String conditionString = jobObject.getString("condition");
                    if (!conditions.contains(conditionString)) {
                        conditions.add(conditionString);
                        writer.write(conditionString + "\n");
                        recordCount ++;
                        if (recordCount % 5000 == 0) {
                            writer.flush();
                        }
                    }
                    if (lineNumber % 5000 == 0) {
                        System.out.println(lineNumber);
                    }
                }
            }
            writer.flush();
        }
    }


    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public void setNoCountRecordFileName(String noCountRecordFileName) {
        this.noCountRecordFileName = noCountRecordFileName;
    }

    public static void main(String[] args) throws IOException {
        TongChengFindNoCount processor = new TongChengFindNoCount();
        processor.setOriginFileName("d:/spider/tongcheng/jobs.txt");
        processor.setNoCountRecordFileName("d:/spider/tongcheng/noCountCondition.txt");
        processor.doSearch();
    }


}
