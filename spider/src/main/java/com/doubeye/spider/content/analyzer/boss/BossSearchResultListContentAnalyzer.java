package com.doubeye.spider.content.analyzer.boss;


import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BossSearchResultListContentAnalyzer extends AbstractContentAnalyzer {
    @Override
    public JSONArray doAnalyze() {
        JSONArray result = new JSONArray();
        Elements jobRootElements = getDocument().getElementsByClass(CLASS_NAME_JOB_ROOT_ELEMENT);
        if (jobRootElements != null && jobRootElements.size() > 0) {
            Element jobRootElement = jobRootElements.first();
            Elements jobElements = jobRootElement.getElementsByTag("li");
            for (Element jobElement : jobElements) {
                JSONObject jobObject = getJobObject(jobElement);
                result.add(jobObject);
            }
        }
        return result;
    }

    private JSONObject getJobObject(Element jobElement) {
        JSONObject jobObject = new JSONObject();
        Element primaryInfoElement = jobElement.getElementsByClass(CLASS_NAME_PRIMARY_INFO_ROOT_ELEMENT).first();
        processPrimaryInfo(jobObject, primaryInfoElement);
        Element companyInfoElement = jobElement.getElementsByClass(CLASS_NAME_COMPANY_ROOT_ELEMENT).first();
        processCompanyInfoElement(jobObject, companyInfoElement);
        Element postAtElement = jobElement.getElementsByClass(CLASS_NAME_POST_AT).first();
        jobObject.put("postAt", Jsoup.parse(postAtElement.html()).text());
        return jobObject;
    }

    private void processPrimaryInfo(JSONObject jobObject, Element primaryInfoElement) {
        Element jobTitleElement = primaryInfoElement.getElementsByClass(CLASS_NAME_JOB_NAME_ELEMENT).first();
        String post = StringUtils.substringBetween(jobTitleElement.html(), ">", "<span");

        Element salaryElement = primaryInfoElement.getElementsByClass(CLASS_NAME_SALARY_ELEMENT).first();
        String salary = Jsoup.parse(salaryElement.html()).text();
        Element jobUrlElement = primaryInfoElement.getElementsByTag("a").first();
        String url = DOMAIN + jobUrlElement.attr("href");
        Element otherJobInfoElement = primaryInfoElement.getElementsByTag("p").first();
        String otherJobInfo = otherJobInfoElement.html();
        String[] array = otherJobInfo.split(HTML_SEPERATOR_OTHER_JOB_INFO);
        if (array.length > 0) {
            jobObject.put("city", array[0].replaceAll("\"", ""));
        }
        if (array.length > 1) {
            jobObject.put("experience", array[1].replaceAll("\"", ""));
        }
        if (array.length > 2) {
            jobObject.put("degree", array[2].replaceAll("\"", ""));
        }

        jobObject.put("post", post);
        jobObject.put("salary", salary);
        jobObject.put("url", url);
    }


    private void processCompanyInfoElement(JSONObject jobObject, Element companyInfoElement) {
        Element companyNameElement = companyInfoElement.getElementsByClass(CLASS_NAME_COMPANY_NAME).first();
        String companyName = Jsoup.parse(companyNameElement.html()).text();

        Element otherCompanyInfoElement = companyInfoElement.getElementsByTag("p").first();
        String otherCompanyInfo = otherCompanyInfoElement.html();
        String[] array = otherCompanyInfo.split(HTML_SEPERATOR_OTHER_JOB_INFO);
        if (array.length > 0) {
            jobObject.put("industry", array[0].replaceAll("\"", ""));
        }
        if (array.length > 1) {
            jobObject.put("companyProperty", array[1].replaceAll("\"", ""));
        }
        if (array.length > 2) {
            jobObject.put("companySize", array[2].replaceAll("\"", ""));
        }

        jobObject.put("company", companyName);

    }

    @Override
    public int getCurrentPage() {
        int currentPage = 1;
        Elements currentPageElements = getDocument().getElementsByClass(CLASS_NAME_CURRENT_PAGE);
        if (currentPageElements != null && currentPageElements.size() > 0) {
            Element currentPageElement = currentPageElements.first();
            currentPage = Integer.parseInt(Jsoup.parse(currentPageElement.html()).text());
        }
        return currentPage;
    }

    private static final String DOMAIN = "https://www.zhipin.com";
    /**
     * 当前页
     */
    private static final String CLASS_NAME_CURRENT_PAGE = "cur";
    /**
     * 所有job的根元素
     */
    private static final String CLASS_NAME_JOB_ROOT_ELEMENT = "job-list";
    /**
     * 主要信息
     */
    private static final String CLASS_NAME_PRIMARY_INFO_ROOT_ELEMENT = "info-primary";
    /**
     * 职位名称
     */
    private static final String CLASS_NAME_JOB_NAME_ELEMENT = "name";
    /**
     * 薪水
     */
    private static final String CLASS_NAME_SALARY_ELEMENT = "red";
    /**
     * 公司信息
     */
    private static final String CLASS_NAME_COMPANY_ROOT_ELEMENT = "info-company";
    /**
     * 公司名称
     */
    private static final String CLASS_NAME_COMPANY_NAME = "name";
    private static final String CLASS_NAME_POST_AT = "time";
    /**
     * 职位信息中的分隔符
     */
    private static final String HTML_SEPERATOR_OTHER_JOB_INFO = "<em class=\"vline\"></em>";

    public static void main(String[] args) {
        BossSearchResultListContentAnalyzer analyzer = new BossSearchResultListContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtml("https://www.zhipin.com/c101010100/h_101010100/?query=JAVA&page=1", UrlContentGetter.FIREFOX_HEADER));
        // System.out.println(analyzer.getDocument().toString());
        // System.out.println("------------------------------------------");
        System.out.println(analyzer.doAnalyze());
    }

}
