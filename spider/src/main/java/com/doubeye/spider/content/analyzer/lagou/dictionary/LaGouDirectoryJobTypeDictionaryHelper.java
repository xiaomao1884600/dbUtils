package com.doubeye.spider.content.analyzer.lagou.dictionary;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.lagou.LaGouUrlGenerator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 拉钩网字典助手
 */
public class LaGouDirectoryJobTypeDictionaryHelper extends AbstractContentAnalyzer {

    @Override
    public JSONArray doAnalyze() throws IOException {
        Elements jobTypeElements = getDocument().select(SELECT_JOB_TYPE_ELEMENT);
        JSONArray result = new JSONArray();
        for (Element jobTypeElement : jobTypeElements) {
            JSONObject jobType = new JSONObject();
            jobType.put("parentId", jobTypeElement.attr(ID_PARENT));
            jobType.put("id", jobTypeElement.attr(ID));
            jobType.put("name", Jsoup.parse(jobTypeElement.html()).text());
            jobType.put("urlSection", StringUtils.substringBetween(jobTypeElement.attr("href"), "zhaopin/", "/"));
            result.add(jobType);
        }
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    public static JSONArray getAllJobTypes() {
        String content = UrlContentGetter.getHtmlCode(URL_HOME_PAGE, "utf-8");
        ContentAnalyzer analyzer = new LaGouDirectoryJobTypeDictionaryHelper();
        analyzer.setContent(content);
        try {
            return analyzer.doAnalyze();
        } catch (IOException e) {
            return new JSONArray();
        }
    }

    public static void main(String[] args) throws IOException {
        JSONArray jobTypes = getAllJobTypes();
        for (int i = 0; i < jobTypes.size(); i ++) {
            String jobType = jobTypes.getJSONObject(i).getString("name");
            if (LaGouUrlGenerator.INTERESTED_JOB_SUBTYPE_NAMES.contains(jobType)) {
                jobType += "\ttrue";
            }
            System.out.println(jobType);
        }
    }

    private static final String ID_PARENT = "data-lg-tj-id";
    private static final String ID = "data-lg-tj-no";
    private static final String SELECT_JOB_TYPE_ELEMENT = "div.menu_sub a[data-lg-tj-no]";
    private static final String URL_HOME_PAGE = "https://www.lagou.com/";
}
