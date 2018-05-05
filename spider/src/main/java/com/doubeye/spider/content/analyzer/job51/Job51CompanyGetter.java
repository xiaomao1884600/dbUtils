package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.commons.utils.net.DefaultUrlPageContentGetter;
import com.doubeye.commons.utils.net.UrlPageContentGetter;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrJobGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrCompanyDetailContentAnalyzer;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrJobGetter;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class Job51CompanyGetter extends AbstractJobGetter{
    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return false;
    }

    public static void main(String[] args) throws IOException {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/job51/companies_11_01.txt";
        FileUtils.deleteQuietly(new File(fileName));
        UrlPageContentGetter urlContentGetter = new DefaultUrlPageContentGetter();
        //前程无忧需要将字符集设置为gbk
        urlContentGetter.setCharset("gbk");
        ContentGetter getter = new ChinaHrJobGetter();
        getter.setPageContentGetter(urlContentGetter);
        getter.setUrls(Job51CompanyUrlGenerator.getCompanyUrls("D:\\spider\\job51\\processedJobs_10_24.txt.companies"));

        getter.setPageTemplate("");
        getter.setContentAnalyzer(new Job51CompanyDetailContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }
}
