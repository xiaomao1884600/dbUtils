package com.doubeye.spider.content.analyzer.university.gaoKaoPai;

import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.net.BrowserSimulatedContentGetter;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 用于分析高考派大学列表的内容分析器
 */
public class GaoKaoPaiUniversityListContentAnalyzer extends AbstractContentAnalyzer {
    private static Logger logger = LogManager.getLogger(GaoKaoPaiUniversityListContentAnalyzer.class);
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Elements universitiesRootElements = getDocument().select(SELECTOR_UNIVERSITIES_ROOT_ELEMENT);
        Elements universityRootElements = universitiesRootElements.select(SELECTOR_UNIVERSITY_ROOT_ELEMENTS);
        for (Element universityRootElement : universityRootElements) {
            JSONObject universityObject = new JSONObject();
            try {
                //学校名称和URL
                Element universityNameElement = universityRootElement.select(SELECTOR_UNIVERSITY_NAME_ROOT_ELEMENT).select("a").first();
                String universityName = Jsoup.parse(universityNameElement.html()).text();
                String universityUrl = universityNameElement.attr("href");
                universityObject.put("name", universityName);
                universityObject.put("url", universityUrl);
                //隶属和重点学科数
                Element belongsToElement = universityRootElement.select(SELECTOR_BELONG_TO_ELEMENT).first();
                String belongsToHtmlString = Jsoup.parse(belongsToElement.html()).text().trim();
                String belongsTo = StringUtils.substringBetween(belongsToHtmlString, "隶属：", "重点学科数：");
                String keyDisciplines = StringUtils.substringBetween(belongsToHtmlString, "重点学科数：", "个");
                universityObject.put("belongsTo", belongsTo);
                universityObject.put("keyDisciplines", keyDisciplines);
                //硕士点和博士点数
                Element masterStationsElement = universityRootElement.select(SELECTOR_MASTER_STATIONS_ELEMENT).first();
                String masterStationsHtmlString = Jsoup.parse(masterStationsElement.html()).text().trim();
                String masterStations = StringUtils.substringBetween(masterStationsHtmlString, "硕士点数：", "个");
                String doctorStations = StringUtils.substringBetween(masterStationsHtmlString, "博士点数：", "个");
                universityObject.put("masterStations", masterStations);
                universityObject.put("doctorStations", doctorStations);
                //tags
                Element tagsRootElement = universityRootElement.select(SELECTOR_TAGS_ELEMENT).first();
                if (tagsRootElement != null) {
                    processTags(tagsRootElement, universityObject);
                }

                //院校详情
                getUniversityDetail(universityUrl, universityObject);

                result.add(universityObject);
            } catch (Exception e) {
                System.err.println(universityObject.toString());
            }
        }
        return result;
    }

    /**
     * 获得院校详情页
     * @param universityUrl 详情页url
     * @param universityObject 院校对象
     */
    private void getUniversityDetail(String universityUrl, JSONObject universityObject) throws IOException {
        ContentAnalyzer analyzer = new GaoKaoPaiUniversityDetailPageContentAnalyzer();
        BrowserSimulatedContentGetter contentGetter = new BrowserSimulatedContentGetter();
        contentGetter.setHeader(UrlContentGetter.FIREFOX_HEADER);
        analyzer.setContent(contentGetter.get(universityUrl));
        JSONArray detail = analyzer.doAnalyze();
        if (detail.size() == 1) {
            JSONObject object = detail.getJSONObject(0);
            JSONUtils.merge(universityObject, object);
        }
    }

    private void processTags(Element tagRootElement, JSONObject universityObject) {
        Elements images = tagRootElement.getElementsByTag("img");
        for (Element image : images) {
            universityObject.put(image.attr("title"), true);
        }
    }

    @Override
    public int getCurrentPage() {
        Elements currentPageElements = getDocument().select(SELECTOR_CURRENT_PAGE);
        if (currentPageElements.size() > 0) {
            return Integer.parseInt(Jsoup.parse(currentPageElements.first().html()).text());
        } else {
            logger.trace(getDocument());
            return 0;
        }
    }

    /**
     * 分页器
     */
    private static final String SELECTOR_CURRENT_PAGE = "span.current";
    /**
     * 所有大学的根元素
     */
    private static final String SELECTOR_UNIVERSITIES_ROOT_ELEMENT = "ul.slist";
    /**
     * 每所大学的根元素
     */
    private static final String SELECTOR_UNIVERSITY_ROOT_ELEMENTS = "li.clearfix";
    /**
     * 学校名称根元素
     */
    private static final String SELECTOR_UNIVERSITY_NAME_ROOT_ELEMENT = "div.tit";
    /**
     * 隶属和重点学科
     */
    private static final String SELECTOR_BELONG_TO_ELEMENT = "div.s1";
    /**
     * 硕士点和博士点
     */
    private static final String SELECTOR_MASTER_STATIONS_ELEMENT = "div.s2";
    /**
     *
     */
    private static final String SELECTOR_TAGS_ELEMENT = "div.tag";

    public static void main(String[] args) throws IOException {
        GaoKaoPaiUniversityListContentAnalyzer analyzer = new GaoKaoPaiUniversityListContentAnalyzer();
        BrowserSimulatedContentGetter contentGetter = new BrowserSimulatedContentGetter();
        contentGetter.setHeader(UrlContentGetter.FIREFOX_HEADER);
        analyzer.setContent(contentGetter.get("http://www.gaokaopai.com/daxue-0-0-0-0-0-0-0--p-115.html"));
        System.out.println(analyzer.doAnalyze());
    }
}
