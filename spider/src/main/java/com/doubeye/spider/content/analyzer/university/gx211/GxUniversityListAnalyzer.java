package com.doubeye.spider.content.analyzer.university.gx211;

import com.doubeye.commons.utils.net.JsoupContentGetter;
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
 * 中国高校之窗省份高校列表页面分析器
 */
public class GxUniversityListAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        for (int i = 0; i <= TABLE_SIZE; i ++) {
            Element tableElement = getDocument().getElementById(String.format(TEMPALTE_TABLE_ID, i));
            result.addAll(analyzeTable(tableElement));
        }
        return result;
    }

    private JSONArray analyzeTable(Element tableElement ) {
        JSONArray result = new JSONArray();
        Elements trs = tableElement.getElementsByTag("tr");
        for (Element tr : trs) {
            if (!tr.hasAttr("style")) {
                JSONObject universityObject = processUniversity(tr);
                result.add(universityObject);
            }
        }
        return result;
    }

    /**
     * 处理高校列表中的一行
     * @param rowElement 行元素（tr元素）
     * @return 高校对象
     */
    private JSONObject processUniversity(Element rowElement) {
        JSONObject university = new JSONObject();
        Elements cells = rowElement.getElementsByTag("td");
        try {
            //名称列
            Element nameElement = cells.get(0);
            String universityName = ContentAnalyzer.getFirstParsedElementContentBySelector(nameElement, "a");
            String universityUrl = ContentAnalyzer.getFirstParsedElementPropertyBySelector(nameElement,"a", "href");
            String property = ContentAnalyzer.getFirstParsedElementContentBySelector(nameElement, "span");
            university.put("universityName", universityName);
            university.put("universityUrl", universityUrl);
            university.put("property", property);
            if (StringUtils.isEmpty(universityName)) {
                universityName = StringUtils.substringAfter(ContentAnalyzer.getElementContent(nameElement).trim(), " ");
                university.put("universityName", universityName);
            }
            if (!StringUtils.isEmpty(universityUrl)) {
                processUniversityDetailPage(universityUrl, university);
            }
            //主管部门
            Element managerElement = cells.get(1);
            String manager = ContentAnalyzer.getElementContent(managerElement);
            university.put("manager", manager);
            //所在地
            Element cityElement = cells.get(2);
            String city = ContentAnalyzer.getElementContent(cityElement);
            university.put("city", city);
            //层次
            Element levelElement = cells.get(3);
            String level = ContentAnalyzer.getElementContent(levelElement);
            university.put("level", level);
            //层次
            Element typeElement = cells.get(4);
            String type = ContentAnalyzer.getElementContent(typeElement);
            university.put("type", type);
            //网址
            Element officialSiteElement = cells.get(5);
            String officialSite = ContentAnalyzer.getElementContent(officialSiteElement);
            university.put("officialSite", officialSite);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return university;
    }

    /**
     * 处理并合并高校详情也
     * @param universityUrl 详情页url
     * @param university 高校JSONObject
     */
    private void processUniversityDetailPage(String universityUrl, JSONObject university) throws IOException {
        try {
            //String content = UrlContentGetter.getHtmlCode(universityUrl, "utf-8");
            JsoupContentGetter contentGetter = new JsoupContentGetter();
            String universityContent = contentGetter.get(universityUrl);
            ContentAnalyzer analyzer = new GxUniversityDetialPageAnalyzer();
            analyzer.setContent(universityContent);
            JSONArray universities = analyzer.doAnalyze();
            if (universities.size() > 0) {
                JSONObject universityDetailObject = universities.getJSONObject(0);
                university.putAll(universityDetailObject);
            }
        } catch (IOException e) {
            System.out.println("抓取url失败: " + universityUrl);
            //throw e;
        }
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String TEMPALTE_TABLE_ID = "Div%d";
    private static final int TABLE_SIZE = 4;

    public static void main(String[] args) throws IOException {
        //String content = UrlContentGetter.getHtmlCode("http://www.gx211.com/gxmd/gx-bj.html" , "utf-8");
        JsoupContentGetter contentGetter = new JsoupContentGetter();
        String content = contentGetter.get("http://www.gx211.com/gxmd/gx-hb.html");
        ContentAnalyzer analyzer = new GxUniversityListAnalyzer();
        analyzer.setContent(content);
        JSONArray universities = analyzer.doAnalyze();
        System.out.println(universities.toString());
    }
}
