package com.doubeye.spider.content.analyzer.university.gx211;

import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 中国高校网专业页分析器
 */
public class GxMajorPageContentAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Elements trElements = getDocument().select(SELECTOR_MAJOR_ROW_ELEMENTS);
        JSONArray majors = processMajor(trElements);
        JSONObject majorObject = new JSONObject();
        majorObject.put("majors", majors);
        result.add(majorObject);
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private JSONArray processMajor(Elements trElements) {
        JSONArray majors = new JSONArray();
        for (Element tr : trElements) {
            Elements tdElements = tr.getElementsByTag("td");
            JSONObject major = new JSONObject();
            try {
                //专业
                String majorName = ContentAnalyzer.getElementContent(tdElements.get(0));
                major.put("majorName", majorName);
                //专业大类
                String majorTypeFunction = tdElements.get(1).children().first().html();
                String majorTypeId = StringUtils.substringBetween(majorTypeFunction, "'", "'");
                String majorTypeName = GxMajorDictionary.MAJOR_TYPE.getString(majorTypeId);
                major.put("majorType", majorTypeName);
                //专业小类
                String majorSubtypeFunction = tdElements.get(2).children().first().html();
                String majorSubtypeId = StringUtils.substringBetween(majorSubtypeFunction, "'", "'");
                String majorSubtypeName = GxMajorDictionary.getMajorSubtype(majorSubtypeId);
                major.put("majorSubtype", majorSubtypeName);
                majors.add(major);
            } catch (Exception e) {
                System.err.println(getUrl());
                e.printStackTrace();
            }

        }
        return majors;
    }

    static String formatMajors(JSONArray majorArray) {
        JSONObject result = new JSONObject();
        for (int i = 0; i < majorArray.size(); i ++) {
            JSONObject majorObject = majorArray.getJSONObject(i);
            String path = majorObject.getString("majorType") + "/" + majorObject.getString("majorSubtype");
            String value = majorObject.getString("majorName");
            JSONUtils.appendValue(result, path, value, "/");
        }
        return JSONUtils.format(result, "\t");
    }

    private static final String SELECTOR_MAJOR_ROW_ELEMENTS = "table tr.trs";

    public static void main(String[] args) throws IOException {
        String content = UrlContentGetter.getHtmlCode("http://www.gx211.com/collegemanage/content1437_11.shtml" , "utf-8");
        ContentAnalyzer analyzer = new GxMajorPageContentAnalyzer();
        analyzer.setContent(content);
        JSONArray majors = analyzer.doAnalyze();
        System.out.println(majors.toString());
        FileUtils.toFile("d:/majors.text", new StringBuilder().append(formatMajors(majors.getJSONObject(0).getJSONArray("majors"))));

    }
}
