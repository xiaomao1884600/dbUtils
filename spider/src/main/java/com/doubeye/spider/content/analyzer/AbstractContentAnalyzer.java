package com.doubeye.spider.content.analyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author doubeye
 * @version 1.0,1
 * 内容分析器抽象类，实现保留内容，document，分析用的而外信息
 * history
 *  1.0.1
 *      ! 添加实现Catchable接口
 */
abstract public class AbstractContentAnalyzer implements ContentAnalyzer, Captchable{
    protected boolean ignoreDetail = false;
    protected String content;
    protected Document document;
    protected String url;
    private String additionInfo;

    @Override
    public void setContent(String content) {
        this.content = content;
        document = Jsoup.parse(content);
    }
    @Override
    public Document getDocument() {
        return this.document;
    }

    protected String getAdditionInfo() {
        return additionInfo;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void setAdditionInfo(String additionInfo) {
        this.additionInfo = additionInfo;
    }

    @Override
    public boolean needCaptcha(Document document) {
        return false;
    }

    @Override
    public String getCaptchaContentUrl(Document document) {
        return "";
    }

    @Override
    public void setIgnoreDetail(boolean ignoreDetail) {
        this.ignoreDetail = ignoreDetail;
    }
}
