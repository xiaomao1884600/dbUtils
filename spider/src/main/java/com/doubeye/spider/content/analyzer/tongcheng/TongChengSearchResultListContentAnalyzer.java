package com.doubeye.spider.content.analyzer.tongcheng;

import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.captcha.chaojiying.ChaoJiYing;
import com.doubeye.spider.content.analyzer.tongcheng.helper.OrganizationCodeSearchHelper;
import com.doubeye.spider.content.analyzer.tongcheng.helper.TongChengPostCaptchaVerification;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import com.doubeye.spider.content.analyzer.ContentAnalyzer;
import com.doubeye.spider.content.analyzer.tongcheng.helper.OrganizationCodeSearchHelper;
import com.doubeye.spider.content.analyzer.tongcheng.helper.TongChengPostCaptchaVerification;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;



public class TongChengSearchResultListContentAnalyzer extends AbstractContentAnalyzer{
    private static Logger logger = LogManager.getLogger(TongChengSearchResultListContentAnalyzer.class);
    @Override
    public JSONArray doAnalyze() throws IOException {
        JSONArray result = new JSONArray();
        Element jobRootElement = getDocument().getElementById(ID_JOB_ROOT_ELEMENT);
        if (jobRootElement != null) {
            Elements jobElements = jobRootElement.getElementsByTag("li");
            for (Element element : jobElements) {
                result.add(processJobElement(element));
                //判断下一个记录是否是无数据节点
                Element nextElement = element.nextElementSibling();
                if (nextElement != null && nextElement.hasClass(CLASS_NAME_NO_DATA)) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public int getCurrentPage() {
        int currentPage = 1;
        Element pageRootElement = getDocument().getElementsByClass(CLASS_NAME_PAGE_ROOT_ELEMENT).first();
        Elements currentPageElements = pageRootElement.getElementsByClass(CLASS_NAME_CURRENT_PAGE_ELEMENT);
        if (currentPageElements != null && currentPageElements.size() > 0) {
            currentPage = Integer.parseInt(currentPageElements.first().html());
        }
        return currentPage;
    }

    /**
     * 分页根元素
     */
    private static final String CLASS_NAME_PAGE_ROOT_ELEMENT = "page_nums";
    /**
     * 当前页
     */
    private static final String CLASS_NAME_CURRENT_PAGE_ELEMENT = "current";
    /**
     * 职位的根元素
     */
    private static final String ID_JOB_ROOT_ELEMENT = "list_con";
    /**
     * 每个职位的根元素
     */
    private static final String CLASS_NAME_JOB_ELEMENTS = "job_item";
    /**
     * 职位名称元素
     */
    private static final String CLASS_NAME_JOB_TITLE = "name";
    /**
     * 薪水元素
     */
    private static final String CLASS_NAME_SALARY = "job_salary";
    /**
     * 职位类型 cate
     */
    private static final String CLASS_NAME_POST_TYPE = "cate";
    /**
     * 公司
     */
    //private static final String CLASS_NAME_COMPANY_NAME = "comp_name";
    private static final String CLASS_NAME_COMPANY_NAME = "div.comp_name a";
    /**
     * 学历
     */
    private static final String CLASS_NAME_DEGREE = "xueli";
    /**
     * 经验
     */
    private static final String CLASS_NAME_EXPERIENCE = "jingyan";
    /**
     * url中的职位id属性
     */
    private static final String JOB_ID_PROPERTY_IN_URL = "entinfo=";
    /**
     * 无更多符合条件的数据
     */
    static final String CLASS_NAME_NO_DATA = "noData";

    private JSONObject processJobElement(Element jobElement) throws IOException {
        JSONObject jobObject = new JSONObject();
        //职位标题
        Element jobTileElement = jobElement.getElementsByClass(CLASS_NAME_JOB_TITLE).first();
        String jobTitle = jobTileElement.html();
        jobObject.put("post", jobTitle);
        //url
        Element urlElement = jobTileElement.parent();
        String url = urlElement.attr("href");
        jobObject.put("url", url);
        String id = StringUtils.substringBetween(url, JOB_ID_PROPERTY_IN_URL, "_");
        jobObject.put("id", id);
        //城市信息，不一定存在
        Elements cityElements = jobTileElement.getElementsByTag("b");
        if (cityElements != null && cityElements.size() > 0) {
            String city = cityElements.first().html();
            jobObject.put("city", city);
        }
        //薪水
        Element salaryElement = jobElement.getElementsByClass(CLASS_NAME_SALARY).first();
        String salary = StringUtils.substringBefore(Jsoup.parse(salaryElement.html()).text(), "元");
        jobObject.put("salary", salary);
        //公司
        //Element companyElement = jobElement.getElementsByClass(CLASS_NAME_COMPANY_NAME).first();
        Element companyElement = ContentAnalyzer.getFirstElement(jobElement, CLASS_NAME_COMPANY_NAME);
        //String company = Jsoup.parse(companyElement.html()).text();
        String company = ContentAnalyzer.getElementAttribute(companyElement, "title");
        jobObject.put("company", company);
        //String companyUrlInSite = companyElement.getElementsByTag("a").attr("href");
        String companyUrlInSite = ContentAnalyzer.getElementAttribute(companyElement, "href");
        jobObject.put("companyUrl", companyUrlInSite);
        //公司在58同城的url

        //组织机构代码
        String organizationCode = OrganizationCodeSearchHelper.getOrganizationCodeByTongCheng(company);
        jobObject.put("organizationCode", organizationCode);
        //获得公司页
        if (!ignoreDetail) {
            getCompanyDetail(companyUrlInSite, jobObject);
        }
        //页面上的职位类型
        Element postTypeElement = jobElement.getElementsByClass(CLASS_NAME_POST_TYPE).first();
        String postTypeFromPage = postTypeElement.html();
        jobObject.put("postTypeFromPage", postTypeFromPage);
        //学历
        Element degreeElement = jobElement.getElementsByClass(CLASS_NAME_DEGREE).first();
        String degree = degreeElement.html();
        jobObject.put("degree", degree);
        //经验
        Element experienceElement = jobElement.getElementsByClass(CLASS_NAME_EXPERIENCE).first();
        String experience = experienceElement.html();
        jobObject.put("experience", experience);
        //招聘人数在详情页
        if (!ignoreDetail) {
            getJobDetail(url, jobObject);
        }
        jobObject.put("condition", getAdditionInfo());
        return jobObject;
    }

    private void getCompanyDetail(String companyUrlInSite, JSONObject jobObject) throws IOException {
        TongChengCompanyDetailPageAnalyzer analyzer = new TongChengCompanyDetailPageAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode(companyUrlInSite, "utf-8"));
        JSONArray detail = analyzer.doAnalyze();
        if (detail.size() == 1) {
            JSONObject object = detail.getJSONObject(0);
            JSONUtils.merge(jobObject, object);
        }
    }




    private static void getJobDetail(String url, JSONObject job) throws IOException {
        TongChengDetailPageAnalyzer analyzer = new TongChengDetailPageAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode(url, "utf-8"));
        if (analyzer.needCaptcha(analyzer.getDocument())) {
            logger.warn("doing captcha verification");
            String captchaImageUri = analyzer.getCaptchaContentUrl(analyzer.getDocument());
            String verifyCode = ChaoJiYing.crackImage(captchaImageUri);
            int verifyTimes = 1;
            boolean verifySuccess = false;
            //尝试验证三次，如果三次均失败，则抛出异常
            for (verifyTimes = 1; verifyTimes <=3 ; verifyTimes ++) {
                String verifyResultContent = TongChengPostCaptchaVerification.doPost(TongChengPostCaptchaVerification.generatePostObject(analyzer.getDocument(), verifyCode));
                System.out.println(verifyResultContent);
                JSONObject verifyResult = JSONObject.fromObject(verifyResultContent);
                if (verifyResult.getString("code").equals("0")) {
                    logger.warn("doing captcha verification success by " + verifyTimes + " time, verifyCode = " + verifyCode);
                    analyzer.setContent(UrlContentGetter.getHtmlCode(verifyResult.getString("msg"), "utf-8"));
                    break;
                } else {
                    //TODO 找回分数
                    logger.warn("doing captcha verification fail by " + verifyTimes + " time , verifyCode = " + verifyCode);
                }
                verifyTimes ++;
            }
            if (verifyTimes > 3) {
                throw new RuntimeException("获得工作详情信息出错，人机验证超过三次:" + url);
            }
        }
        JSONArray detail = analyzer.doAnalyze();
        if (detail.size() == 1) {
            JSONObject object = detail.getJSONObject(0);
            JSONUtils.merge(job, object);
        }
    }


    public static void main(String[] args) throws IOException {
        //测试一下验证码的情形，希望能够打印出正确的验证码地址，根据此地址能够获得验证码的图片
        TongChengSearchResultListContentAnalyzer analyzer = new TongChengSearchResultListContentAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtmlCode("http://tj.58.com/shejizhitu/?postdate=20170904_20170905&bd=1&page=1" , "utf-8"));
        // System.out.println(analyzer.getDocument().toString());
        // System.out.println("------------------------------------------");
        analyzer.doAnalyze();
        System.out.println(analyzer.doAnalyze());

    }
}
