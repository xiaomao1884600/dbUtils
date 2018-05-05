package com.doubeye.commons.utils.net;

public class DefaultUrlPageContentGetter implements UrlPageContentGetter{
    private String charset = "utf-8";
    @Override
    public String get(String url) {
        return UrlContentGetter.getHtmlCode(url, charset);
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }
}
