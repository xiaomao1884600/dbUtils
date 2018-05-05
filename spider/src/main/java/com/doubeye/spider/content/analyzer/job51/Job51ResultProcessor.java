package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.spider.content.analyzer.GeneralSpiderResultProcessor;
import com.doubeye.spider.content.analyzer.UrlIdGetter;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrResultProcessor;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧职位处理
 */
public class Job51ResultProcessor extends GeneralSpiderResultProcessor{
    @Override
    public void beforeSaveRecord(JSONObject jobObject) {
        super.beforeSaveRecord(jobObject);
        String province = jobObject.getString("province");
        String city = StringUtils.substringBefore(jobObject.getString("city"), "-");
        String warning = "";
        if (!city.contains(province)) {
            warning += "province,";
        }
        warning = StringUtils.substringBeforeLast(warning, ",");
        jobObject.put("warning", warning);
    }

    public static void main(String[] args) throws IOException {
        String resultFileName = "d:/spider/job51/processedJobs_10_24.txt";
        FileUtils.deleteQuietly(new File(resultFileName));
        GeneralSpiderResultProcessor processor = new ChinaHrResultProcessor();
        UrlIdGetter idGetter = new UrlIdGetter();
        idGetter.setStartWith("com/");
        idGetter.setEndWith(".html");
        idGetter.setIdPropertyName("detailPageUrl");

        processor.setIdGetter(idGetter);
        processor.addOriginFileName("d:/spider/job51/jobs_10_24.txt");
        processor.setTargetFileName(resultFileName);
        processor.setOriginFileCharset("utf-8");
        processor.removeDuplicate();
    }
}
