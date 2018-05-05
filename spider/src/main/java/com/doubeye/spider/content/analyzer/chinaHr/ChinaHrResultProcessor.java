package com.doubeye.spider.content.analyzer.chinaHr;


import com.doubeye.spider.content.analyzer.GeneralSpiderResultProcessor;
import com.doubeye.spider.content.analyzer.UrlIdGetter;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class ChinaHrResultProcessor extends GeneralSpiderResultProcessor{

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
        String resultFileName = "d:/spider/chinaHr/processedJobs_09_04.txt";
        FileUtils.deleteQuietly(new File(resultFileName));
        GeneralSpiderResultProcessor processor = new ChinaHrResultProcessor();
        UrlIdGetter idGetter = new UrlIdGetter();
        idGetter.setStartWith("job/");
        idGetter.setEndWith(".html");
        idGetter.setIdPropertyName("detailPageUrl");

        processor.setIdGetter(idGetter);
        processor.addOriginFileName("d:/spider/chinaHr/jobs_09_04.txt");
        processor.setTargetFileName(resultFileName);
        processor.setOriginFileCharset("utf-8");
        processor.removeDuplicate();
    }
}
