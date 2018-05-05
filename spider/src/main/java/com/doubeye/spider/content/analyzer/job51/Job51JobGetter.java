package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.commons.utils.net.DefaultUrlPageContentGetter;
import com.doubeye.commons.utils.net.UrlPageContentGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentGetter;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧职位获取器
 */
public class Job51JobGetter extends AbstractJobGetter {
    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return currentPage < getPageCountOnCurrentUrl();
    }

    @Override
    public void setPageCountOnCurrentUrl(Document document) {
        int totalPage = -1;
        Elements pageRootElements = document.select(Job51ResultListContentAnalyzer.SELECTOR_PAGE_ROOT_ELEMENT);
        if (pageRootElements.size() > 0) {
            Elements currentPageElements = pageRootElements.select(SELECTOR_TOTAL_PAGE_ELEMENT);
            for (Element element : currentPageElements){
                String content = Jsoup.parse(element.html()).text();
                if (content.contains("共") && content.contains("页")) {
                    totalPage = Integer.parseInt(StringUtils.substringBetween(content,"共", "页"));
                    break;
                }
            }
            setPageCountOnCurrentUrl(totalPage);
        } else {
            setPageCountOnCurrentUrl(-1);
        }
    }

    private static final String SELECTOR_TOTAL_PAGE_ELEMENT = "span.td";


    public static void main(String[] args) {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/job51/jobs_10_24.txt";
        FileUtils.deleteQuietly(new File(fileName));

        UrlPageContentGetter urlContentGetter = new DefaultUrlPageContentGetter();
        //前程无忧需要将字符集设置为gbk
        urlContentGetter.setCharset("gbk");

        ContentGetter getter = new Job51JobGetter();

        getter.setPageContentGetter(urlContentGetter);

        getter.setUrls(Job51UrlGenerator.getInterestedJobTypeCityUrls(Job51UrlGenerator.ADDED_TIME_LAST_3_DAYS));

        getter.setPageTemplate(Job51UrlGenerator.PAGE_TEMPLATE);
        getter.setContentAnalyzer(new Job51ResultListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }
}
