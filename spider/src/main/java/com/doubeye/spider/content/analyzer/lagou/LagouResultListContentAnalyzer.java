package com.doubeye.spider.content.analyzer.lagou;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.job51.Job51ResultListContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author doubeye
 * @version 1.0.0
 * 拉勾网职位列表分析器
 */
public class LagouResultListContentAnalyzer extends AbstractContentAnalyzer {

    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Elements postElements = getDocument().select(SELECTOR_POST_ELEMENTS);
        for (Element postRootElement : postElements) {
            JSONObject jobObject = new JSONObject();
            String postTitle = ContentAnalyzer.getFirstParsedElementContentBySelector(postRootElement, SELECTOR_POST_TITLE_ELEMENT);
            jobObject.put("post", postTitle);
            String postUrl = ContentAnalyzer.getFirstParsedElementPropertyBySelector(postRootElement, SELECTOR_POST_URL_ELEMENT, "href");
            jobObject.put("postUrl", postUrl);
            String city = ContentAnalyzer.getFirstParsedElementContentBySelector(postRootElement, SELECTOR_CITY_ELEMENT);
            jobObject.put("city", city);
            String postedAt = ContentAnalyzer.getFirstParsedElementContentBySelector(postRootElement, SELECTOR_POSTED_TIME_ELEMENT);
            jobObject.put("postedAt", getPostedAt(postedAt));
            String salary = ContentAnalyzer.getFirstParsedElementContentBySelector(postRootElement, SELECTOR_SALARY_ELEMENT);
            jobObject.put("salary", getSalary(salary));
            String experienceAndDegree = ContentAnalyzer.getFirstParsedElementContentBySelector(postRootElement, SELECTOR_EXPERIENCE_AND_DEGREE).replace(salary, "");
            processExperienceAndDegree(jobObject, experienceAndDegree.trim());
            Element companyElement = ContentAnalyzer.getFirstElement(postRootElement, SELECTOR_COMPANY);
            String company = ContentAnalyzer.getElementContent(companyElement);
            jobObject.put("company", company);
            String companyUrl = ContentAnalyzer.getElementAttribute(companyElement, "href");
            jobObject.put("companyUrl", companyUrl);
            result.add(jobObject);
        }
        return result;
    }

    /**
     * 处理经验和学历
     * @param jobObject 工作对象
     * @param experienceAndDegree 学历和经验
     */
    private void processExperienceAndDegree(JSONObject jobObject, String experienceAndDegree) {
        jobObject.put("experience", StringUtils.substringBefore(experienceAndDegree, SEPARATOR_EXPERIENCE_AND_DEGREE).trim());
        jobObject.put("degree", StringUtils.substringAfter(experienceAndDegree, SEPARATOR_EXPERIENCE_AND_DEGREE).trim() );
    }

    /**
     * 根据显示值获得薪资范围
     * @param salary 薪资显示值
     * @return 去掉k等的值
     */
    private String getSalary(String salary) {
        String[] salaryRange = salary.split(SEPARATOR_YEAR_MONTH_DAY);
        String lower = salaryRange[0];
        String result = multi(lower);
        if (salaryRange.length > 1) {
            String upper = salaryRange[1];
            result += SEPARATOR_YEAR_MONTH_DAY + multi(upper);
        }
        return result;
    }

    private static String multi(String source) {
        if (source.contains(KILO)) {
            try {
                return (Integer.parseInt(StringUtils.substringBefore(source, KILO)) * 1000) + "";
            } catch (NumberFormatException e) {
                return source;
            }
        } else {
            return source;
        }
    }

    /**
     * 获得职位发布日期
     * @param postedAt 根据网站上的显示值计算发布日期
     * @return 实际发布日期
     */
    private String getPostedAt(String postedAt) {

        if (postedAt.contains(SEPARATOR_HOUR_MINUTE)) {
            return DateTimeUtils.today("yyyy-MM-dd");
        } else if (postedAt.contains(DAYS_BEFORE)){
            try {
                int days = Integer.parseInt(StringUtils.substringBefore(postedAt, DAYS_BEFORE));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_MONTH, -1 * days);
                return DateTimeUtils.getDefaultFormattedDateTime(calendar.getTime(), "yyyy-MM-dd");
            } catch (NumberFormatException e) {
                return postedAt;
            }
        } else if (postedAt.contains(SEPARATOR_YEAR_MONTH_DAY)){
            return postedAt;
        } else {
            return postedAt;
        }
    }

    @Override
    public int getCurrentPage() {
        Elements currentPageElements = getDocument().select(SELECTOR_CURRENT_PAGE_ELEMENT);
        if (currentPageElements.size() > 0) {
            return Integer.parseInt(Jsoup.parse(currentPageElements.first().html()).text());
        } else {
            return 1;
        }
    }

    private static final String SELECTOR_CURRENT_PAGE_ELEMENT = "div.pager_container a.pager_is_current";
    private static final String SELECTOR_POST_TITLE_ELEMENT = "a.position_link h3";
    private static final String SELECTOR_POST_URL_ELEMENT = "div.p_top a.position_link";
    private static final String SELECTOR_CITY_ELEMENT = "a.position_link em";
    private static final String SELECTOR_POSTED_TIME_ELEMENT = "div.p_top span.format-time";
    private static final String SELECTOR_SALARY_ELEMENT = "div.li_b_l span.money";
    private static final String SELECTOR_EXPERIENCE_AND_DEGREE = "div.p_bot div.li_b_l";
    private static final String SELECTOR_COMPANY = "div.company_name a";

    private static final String SEPARATOR_HOUR_MINUTE = ":";
    private static final String SEPARATOR_YEAR_MONTH_DAY = "-";
    private static final String SEPARATOR_EXPERIENCE_AND_DEGREE = "/";
    private static final String DAYS_BEFORE = "天前";

    private static final String KILO = "k";

    private static final String SELECTOR_POST_ELEMENTS = "ul.item_con_list li.con_list_item";

    public static void main(String[] args) throws IOException {
        String url = "https://www.lagou.com/zhaopin/shijueshejishi/3/";
        String content = UrlContentGetter.getHtml(url, UrlContentGetter.FIREFOX_HEADER);
        System.out.println(content);
        ContentAnalyzer analyzer = new LagouResultListContentAnalyzer();
        analyzer.setContent(content);
        JSONArray jobs = analyzer.doAnalyze();
        System.out.println(jobs.toString());
    }
}
