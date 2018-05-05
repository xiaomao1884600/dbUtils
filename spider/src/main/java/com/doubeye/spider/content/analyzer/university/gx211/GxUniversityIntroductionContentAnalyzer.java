package com.doubeye.spider.content.analyzer.university.gx211;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 中国高校网高校简介页分析
 */
public class GxUniversityIntroductionContentAnalyzer extends AbstractContentAnalyzer {

    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        JSONObject introductionObject = new JSONObject();

        String introduction = ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_INTRODUCTION_ELEMENT);
        introductionObject.put("introduction", introduction.replace("'", ""));

        result.add(introductionObject);
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String SELECTOR_INTRODUCTION_ELEMENT = "div.content";

    public static void main(String[] args) throws IOException {
        String content = UrlContentGetter.getHtmlCode("http://www.gx211.com/collegemanage/collegecontent54_01.shtml" , "utf-8");
        ContentAnalyzer analyzer = new GxUniversityIntroductionContentAnalyzer();
        analyzer.setContent(content);
        JSONArray universities = analyzer.doAnalyze();
        System.out.println(universities.toString());
    }
}
