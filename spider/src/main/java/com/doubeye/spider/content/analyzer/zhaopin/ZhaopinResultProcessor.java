package com.doubeye.spider.content.analyzer.zhaopin;

import com.doubeye.spider.content.analyzer.GeneralSpiderResultProcessor;
import com.doubeye.spider.content.analyzer.UrlIdGetter;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ZhaopinResultProcessor extends GeneralSpiderResultProcessor{
    @Override
    public void beforeSaveRecord(JSONObject jobObject) {
        super.beforeSaveRecord(jobObject);
    }


    public static void main(String[] args) throws IOException {

        String resultFileName = "d:/spider/zhaopin/processedJobs_08_30.txt";
        FileUtils.deleteQuietly(new File(resultFileName));

        GeneralSpiderResultProcessor processor = new ZhaopinResultProcessor();
        UrlIdGetter idGetter = new UrlIdGetter();
        idGetter.setEndWith("htm");
        idGetter.setStartWith(".com/");
        idGetter.setIdPropertyName("detailPageUrl");

        processor.setIdGetter(idGetter);
        processor.addOriginFileName("d:/spider/zhaopin/jobs_08_30.txt");
        processor.setTargetFileName(resultFileName);
        processor.setOriginFileCharset("utf-8");
        processor.removeDuplicate();
    }

}
