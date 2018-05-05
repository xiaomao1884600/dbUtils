package com.doubeye.spider.content.analyzer.university.gaoKaoPai;

import com.doubeye.commons.utils.net.BrowserSimulatedContentGetter;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GaoKaoPaiUniversityDetailPageContentAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        JSONObject universityDetail = new JSONObject();

        //在基础信息中获得列表页没有的院校类型
        Element baseInfoRightRootElement = getDocument().select(SELECTOR_BASE_INFO_RIGHT_ROOT_ELEMENT).first();
        if (baseInfoRightRootElement != null) {
            Elements liElements = baseInfoRightRootElement.getElementsByTag("li");
            for (Element item : liElements) {
                if (item.toString().contains("学校类型")) {
                    Element valueElement = item.getElementsByClass("c").first();
                    String type = Jsoup.parse(valueElement.html()).text();
                    universityDetail.put("type", type);
                }
            }
        }
        //右侧介绍信息
        Element rightIntroductionRootElement = getDocument().select(SELECTOR_RIGHT_INTRODUCTION_ROOT_ELEMENT).first();
        Elements introElements = rightIntroductionRootElement.getElementsByTag("li");
        for (Element item : introElements) {
            String elementString = Jsoup.parse(item.html()).text().trim();
            if (item.toString().contains("所处城市：")) {
                String city = StringUtils.substringAfter(elementString, "：");
                universityDetail.put("city", city);
            } else if (item.toString().contains("招生电话：")) {
                String phone = StringUtils.substringAfter(elementString, "：");
                universityDetail.put("phone", phone);
            } else if (item.toString().contains("电子邮箱：")) {
                String email = StringUtils.substringAfter(elementString, "：");
                universityDetail.put("email", email);
            }
        }

        //详细介绍
        Elements introductionRootElements = getDocument().select(SELECTOR_INTRODUCTION_ROOT_ELEMENT);
        universityDetail.put("introduction", Jsoup.parse(introductionRootElements.html()).text().trim());

        //专业
        Element menuRootElement = getDocument().select(SELECTOR_MENU_ROOT_ELEEMNT).first();
        Elements menuItemElements = menuRootElement.getElementsByTag("li");
        String majorUrl = "";
        for (Element menuItemElement : menuItemElements) {
            if (menuItemElement.toString().contains("开设专业")) {
                majorUrl = menuItemElement.getElementsByTag("a").first().attr("href");
                universityDetail.put("majorUrl", majorUrl);
                getMajors(majorUrl, universityDetail);
            }
        }
        result.add(universityDetail);
        return result;
    }

    private void getMajors(String majorUrl, JSONObject universityDetail) throws IOException {
        ContentAnalyzer analyzer = new GaoKaoPaiUniversityMajorPageContentAnalyzer();
        BrowserSimulatedContentGetter contentGetter = new BrowserSimulatedContentGetter();
        contentGetter.setHeader(UrlContentGetter.FIREFOX_HEADER);
        analyzer.setContent(contentGetter.get(majorUrl));
        JSONArray majors = analyzer.doAnalyze();
        universityDetail.put("majors", majors);
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    /**
     * 基础信息
     */
    private static final String SELECTOR_BASE_INFO_RIGHT_ROOT_ELEMENT = "ul.baseInfo_right";
    /**
     * 右侧联系方式地址等根元素
     */
    private static final String SELECTOR_RIGHT_INTRODUCTION_ROOT_ELEMENT = "div.schoolIntro_sideBd";

    private static final String SELECTOR_INTRODUCTION_ROOT_ELEMENT = "div.intro";

    private static final String SELECTOR_MENU_ROOT_ELEEMNT = "ul.menu";
}
