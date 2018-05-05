package com.doubeye.spider.content.analyzer.tongcheng;

import com.doubeye.commons.utils.net.BrowserSimulatedContentGetter;
import com.doubeye.commons.utils.net.JsoupContentGetter;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.spider.bean.CompanyBean;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.tongcheng.helper.OrganizationCodeSearchHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author doubeye
 * @version 1.0.1
 *  history
 *      1.0.1 :
 *          + 按JOB公司库添加抓取信息
 *          ! 使用CompanyBean暂存公司信息
 *          ! 规范区分公司属性的标示符为常量
 */
public class TongChengCompanyDetailPageAnalyzer extends AbstractContentAnalyzer{
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        CompanyBean bean = new CompanyBean();
        Elements elements = getDocument().select(SELECTOR_COMPANY_BASIC_INFO_ELEMENTS);
        if (elements.size() > 0) {
            String companyName = ContentAnalyzer.getFirstParsedElementPropertyBySelector(getDocument(), SELECTOR_COMPANY_NAME_ELEMENT, "title");
            bean.setCompany(companyName);

            for (Element element : elements) {
                String content = ContentAnalyzer.getElementContent(element);
                if (content.contains(IDENTIFIER_COMPANY_SITE)) {
                    String companyUrl = ContentAnalyzer.getFirstParsedElementContentBySelector(element, "a");
                    bean.setCompanyUrl(companyUrl);
                    BrowserSimulatedContentGetter getter = new BrowserSimulatedContentGetter();
                    Map<String, String> headers = new HashMap<>(16);
                    headers.put("Referer", getUrl());
                    getter.setHeader(headers);
                    String companyDetialContent = getter.get(companyUrl);
                    if (companyUrl.endsWith(DETAIL_PAGE_TEMPLATE_5858)) {
                        ContentAnalyzer analyzer = new CompanyPageInTongChengContentAnalyzer5858();
                        analyzer.setContent(companyDetialContent);
                        JSONArray companyInfo = analyzer.doAnalyze();
                        if (companyInfo.size() > 0) {
                            RefactorUtils.fillByJSON(bean, companyInfo.getJSONObject(0), true);
                        }
                    }
                } else if (content.contains(IDENTIFIER_PHONE)) {
                    String imgUrl = ContentAnalyzer.getFirstParsedElementPropertyBySelector(element, "img", "src");
                    bean.setPhoneImageUrl(imgUrl);
                } else if (content.contains(IDENTIFIER_CONTACTS)) {
                    String contact = content.trim().replace(IDENTIFIER_CONTACTS, "");
                    bean.setContacts(contact);
                } else if (content.contains(IDENTIFIER_PROPERTY)) {
                    String property = content.trim().replace(IDENTIFIER_PROPERTY, "");
                    bean.setProperty(property);
                } else if (content.contains(IDENTIFIER_SIZE)) {
                    String size = content.trim().replace(IDENTIFIER_SIZE, "");
                    bean.setSize(size);
                } else if (content.contains(IDENTIFIER_ADDRESS)) {
                    String address = ContentAnalyzer.getFirstParsedElementContentBySelector(element, "var");
                    bean.setAddress(address);
                } else if (content.contains(IDENTIFIER_INDUSTRY)) {
                    String industry = content.trim().replace(IDENTIFIER_INDUSTRY, "");
                    bean.setIndustry(industry);
                }
            }

            Elements introductionElements = getDocument().select(SELECTOR_COMPANY_INTRODUCTION);
            StringBuilder builder = new StringBuilder();
            for (Element introductionElement : introductionElements) {
                if (!introductionElement.hasClass(CLASS_NAME_OPEN_INTRO)) {
                    builder.append(ContentAnalyzer.getElementContent(introductionElement));
                }
            }
            bean.setIntroduction(builder.toString());
        } else {//另外一种公司页面
            String contact = ContentAnalyzer.getFirstParsedElementContentBySelector(getDocument(), SELECTOR_CONTACT_ELEMENT);
            bean.setContacts(contact);
            String phoneImageUrl = ContentAnalyzer.getFirstParsedElementPropertyBySelector(getDocument(), SELECTOR_MOBILE_ELEMENT, "src");
            bean.setPhoneImageUrl(phoneImageUrl);
        }

