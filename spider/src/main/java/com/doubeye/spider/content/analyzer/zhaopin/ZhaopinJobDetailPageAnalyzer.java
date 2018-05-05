package com.doubeye.spider.content.analyzer.zhaopin;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ZhaopinJobDetailPageAnalyzer extends AbstractContentAnalyzer{
    @Override
    public JSONArray doAnalyze() {
        JSONArray result = new JSONArray();
        JSONObject jobDetail = new JSONObject();
        Elements elements = getDocument().getElementsByTag("li");
        for (Element element : elements) {
            String elementContent = element.toString();
            if (elementContent.contains("工作性质")) {
                jobDetail.put("jobType", getStrongTagContent(element));
            } else if (elementContent.contains("工作经验")) {
                jobDetail.put("experience", getStrongTagContent(element));
            } else if (elementContent.contains("最低学历")) {
                jobDetail.put("degree", getStrongTagContent(element));
            } else if (elementContent.contains("招聘人数")) {
                jobDetail.put("count", getStrongTagContent(element).replaceAll("人", ""));
            } else if (elementContent.contains("职位类别：")) {
                jobDetail.put("postTypeFromPage", element.getElementsByTag("a").html());
            } else if (elementContent.contains("公司主页")) {
                jobDetail.put("companyUrl", Jsoup.parse(element.getElementsByTag("strong").html()).text());
            }
        }
        jobDetail.put("condition", getAdditionInfo());
        result.add(jobDetail);
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private String getStrongTagContent(Element element) {
        return element.getElementsByTag("strong").html();
    }


    public static void main(String[] args) throws IOException {
        ContentAnalyzer analyzer = new ZhaopinJobDetailPageAnalyzer();
            analyzer.setContent(UrlContentGetter.getHtmlCode("http://jobs.zhaopin.com/547165126250542.htm" , "utf-8"));
        System.out.println(analyzer.doAnalyze().toString());
    }

}
