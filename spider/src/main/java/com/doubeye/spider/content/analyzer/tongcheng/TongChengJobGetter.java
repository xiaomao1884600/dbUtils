package com.doubeye.spider.content.analyzer.tongcheng;


import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;

import net.sf.json.JSONArray;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;


public class TongChengJobGetter extends AbstractJobGetter{

    /**
     * 目前看58同城每页返回记录条数不一定的
     * @param contents 职位数组
     * @param currentPage 当前页数
     * @return 是否还有更多的页数
     */
    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return (currentPage < getPageCountOnCurrentUrl()) && (currentPage < MAX_PAGE_NUMBER) &&
                (getContentAnalyzer().getDocument().getElementsByClass(TongChengSearchResultListContentAnalyzer.CLASS_NAME_NO_DATA).size() == 0);
    }



    @Override
    public void setPageCountOnCurrentUrl(Document document) {
        Elements maxPageElements = document.getElementsByClass(CLASS_NAME_TOTAL_PAGE);
        if (maxPageElements != null && maxPageElements.size() > 0) {
            Element maxPageElement = maxPageElements.first();
            setPageCountOnCurrentUrl(Integer.parseInt(maxPageElement.html()));
        } else {
            setPageCountOnCurrentUrl(0);
        }
    }




    private static final int MAX_PAGE_NUMBER = 70;
    private static final String CLASS_NAME_TOTAL_PAGE = "total_page";

    public static void main(String[] args) {

        //String fileName = "d:/spider/tongcheng/jobs.txt";
        String fileName = "d:/spider/tongcheng/jobs_09_19.txt";
        FileUtils.deleteQuietly(new File(fileName));
        ContentGetter getter = new TongChengJobGetter();
        getter.setUrls(TongChengUrlGenerator.getInterestedJobTypeCityUrls(true));

        /*
        java.util.List<String> urls = new java.util.ArrayList<>();
        urls.add("http://bj.58.com/wangzhanmeigong/?postdate=20170831_20170901[@a|||b");
        getter.setUrls(urls);
        */

        /*
        GenerateRetryUrls processor = new GenerateRetryUrls();
        processor.setFileName("D:/spider/tongcheng/noCountCondition.txt");
        getter.setUrls(processor.getUrlsNeedToRetry());
        */

        getter.setPageTemplate(TongChengUrlGenerator.PAGE_TEMPLATE);
        getter.setContentAnalyzer(new TongChengSearchResultListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }


}
