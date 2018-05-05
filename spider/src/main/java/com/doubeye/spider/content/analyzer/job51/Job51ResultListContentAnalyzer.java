package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.json.JSONUtils;
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

/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧职位查询列表内容分析器
 */
public class Job51ResultListContentAnalyzer extends AbstractContentAnalyzer {

    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Element jobResultTableElement = getDocument().getElementById(ID_JOB_RESULT_ROOT_ELEMENT);
        if (jobResultTableElement != null) {
            Elements jobElements = jobResultTableElement.select(SELECTOR_JOB_ELEMENT);
            for (Element jobElement : jobElements) {
                //去掉title元素
                if (!jobElement.hasClass(CLASS_NAME_TITLE)) {
                    JSONObject jobObject = new JSONObject();
                    Element jobTitleElement = jobElement.select(SELECTOR_POST_TITLE_ELEMENT).first();
                    String detailPageUrl = jobTitleElement.attr("href");
                    String post = Jsoup.parse(jobTitleElement.html()).text();
                    jobObject.put("post", post);
                    jobObject.put("detailPageUrl", detailPageUrl);
                    getJobDetail(jobObject, detailPageUrl);

                    Element companyElement = jobElement.select(SELECTOR_COMPANY_ELEMENT).first();
                    String company = Jsoup.parse(companyElement.html()).text();
                    String companyUrl = companyElement.attr("href");
                    jobObject.put("company", company);
                    jobObject.put("companyUrl", companyUrl);


                    Element cityElement = jobElement.select(SELECTOR_CITY_ELEMENT).first();
                    String city = Jsoup.parse(cityElement.html()).text();
                    jobObject.put("city", city);

                    Element salaryElement = jobElement.select(SELECTOR_SALARY_ELEMENT).first();
                    processSalary(jobObject, salaryElement);

                    Element postAtElement = jobElement.select(SELECTOR_POST_AT_ELEMENT).first();
                    String postAt = Jsoup.parse(postAtElement.html()).text();
                    postAt = DateTimeUtils.getCurrentTime("yyyy") + "-" + postAt;
                    jobObject.put("postAt", postAt);


                    jobObject.put("condition", getAdditionInfo());
                    result.add(jobObject);
                }
            }
        }
        return result;
    }

    private void getJobDetail(JSONObject jobObject, String url) throws IOException {
        ContentAnalyzer analyzer = new Job51PostDetailContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode(url, "gbk"));
        JSONArray detail = analyzer.doAnalyze();
        if (detail.size() == 1) {
            JSONObject object = detail.getJSONObject(0);
            JSONUtils.merge(jobObject, object);
        }
    }

    /**
     * 处理薪资，目前处理了万/月和千/月
     * @param jobObject 职位对象
     * @param salaryElement 薪资元素
     */
    private void processSalary(JSONObject jobObject, Element salaryElement) {
        String salaryString = Jsoup.parse(salaryElement.html()).text();
        if (salaryString.endsWith(SALARY_10K_PER_MONTH)) {
            jobObject.put("salary", processSalary(StringUtils.substringBefore(salaryString, SALARY_10K_PER_MONTH), 10000));
        } else if (salaryString.endsWith(SALARY_K_PER_MONTH)) {
            jobObject.put("salary", processSalary(StringUtils.substringBefore(salaryString, SALARY_K_PER_MONTH), 1000));
        } else {
            jobObject.put("salary", salaryString);
        }
    }

    private static String processSalary(String salaryString, int multiplier) {
        Float start = Float.parseFloat(StringUtils.substringBefore(salaryString, "-"));
        Float end = Float.parseFloat(StringUtils.substringAfter(salaryString, "-"));
        return new Float(start * multiplier).intValue() + "-" + new Float(end * multiplier).intValue();
    }

    @Override
    public int getCurrentPage() {
        Elements pageRootElements = getDocument().select(SELECTOR_PAGE_ROOT_ELEMENT);
        if (pageRootElements.size() > 0) {
            Element currentPageElement = pageRootElements.select(CLASS_NAME_CURRENT_PATE_ELEMENT).first();
            return Integer.parseInt(Jsoup.parse(currentPageElement.html()).text());
        } else {
            return 1;
        }
    }

    private static final String ID_JOB_RESULT_ROOT_ELEMENT = "resultList";

    private static final String CLASS_NAME_TITLE = "title";

    static final String SELECTOR_PAGE_ROOT_ELEMENT = "div.dw_page";
    private final String CLASS_NAME_CURRENT_PATE_ELEMENT = "on";

    private static final String SELECTOR_JOB_ELEMENT = "div.el";

    private static final String SELECTOR_POST_TITLE_ELEMENT = "p.t1 a";
    private static final String SELECTOR_COMPANY_ELEMENT = "span.t2>a";
    private static final String SELECTOR_CITY_ELEMENT = "span.t3";
    private static final String SELECTOR_SALARY_ELEMENT = "span.t4";
    private static final String SELECTOR_POST_AT_ELEMENT = "span.t5";

    private static final String SALARY_10K_PER_MONTH = "万/月";
    private static final String SALARY_K_PER_MONTH = "千/月";

    public static void main (String[] args) throws IOException {
        String url = "http://search.51job.com/list/250000,000000,2707,00,2,99,%2B,2,1.html";
        String content = UrlContentGetter.getHtmlCode(url, "gbk");
        ContentAnalyzer analyzer = new Job51ResultListContentAnalyzer();
        analyzer.setContent(content);
        JSONArray jobs = analyzer.doAnalyze();
        System.out.println(jobs.toString());
    }
}
