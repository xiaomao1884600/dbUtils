package com.doubeye.commons.utils.net;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;

public class BrowserSimulatedContentGetter implements UrlPageContentGetter{
    private Map<String, String> header = UrlContentGetter.FIREFOX_HEADER;

    private String proxyHost;
    private int proxyPort;

    @Override
    public String get(String url) throws IOException {
        try {
            if (StringUtils.isEmpty(proxyHost)) {
                return UrlContentGetter.getHtml(url, header);
            } else {
                return UrlContentGetter.getHtml(url, header, proxyHost, proxyPort);
            }
        } catch (IOException e) {
            System.err.println(url);
            throw e;
        }
    }

    @Override
    public void setCharset(String charset) {

    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public static void main(String[] args) throws IOException {
        BrowserSimulatedContentGetter getter = new BrowserSimulatedContentGetter();
        System.out.println(getter.get("http://yongkang.58.com/shejizhitu/?postdate=2017-08-31_2017-09-01&page=1"));
    }
}
