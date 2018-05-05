package com.doubeye.spider.content.analyzer.chinaHr;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.bean.CompanyBean;
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
 * 中华英才网公司详情页分析器
 * history:
 *  1.0.1 :
 *      + 按JOB公司库添加抓取信息
 *      ! 规范区分公司属性的标示符为常量
 *      ! 使用CompanyBean处理公司信息
 *      ! 将分析公司基本信息和联系信息部分拆为单独的函数
 *
 */
public class ChinaHrCompanyDetailContentAnalyzer extends AbstractContentAnalyzer{
    @Override
    public JSONArray doAnalyze() {
        JSONArray result = new JSONArray();

        CompanyBean bean = new CompanyBean();

        String companyName = ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_COMPANY_NAME_ELEMENT);
        bean.setCompany(companyName);

        Elements companyContactRootElements = getDocument().getElementsByClass(CLASS_NAME_COMPANY_INFO_ROOT_ELEMENT);
        processContact(companyContactRootElements, bean);
        Elements companyBasicInfoRootElement = getDocument().select(SELECTOR_COMPANY_INFO_ELEMENTS);
        processCompanyInfo(companyBasicInfoRootElement, bean);
        bean.setCompanyUrl(getUrl());

        bean.setIntroduction(ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_COMPANY_INTRODUCTION));

        JSONObject companyObject = JSONObject.fromObject(bean);
        result.add(companyObject);
        return result;
    }

    private void processContact(Elements rootElements, CompanyBean bean) {
        if (rootElements.size() > 0) {
            Element companyInfoRootElement = rootElements.first();
            Elements elements = companyInfoRootElement.getElementsByTag("p");
            for (Element element : elements) {
                String content = Jsoup.parse(element.html()).text();
                if (content.contains(IDENTIFIER_CONTACTS)) {
                    bean.setContacts(StringUtils.substringAfter(content, SEPARATOR_COLON));
                } else if (content.contains(IDENTIFIER_MOBILE)) {
                    bean.setMobile(StringUtils.substringAfter(content, SEPARATOR_COLON));
                } else if (content.contains(IDENTIFIER_PHONE)) {
                    bean.setPhone(StringUtils.substringAfter(content, SEPARATOR_COLON));
                } else if (content.contains(IDENTIFIER_EMAIL)) {
                    bean.setEmail(StringUtils.substringAfter(content, SEPARATOR_COLON));
                } else if (content.contains(IDENTIFIER_ADDRESS)) {
                    bean.setAddress(StringUtils.substringAfter(content, SEPARATOR_COLON));
                } else if (content.contains(IDENTIFIER_SITE)) {
                    bean.setRealUrl(StringUtils.substringAfter(content, SEPARATOR_COLON));
                }
            }
        }
    }

    private void processCompanyInfo(Elements elements, CompanyBean bean) {
        for (int i = 0; i < elements.size(); i ++) {
            Element element = elements.get(i);
            String content = ContentAnalyzer.getElementContent(element);
            if (i == INDEX_INDUSTRY) {
                bean.setIndustry(ContentAnalyzer.getElementContent(element));
            } else if (i == INDEX_PROPERTY){
                bean.setProperty(ContentAnalyzer.getElementContent(element));
            }
            if (content.contains(IDENTIFIER_ESTABLISHED_YEAR)) {
                bean.setEstablishedTime(StringUtils.substringAfter(content, SEPARATOR_COLON));
            } else if (content.contains(IDENTIFIER_REGISTERED_CAPITAL)) {
                bean.setRegisteredCapital(StringUtils.substringAfter(content, SEPARATOR_COLON));
            }
        }
    }

    private static final String CLASS_NAME_COMPANY_INFO_ROOT_ELEMENT = "address";
    private static final String SELECTOR_COMPANY_NAME_ELEMENT = "div.wrap-til h1";
    private static final String SELECTOR_COMPANY_INFO_ELEMENTS = "div.wrap-mc em";
    private static final String SELECTOR_COMPANY_INTRODUCTION = "div.article";
    private static final int INDEX_INDUSTRY = 1;
    private static final int INDEX_PROPERTY = 2;

    private static final String SEPARATOR_COLON = "：";

    private static final String IDENTIFIER_CONTACTS = "联系人";
    private static final String IDENTIFIER_MOBILE = "手机";
    private static final String IDENTIFIER_PHONE = "固话";
    private static final String IDENTIFIER_EMAIL = "邮箱";
    private static final String IDENTIFIER_ADDRESS = "公司地址";
    private static final String IDENTIFIER_SITE = "网址";
    private static final String IDENTIFIER_ESTABLISHED_YEAR = "成立年份";
    private static final String IDENTIFIER_REGISTERED_CAPITAL = "注册资金";


    @Override
    public int getCurrentPage() {
        return 1;
    }

    public static void main(String[] args) throws IOException {
        //存在返回信息
        String url = "http://www.chinahr.com/company/22024968935041.html";
        //只有公司地址
        //String url = "http://www.chinahr.com/company/23762756286977.html";
        ChinaHrCompanyDetailContentAnalyzer analyzer = new ChinaHrCompanyDetailContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode(url, "utf-8"));
        analyzer.setUrl(url);
        JSONArray detail = analyzer.doAnalyze();
        System.out.println(detail.toString());
    }
}
