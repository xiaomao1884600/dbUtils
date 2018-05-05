package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.bean.CompanyBean;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.1
 * 前程无忧公司详情页分析器
 *  history
 *      1.0.1
 *          + 按JOB公司库添加抓取信息
 *          ! 使用CompanyBean暂存公司信息
 *          ! 规范区分公司属性的标示符为常量
 */
public class Job51CompanyDetailContentAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();


        CompanyBean bean = new CompanyBean();

        Elements companyNameElements = getDocument().select(SELECTOR_COMPANY_NAME_ELEMENT);
        if (companyNameElements.size() > 0) {
            bean.setCompany(companyNameElements.first().attr(PROPERTY_NAME_TITLE));

            Elements infoElements = getDocument().select(SELECTOR_COMPANY_REAL_URL_ADDRESS_ELEMENT);
            for (Element infoElement : infoElements) {
                String content = Jsoup.parse(infoElement.html()).text();
                if (content.contains(IDENTIFIER_ADDRESS)) {
                    bean.setAddress(StringUtils.substringAfter(content, IDENTIFIER_ADDRESS));
                } else if (content.contains(IDENTIFIER_OFFICIAL_SITE)) {
                    bean.setRealUrl(StringUtils.substringAfter(content, IDENTIFIER_OFFICIAL_SITE));
                }
            }
        }

        Elements companyInfoElements = getDocument().select(SELECTOR_COMPANY_INFO_ELEMENT);
        processCompanyInfo(companyInfoElements, bean);

        bean.setIntroduction(ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_COMPOANY_INTRODUCTION));

        bean.setCompanyUrl(getUrl());

        JSONObject companyObject = JSONObject.fromObject(bean);
        result.add(companyObject);
        return result;
    }

    private void processCompanyInfo(Elements elements, CompanyBean bean) {
        if (elements.size() > 0) {
            //公司基本信息类似如下：民营公司   |  150-500人   |  家居/室内设计/装潢 建筑/建材/工程， 用|分隔公司属性，公司规模和行业
            String contents = ContentAnalyzer.getElementContent(elements.first()).trim();
            String[] contentArray = contents.split(SEPARATOR_COMPANY_PROPERTY);
            if (contentArray.length >= 1) {
                bean.setProperty(contentArray[INDEX_PROPERTY].trim().replace(CHARACTER_BLANK, ""));
            }
            if (contentArray.length >= 2) {
                bean.setSize(contentArray[INDEX_SIZE].trim().replace(CHARACTER_BLANK, ""));
            }
            if (contentArray.length >= 3) {
                bean.setIndustry(contentArray[INDEX_INDUSTRY].trim().replace(CHARACTER_BLANK, ""));
            }
        }
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String SELECTOR_COMPANY_REAL_URL_ADDRESS_ELEMENT = "p.fp";
    private static final String SELECTOR_COMPANY_NAME_ELEMENT = "div.tHCop h1";
    private static final String SELECTOR_COMPANY_INFO_ELEMENT = "p.ltype";
    private static final String SELECTOR_COMPOANY_INTRODUCTION = "div.con_msg div.in";

    private static final String PROPERTY_NAME_TITLE = "title";


    private static final String IDENTIFIER_ADDRESS = "公司地址：";
    private static final String IDENTIFIER_OFFICIAL_SITE = "公司官网：";


    private static final String SEPARATOR_COMPANY_PROPERTY = "\\|";
    private static final String CHARACTER_BLANK = " ";

    private static final int INDEX_PROPERTY = 0;
    private static final int INDEX_SIZE = 1;
    private static final int INDEX_INDUSTRY = 2;

    public static void main(String[] args) throws IOException {
        String url = "http://jobs.51job.com/all/co3575340.html";
        String content = UrlContentGetter.getHtmlCode(url, "gbk");
        ContentAnalyzer analyzer = new Job51CompanyDetailContentAnalyzer();
        analyzer.setUrl(url);
        analyzer.setContent(content);
        JSONArray jobs = analyzer.doAnalyze();
        System.out.println(jobs.toString());
    }

}
