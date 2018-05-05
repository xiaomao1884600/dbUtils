package com.doubeye.spider.content.analyzer.lagou;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.commons.utils.net.JsoupContentGetter;
import com.doubeye.commons.utils.net.UrlPageContentGetter;
import com.doubeye.spider.content.analyzer.AbstractJobGetter;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentGetter;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;

import java.io.File;

/**
 * @author doubeye
 * @version 1.0.0
 * 拉钩职位获取器
 */
public class LaGouJobGetter extends AbstractJobGetter{

    @Override
    public boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage) {
        return currentPage < getPageCountOnCurrentUrl();
    }

    @Override
    public void setPageCountOnCurrentUrl(Document document) {
        int totalPage;
        String totalPageString = ContentAnalyzer.getFirstParsedElementContentBySelector(document, SELECTOR_TOTAL_PAGE_ELEMENT);
        try {
            totalPage = Integer.parseInt(totalPageString);
        } catch (NumberFormatException e) {
            totalPage = -1;
        }
        setPageCountOnCurrentUrl(totalPage);
    }

    private static final String SELECTOR_TOTAL_PAGE_ELEMENT = "div.page-number span.totalNum";


    public static void main(String[] args) {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/laGou/jobs_11_03.txt";
        FileUtils.deleteQuietly(new File(fileName));

        UrlPageContentGetter urlContentGetter = new JsoupContentGetter();

        ContentGetter getter = new LaGouJobGetter();

        getter.setPageContentGetter(urlContentGetter);

        getter.setUrls(LaGouUrlGenerator.getInterestedJobTypeCityUrls());

        getter.setInterval(61000);

        getter.setPageTemplate(LaGouUrlGenerator.PAGE_TEMPLATE);
        getter.setContentAnalyzer(new LagouResultListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet("https://www.lagou.com/zhaopin/shijueshejishi/", 0);
    }
}
