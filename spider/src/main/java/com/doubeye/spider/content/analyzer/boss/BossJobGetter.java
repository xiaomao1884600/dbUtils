package com.doubeye.spider.content.analyzer.boss;

import com.doubeye.commons.utils.net.BrowserSimulatedContentGetter;
import com.doubeye.commons.utils.net.UrlContentGetter;

import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;


import java.io.File;

public class BossJobGetter extends AbstractJobGetter{
    /**
     * 目前看58同城每页返回记录条数不一定的
     * @param contents 职位数组
     * @param currentPage 当前页数
     * @return 是否还有更多的页数
     */
    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        //this.
        return currentPage < MAX_PAGE_NUMBER && contents.size() == JOB_PER_PAGE;
    }

    @Override
    public boolean hasForbidden(Document document) {
        return document.toString().contains(TOO_FREQUENT);
    }

    private static final int MAX_PAGE_NUMBER = 30;

    private static final int JOB_PER_PAGE = 15;

    private static final String TOO_FREQUENT = "由于您当前网络访问页面过于频繁";

    public static void main(String[] args) {
        String fileName = "d:/spider/boss/jobs.txt";

        FileUtils.deleteQuietly(new File(fileName));
        ContentGetter getter = new BossJobGetter();
        getter.setUrls(BossUrlGenerator.getInterestedJobTypeCityUrls());

        /*
        java.util.List<String> urls = new java.util.ArrayList<>();
        //满满30页
        //urls.add("https://www.zhipin.com/c101010100/h_101010100/?query=JAVA");
        //无结果
        //urls.add("https://www.zhipin.com/job_detail/?query=COO&scity=101290500");
        //不满1页
        urls.add("https://www.zhipin.com/job_detail/?query=COO&scity=101080100");
        getter.setUrls(urls);
           */

        BrowserSimulatedContentGetter contentGetter = new BrowserSimulatedContentGetter();
        contentGetter.setHeader(UrlContentGetter.FIREFOX_HEADER);
        // contentGetter.setProxyHost("75.151.213.85");
        // contentGetter.setProxyPort(8080);
        getter.setPageContentGetter(contentGetter);
        //getter.setInterval(70000);

        getter.setPageTemplate(BossUrlGenerator.PAGE_TEMPLATE);
        getter.setContentAnalyzer(new BossSearchResultListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }
}
