package com.doubeye.spider.content.analyzer.boss.dictionary;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class BossAllJobTypeDictionaryPageAnalyzer extends AbstractContentAnalyzer{
    /**
     * 存放中华英才网所有职位字典的页面
     */
    private static final String URL = "https://www.zhipin.com/";

    @Override
    public JSONArray doAnalyze() {
        return analyzeTopLevelJobTypes();
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    /**
     * 获取顶层职位类型
     * @return 顶层职位类型
     */
    private JSONArray analyzeTopLevelJobTypes() {
        JSONArray result = new JSONArray();
        Element jobTypeRootElement = getDocument().getElementsByClass(CLASS_NAME_JOB_TYPE_FIRST_LEVEL_ROOT_ELEMENT).first();
        Elements elements = jobTypeRootElement.getElementsByTag("dl");
        for (Element element : elements) {
            Element titleElement = element.getElementsByTag("dt").first();
            String jobTypeName = Jsoup.parse(titleElement.html()).text();
            //跳过所有行业节点
            if (jobTypeName.equals(JOB_TYPE_ALL_INDUSTRY)) {
                continue;
            }
            JSONObject topJobType = new JSONObject();
            topJobType.put("jobTypeName", jobTypeName);
            topJobType.put("subTypes", analyzeSecondLevelJobTypes(element));
            result.add(topJobType);
        }
        return result;
    }

    private JSONArray analyzeSecondLevelJobTypes(Element subJobTypeRootElement) {
        JSONArray result = new JSONArray();
        Elements subJobTypeElements = subJobTypeRootElement.getElementsByTag("li");
        subJobTypeElements.forEach(element -> {
            Element subJobTypeElement = element.getElementsByTag("h4").first();
            if (subJobTypeElement == null) {
                return;
            }
            String subJobTypeName = subJobTypeElement.html();
            JSONObject subJobType = new JSONObject();
            subJobType.put("subJobTypeName", subJobTypeName);
            subJobType.put("jobs", analyzeJobs(element));
            result.add(subJobType);
        });
        return result;
    }

    private JSONArray analyzeJobs(Element jobsRootElement) {
        JSONArray jobs = new JSONArray();
        Elements jobElements = jobsRootElement.getElementsByTag("a");
        jobElements.forEach(jobElement -> {
            JSONObject jobObject = new JSONObject();
            String code = jobElement.attr("href");
            String name = jobElement.html();
            jobObject.put("code", code);
            jobObject.put("name", name);
            jobs.add(jobObject);
        });
        return jobs;
    }


    public static JSONArray getAllJobTypes() {
        BossAllJobTypeDictionaryPageAnalyzer analyzer = new BossAllJobTypeDictionaryPageAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtml(URL, UrlContentGetter.FIREFOX_HEADER));
        return analyzer.doAnalyze();
    }



    /**
     * 职位类型根元素类名
     */
    private static final String CLASS_NAME_JOB_TYPE_FIRST_LEVEL_ROOT_ELEMENT = "job-menu";
    /**
     * 职位子类型根元素类名
     */
    //private static final String CLASS_NAME_JOB_SUBTYPE_ROOT_ELEMENT = "menu-sub";

    private static final String JOB_TYPE_ALL_INDUSTRY = "所有行业";

    public static void main(String[] args) {
        System.out.println(getAllJobTypes());
    }
}
