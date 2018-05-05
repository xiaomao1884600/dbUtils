package com.doubeye.spider.content.analyzer.zhaopin;


import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.NoSuchAlgorithmException;

public class ZhaopinSearchResultListContentAnalyzer extends AbstractContentAnalyzer {


    @Override
    public int getCurrentPage() {
        Element element = getDocument().getElementById("c4");
        return Integer.parseInt(element.attr("value"));
    }

    @Override
    public JSONArray doAnalyze() {
        JSONArray result = new JSONArray();
        Elements elements = getDocument().getElementsByClass("newlist");
        for (Element element : elements) {
            JSONObject jobTitle = new JSONObject();
            Elements jobPost = element.getElementsByClass("zwmc").get(0).getElementsByTag("a");
            String post = jobPost.html();
            if (org.apache.commons.lang.StringUtils.isEmpty(post)) {
                continue;
            }
            jobTitle.put("post", post);
            String detailPageUrl = jobPost.attr("href");
            jobTitle.put("detailPageUrl", detailPageUrl);
            String companyTemp = element.getElementsByClass("gsmc").get(0).getElementsByTag("a").html();
            String company;
            if (companyTemp.indexOf("<img") > 0) {
                company = companyTemp.substring(0, companyTemp.indexOf("<img")).replaceAll("\n", "");//jobTitle.put("company", companyTemp.substring(0, companyTemp.indexOf("<img")).replaceAll("\n", ""));
            } else {
                company = companyTemp.replaceAll("\n", "");//jobTitle.put("company", companyTemp.replaceAll("\n", ""));
            }
            jobTitle.put("company", company);
            jobTitle.put("salary", element.getElementsByClass("zwyx").html());
            jobTitle.put("city", element.getElementsByClass("gzdd").html());
            jobTitle.put("postedAt", element.getElementsByClass("gxsj").get(0).getElementsByTag("span").html());
            if (!org.apache.commons.lang.StringUtils.isEmpty(detailPageUrl)) {
                getJobDetail(detailPageUrl, jobTitle);
            }
            jobTitle.put("condition", getAdditionInfo());

            result.add(jobTitle);
            Element sibling = element.nextElementSibling();
            if (sibling != null && sibling.hasClass(HTML_CLASS_RECOMMEND_TIPS)) {
                break;
            }
        }
        return result;
    }

    private static void getJobDetail(String url, JSONObject job) {
        ZhaopinJobDetailPageAnalyzer analyzer = new ZhaopinJobDetailPageAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode(url, "utf-8"));
        JSONArray detail = analyzer.doAnalyze();
        if (detail.size() == 1) {
            JSONObject object = detail.getJSONObject(0);
            JSONUtils.merge(job, object);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        ZhaopinSearchResultListContentAnalyzer analyzer = new ZhaopinSearchResultListContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode("http://sou.zhaopin.com/jobs/searchresult.ashx?bj=200300&sj=398&jl=%E5%8C%97%E4%BA%AC&sm=0&p=1" , "utf-8"));
        System.out.println(analyzer.doAnalyze().toString());

    }

    /**
     * 推荐职位分隔符的类，用来终止获得职位
     */
    private static final String HTML_CLASS_RECOMMEND_TIPS  = "show_recommend_tips";
}
