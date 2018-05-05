package com.doubeye.commons.utils.net;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsoupContentGetter implements UrlPageContentGetter{
    private String charset;
    @Override
    public String get(String url) throws IOException {
        Map<String, String> header = new HashMap<>();

        header.put("Cache-Control", "no-cache");
        header.put("Connection", "keep-alive");
        //header.put("Host", "www.lagou.com");
        header.put("REQUEST_ID", "keep-alive");
        //header.put("Referer", "https://www.lagou.com/zhaopin/shijueshejishi/3/");
        header.put("X-Anit-Forge-Code", "0");
        header.put("X-Anit-Forge-Token", "None");
        header.put("X-Requested-With", "XMLHttpRequest");

        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "zh-CN,zh;q=0.8");


        header.put("Pragma", "no-cache");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        //header.put("Cookie", cookie);

        return Jsoup.connect(url).headers(header).timeout(120000).followRedirects(true).execute().parse().toString();
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }
    //Document doc = Jsoup.connect("https://www.tianyancha.com/search?key=Klein&checkFrom=searchBox&rnd=").headers(header).timeout(120000).followRedirects(true).execute().parse();
}
