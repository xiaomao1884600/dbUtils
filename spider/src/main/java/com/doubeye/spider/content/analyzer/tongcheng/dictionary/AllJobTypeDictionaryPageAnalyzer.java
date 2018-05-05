package com.doubeye.spider.content.analyzer.tongcheng.dictionary;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AllJobTypeDictionaryPageAnalyzer extends AbstractContentAnalyzer{

    /**
     * 存放中华英才网所有职位字典的页面
     */
    private static final String URL = "http://lz.58.com/job.shtml";

    @Override
    public JSONArray doAnalyze() {
        List<String> topLevelJobTypes = analyzeTopLevelJobTypes();
        JSONArray secondJobTypes = analyzeSecondLevelJobTypes();
        return mergeTopAndSecondLevelJobTypes(topLevelJobTypes, secondJobTypes);
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    /**
     * 获取顶层职位类型
     * @return 顶层职位类型
     */
    private List<String> analyzeTopLevelJobTypes() {
        List<String> result = new ArrayList<>();
        Element jobTypeRootElement = getDocument().getElementById(ID_TOP_ROOT);
        Elements elements = jobTypeRootElement.getElementsByTag("li");
        for (Element element : elements) {
            result.add(Jsoup.parse(element.html()).text());
        }
        return result;
    }

    private JSONArray analyzeSecondLevelJobTypes() {
        JSONArray result = new JSONArray();
        Element secondRootElement = getDocument().getElementById(ID_SECOND_ROOT);
        Elements elements = secondRootElement.getElementsByTag("ul");
        for (Element element : elements) {
            JSONArray secondJobTypeResult = new JSONArray();
            Elements secondLevelJobTypeElements = element.getElementsByTag("li");
            for (Element jobTypeElement : secondLevelJobTypeElements) {
                JSONObject secondLevelJob = new JSONObject();
                Element secondTitleStrongElement = jobTypeElement.getElementsByTag("strong").first();
                secondLevelJob.put("name", Jsoup.parse(secondTitleStrongElement.html()).text());
                Element secondTitleAElement = secondTitleStrongElement.getElementsByTag("a").first();
                secondLevelJob.put("code", secondTitleAElement.attr("leibie"));
                secondLevelJob.put("jobTypes", getJobTypesUnderSecondLevelJobType(jobTypeElement));
                secondJobTypeResult.add(secondLevelJob);
            }
            result.add(secondJobTypeResult);
        }

        return result;
    }

    private JSONArray getJobTypesUnderSecondLevelJobType(Element element) {
        JSONArray result = new JSONArray();
        if (element != null) {
            Elements jobTypeElements = element.getElementsByTag("a");
            for (int i = 1; i < jobTypeElements.size(); i++) {
                Element jobTypeElement = jobTypeElements.get(i);
                JSONObject jobType = new JSONObject();
                jobType.put("name", Jsoup.parse(jobTypeElement.html()).text());
                jobType.put("code", jobTypeElement.attr("leibie"));
                result.add(jobType);
            }
        }

        return result;
    }

    private JSONArray mergeTopAndSecondLevelJobTypes(List<String> firstLevel, JSONArray theOther) {
        JSONArray result = new JSONArray();
        if (firstLevel.size() != theOther.size()) {
            throw new RuntimeException("第一级和第二季职位类型数组大小不符");
        }
        for (int i = 0; i < firstLevel.size(); i ++) {
            JSONObject topJobType = new JSONObject();
            topJobType.put("type", firstLevel.get(i));
            topJobType.put("subtypes", theOther.get(i));
            result.add(topJobType);
        }
        return result;
    }
    private static final String ID_TOP_ROOT = "sidebar-left";
    private static final String ID_SECOND_ROOT = "sidebar-right";

    public static JSONArray getTongChengAllJobTypes() {
        String content = UrlContentGetter.getHtmlCode(URL, "utf-8");
        AllJobTypeDictionaryPageAnalyzer analyzer = new AllJobTypeDictionaryPageAnalyzer();
        try {
            analyzer.setContent(content);
            return analyzer.doAnalyze();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void main(String[] args) {
        System.out.println(AllJobTypeDictionaryPageAnalyzer.getTongChengAllJobTypes());
    }
}
