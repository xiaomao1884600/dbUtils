package com.doubeye.spider.content.analyzer;

import com.doubeye.commons.utils.net.DefaultUrlPageContentGetter;
import com.doubeye.commons.utils.net.UrlPageContentGetter;
import net.sf.json.JSONArray;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.List;

public interface ContentGetter {
    void setUrls(List<String> urls);

    /**
     * 执行抓取
     */
    void doGet();

    /**
     * 执行抓取
     * @param page 开始的页数
     */
    void doGet(int page);

    /**
     * 执行抓取
     * @param url 开始的url
     * @param startPage 开始的页数
     */
    void doGet(String url, int startPage);
    boolean hasMoreOnCurrentPage(JSONArray contents, int currentPage);
    //TODO 将来使用持久器来做
    void persistResult(String fileName, JSONArray contents) throws IOException;
    void setFileName(String fileName);

    /**
     * 设置页面内容获取器，默认为DefaultUrlPageContentGetter，字符集为utf-8
     * @param pageContentGetterPage 页面内容获取器
     */
    void setPageContentGetter(UrlPageContentGetter pageContentGetterPage);

    void setContentAnalyzer(ContentAnalyzer analyzer);

    /**
     * 设置本url共多少页，如果指定的url无法获得总页数，则返回-1
     * @param document HTML Document对象
     */
    void setPageCountOnCurrentUrl(Document document);
    void setPageTemplate(String pageTemplate);

    /**
     * 判断返回内容是否表示进制频繁访问
     * @param document 抓取的内容
     * @return 是否被禁止频繁访问
     */
    boolean hasForbidden(Document document);

    /**
     * 对抓取的一组数据进行后处理
     * @param contents 抓取的内容
     */
    void postProcessContents(JSONArray contents);

    /**
     * 设置两次抓取间隔的时间
     * @param interval 间隔时间，单位毫秒
     */
    void setInterval(int interval);

    /**
     * 获得内容分析器
     * @return 内容分析器
     */
    ContentAnalyzer getContentAnalyzer();


    public static final String LOG_TEMPLATE = "[url = %s] [retrieve takes = %d] [analyze takes = %d] [writes takes = %d] [all takes = %d] [get %d entries] [progress = %f]";
}
