package com.doubeye.spider.content.analyzer.chinaHr;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;

import net.sf.json.JSONArray;

import org.apache.commons.io.FileUtils;

import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;


public class ChinaHrJobGetter extends AbstractJobGetter{

    @Override
    public boolean hasMoreOnCurrentPage(JSONArray content, int currentPage) {
        return currentPage < getPageCountOnCurrentUrl();
    }

    @Override
    public void setPageCountOnCurrentUrl(Document document) {
        Elements pageListElements = document.getElementsByClass(CLASS_NAME_PAGE_LIST);
        if (pageListElements.size() > 0) {
            Element pageListElement = document.getElementsByClass(CLASS_NAME_PAGE_LIST).first();
            Elements elements = pageListElement.getElementsByTag("a");
            if (elements.size() <= 1) {
                setPageCountOnCurrentUrl(1);
            } else {
                setPageCountOnCurrentUrl(Integer.parseInt(elements.get(elements.size() - 2).html()));
            }
        } else {
            setPageCountOnCurrentUrl(0);
        }
    }

    private static final String CLASS_NAME_PAGE_LIST = "pageList";

    public static void main(String[] args) {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/chinaHr/jobs_09_05_test.txt";
        FileUtils.deleteQuietly(new File(fileName));
        ContentGetter getter = new ChinaHrJobGetter();
        getter.setUrls(ChinaHrUrlGenerator.getInterestedJobTypeCityUrls(true));


        java.util.List<String> urls = new java.util.ArrayList<>();
        urls.add("http://www.chinahr.com/sou/?orderField=relate&keyword=3D%E5%88%B6%E4%BD%9C&city=27&companyType=0&degree=-1&refreshTime=1&workAge=-1&page=1[@摄影师|||北京");
        getter.setUrls(urls);


        getter.setPageTemplate(ChinaHrUrlGenerator.PAGE_TEMPLATE);
        getter.setContentAnalyzer(new ChinaHrSearchResultListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }
}
