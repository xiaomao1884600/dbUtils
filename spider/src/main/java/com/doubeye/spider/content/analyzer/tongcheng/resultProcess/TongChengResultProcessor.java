package com.doubeye.spider.content.analyzer.tongcheng.resultProcess;

import com.doubeye.spider.content.analyzer.GeneralSpiderResultProcessor;
import com.doubeye.spider.content.analyzer.UrlIdGetter;
import com.doubeye.spider.content.analyzer.GeneralSpiderResultProcessor;
import com.doubeye.spider.content.analyzer.UrlIdGetter;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;

public class TongChengResultProcessor extends GeneralSpiderResultProcessor {
    @Override
    public void beforeSaveRecord(JSONObject jobObject) {
        super.beforeSaveRecord(jobObject);
    }

    public static void main(String[] args) throws IOException {
        String resultFileName = "d:/spider/tongcheng/processedJobs_09_04.txt";
        FileUtils.deleteQuietly(new File(resultFileName));

        GeneralSpiderResultProcessor processor = new TongChengResultProcessor();
        UrlIdGetter idGetter = new UrlIdGetter();
        idGetter.setEndWith("");
        idGetter.setStartWith("");
        idGetter.setIdPropertyName("id");

        processor.setIdGetter(idGetter);
        processor.addOriginFileName("d:/spider/tongcheng/jobs_09_04.txt");
        processor.setTargetFileName(resultFileName);
        processor.setOriginFileCharset("utf-8");
        processor.removeDuplicate();
    }
}