        OrganizationCodeSearchHelper.fillOrganizationInfoByTongCheng(bean.getCompany(), bean);
        result.add(JSONObject.fromObject(bean));
        return result;
    }



    private static final String SELECTOR_COMPANY_NAME_ELEMENT = "a.businessName";
    private static final String SELECTOR_COMPANY_BASIC_INFO_ELEMENTS = "ul.basicMsgList li";
    private static final String SELECTOR_COMPANY_INTRODUCTION = "div.compIntro span";
    private static final String CLASS_NAME_OPEN_INTRO = "openintro";
    private static final String SELECTOR_CONTACT_ELEMENT = "tr.tr_l4 td.td_c2";
    private static final String SELECTOR_MOBILE_ELEMENT  = "td.td_c3 img";




    private static final String IDENTIFIER_COMPANY_SITE = "企业网址";
    private static final String IDENTIFIER_PHONE = "联系电话";
    private static final String IDENTIFIER_CONTACTS = "联系人：";
    private static final String IDENTIFIER_PROPERTY = "公司性质：";
    private static final String IDENTIFIER_SIZE = "公司规模：";
    private static final String IDENTIFIER_ADDRESS = "公司地址：";
    private static final String IDENTIFIER_INDUSTRY = "公司行业：";

    private static final String DETAIL_PAGE_TEMPLATE_5858 = "5858.com";

    @Override
    public int getCurrentPage() {
        return 1;
    }

    public static void main(String[] args) throws IOException {

        String url = "http://qy.58.com/9742240432902/?PGTID=0d302408-0000-15c8-4829-7ef559adc8b6&ClickID=8";
        TongChengCompanyDetailPageAnalyzer analyzer = new TongChengCompanyDetailPageAnalyzer();

        JsoupContentGetter contentGetter = new JsoupContentGetter();
        String content = contentGetter.get(url);
        analyzer.setContent(content);
        JSONArray detail = analyzer.doAnalyze();
        System.out.println(detail.toString());
    }

    /**
     * @author doubeye
     * @version 1.0.0
     * 公司在58同城内的主页
     */
    public class CompanyPageInTongChengContentAnalyzer5858 extends AbstractContentAnalyzer {
        private static final String SELECTOR_PHONE = "input.company-contact-telphone";
        private static final String SELECTOR_MOBILE = "input.company-contact-telphone2";
        private static final String SELECTOR_INFO_ELEMENTS = "div.mod-box li";
        @Override
        public JSONArray doAnalyze() {
            JSONArray result = new JSONArray();
            String phone = ContentAnalyzer.getFirstParsedElementPropertyBySelector(getDocument(), SELECTOR_PHONE, "value");
            String mobile = ContentAnalyzer.getFirstParsedElementPropertyBySelector(getDocument(), SELECTOR_MOBILE, "value");
            String email = "";
            Elements infoElements = getDocument().select(SELECTOR_INFO_ELEMENTS);
            for (Element element : infoElements) {
                String content = ContentAnalyzer.getElementContent(element);
                if (content.contains(IDENTIFIER_EMAIL)) {
                    email = content.replace(IDENTIFIER_EMAIL, "");
                }
            }
            if (StringUtils.isNotEmpty(phone) || StringUtils.isNotEmpty(mobile) || StringUtils.isNotEmpty(email)) {
                CompanyBean bean = new CompanyBean();
                bean.setPhone(phone);
                bean.setMobile(mobile);
                bean.setEmail(email);
                result.add(JSONObject.fromObject(bean));
            }
            return result;
        }

        @Override
        public int getCurrentPage() {
            return 1;
        }

        private static final String IDENTIFIER_EMAIL = "电子邮箱：";
    }
}
