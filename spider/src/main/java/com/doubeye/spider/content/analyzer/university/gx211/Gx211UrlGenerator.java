package com.doubeye.spider.content.analyzer.university.gx211;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 生成中国高校之窗url的工具类
 */
public class Gx211UrlGenerator {
    private static final String URL = "http://www.gx211.com/gxmd/%s";

    private static final Set<String> provinces = new HashSet<>();
    static {
        provinces.add("gx-bj.html");
        provinces.add("gx-tj.html");
        provinces.add("gx-heb.html");
        provinces.add("gx-shx.html");
        provinces.add("gx-ln.html");
        provinces.add("gx-jl.html");
        provinces.add("gx-hlj.html");
        provinces.add("gx-sh.html");
        provinces.add("gx-js.html");
        provinces.add("gx-zj.html");
        provinces.add("gx-ah.html");
        provinces.add("gx-fj.html");
        provinces.add("gx-jx.html");
        provinces.add("gx-sd.html");
        provinces.add("gx-hen.html");
        provinces.add("gx-hb.html");
        provinces.add("gx-hn.html");
        provinces.add("gx-gd.html");
        provinces.add("gx-nm.html");
        provinces.add("gx-gx.html");
        provinces.add("gx-hain.html");
        provinces.add("gx-cq.html");
        provinces.add("gx-sc.html");
        provinces.add("gx-gz.html");
        provinces.add("gx-yn.html");
        provinces.add("gx-xz.html");
        provinces.add("gx-sx.html");
        provinces.add("gx-gs.html");
        provinces.add("gx-qh.html");
        provinces.add("gx-nx.html");
        provinces.add("gx-xj.html");
    }

    public static List<String> getUrls() {
        List<String> urls = new ArrayList<>();
        provinces.forEach(
                url -> urls.add(String.format(URL, url))
        );
        return urls;
    }

    public static List<String> getUrls(String city) {
        List<String> urls = new ArrayList<>();
        provinces.forEach(url -> {
            if (url.contains(city)) {
                urls.add(String.format(URL, url));
            }
        });
        return urls;
    }

    public static void main(String[] args) {
        System.out.println(getUrls().size());
    }
}
