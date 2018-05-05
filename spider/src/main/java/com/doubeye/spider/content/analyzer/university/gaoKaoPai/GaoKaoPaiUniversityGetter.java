package com.doubeye.spider.content.analyzer.university.gaoKaoPai;

import com.doubeye.commons.utils.log.LogUtils;
import com.doubeye.commons.utils.net.BrowserSimulatedContentGetter;
import com.doubeye.commons.utils.net.UrlContentGetter;
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
 * 高校帮高校获取器
 */
public class GaoKaoPaiUniversityGetter extends AbstractJobGetter {
    @Override
    public boolean hasMoreOnCurrentPage(JSONArray content, int currentPage) {
        return currentPage < getPageCountOnCurrentUrl();
    }

    @Override
    public void setPageCountOnCurrentUrl(Document document) {
        Elements pageInfoElements = document.select(SELECTOR_PAGE_INFO_ELEMENT);
        if (pageInfoElements.size() > 0) {
            Element pageInfoElement = pageInfoElements.first();
            String content = StringUtils.substringBetween(Jsoup.parse(pageInfoElement.html()).text(), "/", "页").trim();
            setPageCountOnCurrentUrl(Integer.parseInt(content));
        } else {
            setPageCountOnCurrentUrl(0);
        }
    }

    public static void main(String[] args) {
        LogUtils.setLogLevel(Level.TRACE);
        String fileName = "d:/spider/gaoKaoPai/universities.txt";
        FileUtils.deleteQuietly(new File(fileName));
        ContentGetter getter = new GaoKaoPaiUniversityGetter();
        getter.setUrls(GaoKaoPaiUrlGenerator.getUrls());

        getter.setPageTemplate(GaoKaoPaiUrlGenerator.PAGE_TEMPLATE);
        BrowserSimulatedContentGetter contentGetter = new BrowserSimulatedContentGetter();
        contentGetter.setHeader(UrlContentGetter.FIREFOX_HEADER);
        getter.setPageContentGetter(contentGetter);
        getter.setInterval(1000);

        getter.setContentAnalyzer(new GaoKaoPaiUniversityListContentAnalyzer());
        getter.setFileName(fileName);
        getter.doGet();
    }

    private static final String SELECTOR_PAGE_INFO_ELEMENT = "span.pageInfo";
}
