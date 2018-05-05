package com.doubeye.spider.content.analyzer.chinaHr.dictionary;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrCompanyDetailContentAnalyzer;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrCompanyUrlGenerator;
import com.doubeye.spider.content.analyzer.chinaHr.ChinaHrJobGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import com.doubeye.spider.content.analyzer.chinaHr.*;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class ChinaHrCompanyGetter extends AbstractJobGetter {
    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return false;
    }

    public static void main(String[] args) throws IOException {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/chinaHr/companies_09_19.txt";
        FileUtils.deleteQuietly(new File(fileName));
        ContentGetter getter = new ChinaHrJobGetter();
        getter.setUrls(ChinaHrCompanyUrlGenerator.getCompanyUrls("D:\\spider\\chinaHr\\processedJobs_09_19.txt.companies"));

        /*
        java.util.List<String> urls = new java.util.ArrayList<>();
        urls.add("http://www.chinahr.com/sou/?orderField=relate&keyword=3D%E5%88%B6%E4%BD%9C&city=27&companyType=0&degree=-1&refreshTime=1&workAge=-1&page=1[@摄影师|||北京");
        getter.setUrls(urls);
        */

        getter.setPageTemplate("");
        getter.setInterval(1500);
        getter.setContentAnalyzer(new ChinaHrCompanyDetailContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }
}
