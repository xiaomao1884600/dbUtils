package com.doubeye.spider.content.analyzer.zhaopin;

import com.doubeye.commons.utils.net.JsoupContentGetter;
import com.doubeye.spider.bean.CompanyBean;
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
 * 智联招聘公司页分析器
 */
public class ZhaopinCompanyDetailAnalyzer extends AbstractContentAnalyzer{

    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        CompanyBean companyBean = new CompanyBean();
        //公司名称
        String companyName = ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_COMPANY_NAME_ELEMENT).trim();
        companyBean.setCompany(companyName);

        Elements companyInfoElements = getDocument().select(SELECTOR_COMPANY_INFO_ELEMENTS);
        for (Element companyInfoElement : companyInfoElements) {
            String content = ContentAnalyzer.getElementContent(companyInfoElement);
            String value = StringUtils.substringAfter(content, SEPARATOR_COLON);
            if (content.contains(IDENTIFIER_PROPERTY)) {
                companyBean.setProperty(value);
            } else if (content.contains(IDENTIFIER_SIZE)) {
                companyBean.setSize(value);
            } else if (content.contains(IDENTIFIER_OFFICIAL_SITE)) {
                companyBean.setOriginCompanyName(value);
            } else if (content.contains(IDENTIFIER_INDUSTRY)) {
                companyBean.setIndustry(value);
            } else if (content.contains(IDENTIFIER_ADDRESS)) {
                companyBean.setAddress(value);
            }
        }

        companyBean.setIntroduction(ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_INTRODUCTION_ROOT_ELEMENT));

        result.add(JSONObject.fromObject(companyBean));
        return result;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String SELECTOR_COMPANY_NAME_ELEMENT = "div.mainLeft h1";
    private static final String SELECTOR_COMPANY_INFO_ELEMENTS = "table.comTinyDes tr";
    private static final String SELECTOR_INTRODUCTION_ROOT_ELEMENT = "div.company-content";

    private static final String IDENTIFIER_PROPERTY = "公司性质";
    private static final String IDENTIFIER_SIZE = "公司规模";
    private static final String IDENTIFIER_OFFICIAL_SITE = "公司网站";
    private static final String IDENTIFIER_INDUSTRY = "公司行业";
    private static final String IDENTIFIER_ADDRESS = "公司地址";

    private static final String SEPARATOR_COLON = "：";

    public static void main(String[] args) throws IOException {
        String url = "http://company.zhaopin.com/%E5%8C%97%E4%BA%AC%E4%B8%AD%E7%A7%91%E8%81%94%E9%80%9A%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E8%B4%A3%E4%BB%BB%E5%85%AC%E5%8F%B8_CC547165126.htm";
        ContentAnalyzer analyzer = new ZhaopinCompanyDetailAnalyzer();

        JsoupContentGetter contentGetter = new JsoupContentGetter();
        String content = contentGetter.get(url);
        analyzer.setContent(content);
        JSONArray detail = analyzer.doAnalyze();
        System.out.println(detail.toString());
    }
}
