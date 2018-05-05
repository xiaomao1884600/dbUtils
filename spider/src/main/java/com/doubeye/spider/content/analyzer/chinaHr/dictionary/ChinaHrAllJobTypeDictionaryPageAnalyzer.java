package com.doubeye.spider.content.analyzer.chinaHr.dictionary;


import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 用来分析中华英才网所有职位字典页面的页面分析器
 */
public class ChinaHrAllJobTypeDictionaryPageAnalyzer extends AbstractContentAnalyzer {



    /**
     * 存放中华英才网所有职位字典的页面
     */
    private static final String URL = "http://www.chinahr.com/beijing/jobs/";

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
        Elements elements = getDocument().getElementsByClass(CLASSNAME_TOP_LEVEL);
        for (Element element : elements) {
            result.add(Jsoup.parse(element.html()).text());
        }

        return result;
    }

    private JSONArray analyzeSecondLevelJobTypes() {
        JSONArray result = new JSONArray();
        Elements elements = getDocument().getElementsByClass(CLASSNAME_SECOND_LEVEL_ROOT);
        for (Element element : elements) {
            JSONArray secondJobTypeResult = new JSONArray();
            Elements secondLevelJobTypeElements = element.getElementsByClass(CLASSNAME_SECOND_JOB_LEVEL_TITLE);
            for (Element jobTypeElement : secondLevelJobTypeElements) {
                JSONObject secondLevelJob = new JSONObject();
                secondLevelJob.put("name", Jsoup.parse(jobTypeElement.html()).text());
                secondLevelJob.put("jobTypes", getJobTypesUnderSecondLevelJobType(jobTypeElement.nextElementSibling()));
                secondJobTypeResult.add(secondLevelJob);
            }
            result.add(secondJobTypeResult);
        }

        return result;
    }

    private List<String> getJobTypesUnderSecondLevelJobType(Element element) {
        List<String> result = new ArrayList<>();
        if (element != null) {
            Elements elements = element.children();
            for (Element jobTypeElement : elements) {
                result.add(Jsoup.parse(jobTypeElement.html()).text());
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

    private static final String CLASSNAME_TOP_LEVEL = "item-til";
    private static final String CLASSNAME_SECOND_LEVEL_ROOT = "item-con";
    private static final String CLASSNAME_SECOND_JOB_LEVEL_TITLE = "til-con";


    public static void main(String[] args) throws IOException {

    }
}
