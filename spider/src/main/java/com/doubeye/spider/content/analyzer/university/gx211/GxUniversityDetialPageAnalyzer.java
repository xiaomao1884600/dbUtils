package com.doubeye.spider.content.analyzer.university.gx211;

import com.doubeye.commons.utils.net.JsoupContentGetter;
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

public class GxUniversityDetialPageAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        JSONObject detailInfo = new JSONObject();
        //介绍详情页
        String introductionPageUrl = ContentAnalyzer.getFirstParsedElementPropertyBySelector(getDocument(), SELECTOR_INTRODUCTION_PAGE_ELEMENT, "href");
        introductionPageUrl = String.format(TEMPLATE_INTRODUCTION_PAGE_URL, introductionPageUrl);
        detailInfo.put("introductionPageUrl", introductionPageUrl);
        if (!StringUtils.isEmpty(introductionPageUrl)) {
            processIntroductionPage(introductionPageUrl, detailInfo);
        }
        //专业设置页
        String majorUrl = ContentAnalyzer.getElementAttribute(getDocument().getElementById(ID_MAJOR_ELEMENT), "href");
        majorUrl = String.format(TEMPLATE_MAJOR_PAGE_URL, majorUrl);
        detailInfo.put("majorPageUrl", majorUrl);
        if (!StringUtils.isEmpty(majorUrl)) {
            processMajorPage(majorUrl, detailInfo);
        }
        //联系方式
        Elements contactsElements = getDocument().select(SELECTOR_CONTACTS_ELEMENT);
        processContacts(contactsElements, detailInfo);

        result.add(detailInfo);
        return result;
    }

    /**
     * 处理联系方式
     * @param contactsElement 联系方式根元素
     * @param detailInfo 高校详情信息
     */
    private void processContacts(Elements contactsElement, JSONObject detailInfo) {
        for (Element element : contactsElement) {
            String content = ContentAnalyzer.getElementContent(element).trim();
            if (content.contains(IDENTIFIER_ADDRESS)) {
                String[] contents = content.split(SEPARATOR_BLANK);
                for (String item : contents) {
                    if (item.startsWith(IDENTIFIER_ADDRESS)) {
                        detailInfo.put("address", StringUtils.substringAfter(item, SEPARATOR_COLOR));
                    } else if (item.startsWith(IDENTIFIER_ZIP_CODE)) {
                        detailInfo.put("zipCode", StringUtils.substringAfter(item, SEPARATOR_COLOR));
                    } else if (item.startsWith(IDENTIFIER_PHONE)) {
                        detailInfo.put("phone", StringUtils.substringAfter(item, SEPARATOR_COLOR));
                    }
                }
            }
        }
    }

    /**
     * 处理简介页信息
     * @param introductionUrl 简介也url
     * @param detailInfo 详情对象
     */
    private void processIntroductionPage(String introductionUrl, JSONObject detailInfo) throws IOException {
        try {
            //String introductionContent = UrlContentGetter.getHtmlCode(introductionUrl, "utf-8");
            JsoupContentGetter contentGetter = new JsoupContentGetter();
            String introductionContent = contentGetter.get(introductionUrl);
            ContentAnalyzer analyzer = new GxUniversityIntroductionContentAnalyzer();
            analyzer.setContent(introductionContent);
            JSONArray introductionArray = analyzer.doAnalyze();
            if (introductionArray.size() > 0) {
                JSONObject introduction = introductionArray.getJSONObject(0);
                detailInfo.putAll(introduction);
            }
        } catch (Exception e) {
            System.out.println("抓取url失败: " + introductionUrl);
            //throw e;
        }

    }

    /**
     * 处理开设专业页
     * @param majorUrl 专业页url
     * @param detailInfo 详情对象
     */
    private void processMajorPage(String majorUrl, JSONObject detailInfo) throws IOException {
        try {
            //String content = UrlContentGetter.getHtmlCode(majorUrl, "utf-8");
            JsoupContentGetter contentGetter = new JsoupContentGetter();
            String majorContent = contentGetter.get(majorUrl);
            ContentAnalyzer analyzer = new GxMajorPageContentAnalyzer();
            analyzer.setContent(majorContent);
            analyzer.setUrl(majorUrl);
            JSONArray majorArray = analyzer.doAnalyze();
            if (majorArray.size() > 0) {
                JSONObject major = majorArray.getJSONObject(0);
                detailInfo.putAll(major);
            }
        } catch (IOException e) {
            System.out.println("抓取url失败: " + majorUrl);
            //throw e;
        }
    }


    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String TEMPLATE_INTRODUCTION_PAGE_URL = "http://www.gx211.com/collegemanage/%s";
    private static final String TEMPLATE_MAJOR_PAGE_URL = "http://www.gx211.com/collegemanage/%s";

    private static final String ID_MAJOR_ELEMENT = "a11";
    private static final String SELECTOR_CONTACTS_ELEMENT = "ul.ListContent li.content";

    private static final String SELECTOR_INTRODUCTION_PAGE_ELEMENT = "li.Text h2 a";

    private static final String SEPARATOR_BLANK = " ";
    private static final String SEPARATOR_COLOR = "：";
    private static final String IDENTIFIER_ADDRESS = "地址";
    private static final String IDENTIFIER_ZIP_CODE = "邮编";
    private static final String IDENTIFIER_PHONE = "电话";


    public static void main(String[] args) throws IOException {
        String content = UrlContentGetter.getHtmlCode("http://www.gx211.com/collegemanage/college54_01.shtml" , "utf-8");
        ContentAnalyzer analyzer = new GxUniversityDetialPageAnalyzer();
        analyzer.setContent(content);
        JSONArray universities = analyzer.doAnalyze();
        System.out.println(universities.toString());
    }

}
