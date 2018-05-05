package com.doubeye.core.scheduler;

/*
import com.doubeye.commons.application.ApplicationCache;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.spider.contentAnalyzer.AbstractJobGetter;
import com.doubeye.spider.contentAnalyzer.tongcheng.TongChengJogGetter;
import com.doubeye.spider.contentAnalyzer.tongcheng.TongChengSearchResultListContentAnalyzer;
import com.doubeye.spider.contentAnalyzer.tongcheng.TongChengUrlGenerator;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;
*/

public class TestService {
    /*
    public JSONObject runTongchengJobGetter(Map<String, String[]> parameter) {
        String fileName = "d:/spider/tongcheng/jobs_09_05.txt";
        FileUtils.deleteQuietly(new File(fileName));
        AbstractJobGetter getter = new TongChengJogGetter();
        getter.setUrls(TongChengUrlGenerator.getInterestedJobTypeCityUrls(true));

        getter.setPageTemplate(TongChengUrlGenerator.PAGE_TEMPLATE);
        getter.setContentAnalyzer(new TongChengSearchResultListContentAnalyzer());
        getter.setFileName(fileName);
        new Thread(getter).start();

        ApplicationCache.getInstance().addTask("58同城招聘数据抓取", getter);

        return ResponseHelper.getSuccessObject();

    }
    */
}
