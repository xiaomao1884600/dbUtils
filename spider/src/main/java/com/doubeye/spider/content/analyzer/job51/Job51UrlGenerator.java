package com.doubeye.spider.content.analyzer.job51;

import com.doubeye.commons.utils.json.JSONUtils;

import com.doubeye.spider.content.analyzer.job51.dictionary.Job51Dictionary;
import com.doubeye.spider.content.analyzer.job51.dictionary.Job51Dictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧网职位获取URL生成器
 */
@SuppressWarnings("unused")
public class Job51UrlGenerator {

    /**
     * 搜索URL模板，第一个参数为省份id,第二个参数为职位类型id,第三个参数为发布日期（9表示所有，1表示24小时内，2表示近三天，3表示近一个月，4表示其他）
     */
    private static final String URL_TEMPLATE = "http://search.51job.com/list/%s,000000,%s,00,%s,99,+,2,[@%s";
    static final String PAGE_TEMPLATE = "%s.html";

    public static List<String> getInterestedJobTypeCityUrls(int addedTime) {
        List<String> result = new ArrayList<>();
        INTERESTED_JOB_SUBTYPE_NAMES.forEach(jobType -> {
            JSONArray provinces = Job51Dictionary.JOB51_CITY;
            String jobTypeId = getJobType(jobType);
            for (int i = 0; i < provinces.size(); i ++ ){
                String condition = jobType + "|||" + provinces.getJSONObject(i).getString("name");
                result.add(String.format(URL_TEMPLATE,  provinces.getJSONObject(i).getString("id"), jobTypeId, addedTime, condition));
            }
        });
        return result;
    }

    private static JSONObject jobCondition = new JSONObject();

    private static String getJobType(String jobTypeName) {
        jobCondition.put("name", jobTypeName);
        return JSONUtils.findFirst(Job51Dictionary.JOB51_JOB_TYPE,  jobCondition).getString("id");
    }

    public static void main(String[] args) {
        INTERESTED_JOB_SUBTYPE_NAMES.add("软件测试");
        System.out.println(getInterestedJobTypeCityUrls(2));
    }


    private static Set<String> INTERESTED_JOB_SUBTYPE_NAMES = new HashSet<>();

    static {
        INTERESTED_JOB_SUBTYPE_NAMES.add("软件UI设计师/工程师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("多媒体/游戏开发工程师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("UI设计师/顾问");
        INTERESTED_JOB_SUBTYPE_NAMES.add("用户体验（UE/UX）设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("Web前端开发");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网站维护工程师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网页设计/制作/美工");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏界面设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术指导");
        INTERESTED_JOB_SUBTYPE_NAMES.add("艺术/设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("摄影师/摄像师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("后期制作");
        INTERESTED_JOB_SUBTYPE_NAMES.add("艺术/设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("绘画");
        INTERESTED_JOB_SUBTYPE_NAMES.add("动画/3D设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("原画师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("多媒体设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("包装设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("室内设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("园艺/园林/景观设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("音乐/美术教师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网店美工");
        INTERESTED_JOB_SUBTYPE_NAMES.add("建筑制图/模型/渲染");
    }


    static int ADDED_TIME_ALL = 9;
    static int ADDED_TIME_LAST_24_HOURS = 1;
    static int ADDED_TIME_LAST_3_DAYS = 2;
    static int ADDED_TIME_LAST_MONTH = 3;
    static int ADDED_TIME_ORTHER = 4;
}
