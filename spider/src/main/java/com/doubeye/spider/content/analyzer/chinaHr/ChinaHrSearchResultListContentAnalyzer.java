package com.doubeye.spider.content.analyzer.chinaHr;

import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
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
 * @version 1.0.0
 * 中华英才网职位城市查找结果分析器
 */
public class ChinaHrSearchResultListContentAnalyzer extends AbstractContentAnalyzer{
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Elements elements = getDocument().getElementsByClass(CLASS_NAME_JOB_ROOT);
        for (Element jobElement : elements) {
            JSONObject jobObject = new JSONObject();
            jobObject.put("detailPageUrl", jobElement.attr(PROPERTY_JOB_DETAIL_URL));
            Element jobTitleElement = jobElement.getElementsByClass(CLASS_NAME_JOB_CONTENT_FIRST_ROW).first();
            if (jobTitleElement != null) {
                Element titleElement = jobTitleElement.getElementsByClass(CLASS_NAME_JOB_TITLE).first();
                String jobTitle = titleElement.attr(PROPERTY_NAME_JOB_TITLE);
                if (StringUtils.isEmpty(jobTitle)) {
                    jobTitle = Jsoup.parse(titleElement.html()).text();
                }
                jobObject.put("post", jobTitle);

                Element postAtElement = jobTitleElement.getElementsByClass(CLASS_NAME_POST_AT).first();
                jobObject.put("postedAt", postAtElement.html());
                Element companyElement = jobElement.getElementsByClass(CLASS_NAME_COMPANY).first();
                jobObject.put("companyNameInList", Jsoup.parse(companyElement.html()).text());
                Element companyUrlElement = companyElement.getElementsByTag("a").first();
                String companyUrl = companyUrlElement.attr("href");
                jobObject.put("companyUrl", companyUrl);
                if (!ignoreDetail) {
                    getCompanyDetail(companyUrl, jobObject);
                }
            }
            Element jobInfoElement = jobElement.getElementsByClass(CLASS_NAME_JOB_CONTENT_SECOND_ROW).first();
            if (jobInfoElement != null) {
                Element bunchInfoElement = jobInfoElement.getElementsByClass(CLASS_NAME_BUNCH_INFO).first();
                processBunchInfo(jobObject, bunchInfoElement.html());
                Element salaryElement = jobInfoElement.getElementsByClass(CLASS_NAME_SALARY).first();
                jobObject.put("salary", salaryElement.html());
                Element otherElement = jobInfoElement.getElementsByClass(CLASS_NAME_OTHERS).first();
                Elements otherElements = otherElement.getElementsByTag("em");
                if (otherElements.size() == 3) {
                    jobObject.put("industry", otherElements.get(0).html());
                    jobObject.put("companyProperty", otherElements.get(1).html());
                    jobObject.put("companySize", otherElements.get(2).html());
                }
            }

            jobObject.put("condition", getAdditionInfo());

            result.add(jobObject);
        }
        return result;
    }

    private void getCompanyDetail(String companyUrl, JSONObject jobObject) throws IOException {
        ChinaHrCompanyDetailContentAnalyzer analyzer = new ChinaHrCompanyDetailContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode(companyUrl, "utf-8"));
        JSONArray detail = analyzer.doAnalyze();
        if (detail.size() == 1) {
            JSONObject object = detail.getJSONObject(0);
            JSONUtils.merge(jobObject, object);
        }
    }

    private static void processBunchInfo(JSONObject jobObject, String bunchInfo) {
        String provinceCity = StringUtils.substringAfter(StringUtils.substringBefore(bunchInfo, "]"), "[");
        if (provinceCity.contains("/")) {
            String[] cities = provinceCity.split("/");
            if (cities.length >= 1) {
                jobObject.put("city", cities[0]);
            }
        } else {
            jobObject.put("province", provinceCity);
        }
        String other = StringUtils.substringAfter(bunchInfo, "]");
        if (other.contains("/")) {
            String[] info = other.split("/");
            jobObject.put("experience", info[0]);
            jobObject.put("degree", info[1]);
        }
    }


    @Override
    public int getCurrentPage() {
        return Integer.parseInt(Jsoup.parse(document.getElementsByClass(CLASS_NAME_CURRENT_PAGE).first().html()).text());
    }

    private static final String CLASS_NAME_CURRENT_PAGE = "current";
    /**
     * 每个职位的根元素
     */
    private static final String CLASS_NAME_JOB_ROOT = "jobList";
    /**
     * 职位详情页url属性
     */
    private static final String PROPERTY_JOB_DETAIL_URL = "data-url";

    private static final String CLASS_NAME_JOB_CONTENT_FIRST_ROW = "l1";
    private static final String CLASS_NAME_JOB_CONTENT_SECOND_ROW = "l2";
    /**
     * 职位名称className
     */
    private static final String CLASS_NAME_JOB_TITLE = "e1";
    /**
     * 职位名称propertyName
     */
    private static final String PROPERTY_NAME_JOB_TITLE = "title";
    /**
     * 发布时间className
     */
    private static final String CLASS_NAME_POST_AT = "e2";
    /**
     * 公司className
     */
    private static final String CLASS_NAME_COMPANY = "e3";
    /**
     * [省/市]/工作经验/学历
     */
    private static final String CLASS_NAME_BUNCH_INFO = "e1";
    /**
     * 薪资
     */
    private static final String CLASS_NAME_SALARY = "e2";
    /**
     * 行业，企业属性，企业规模
     */
    private static final String CLASS_NAME_OTHERS = "e3";


    public static void main(String[] args) throws IOException {
        ContentAnalyzer analyzer = new ChinaHrSearchResultListContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode("http://www.gaokaopai.com/daxue-0-0-0-0-0-0-0.html" , "utf-8"));
        System.out.println(analyzer.doAnalyze().toString());
    }
}
