package com.doubeye.spider.content.analyzer.chinaHr;

import com.doubeye.spider.content.analyzer.chinaHr.dictionary.ChinaHrDictionary;
import com.doubeye.spider.content.analyzer.chinaHr.dictionary.ChinaHrDictionary;
import net.sf.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChinaHrUrlGenerator {

    public static List<String> getInterestedJobTypeCityUrls(boolean today) {
        List<String> result = new ArrayList<>();
        INTERESTED_JOB_SUBTYPE_NAMES.forEach(jobType -> {
            JSONArray provinces = ChinaHrDictionary.CHINA_HR_CITY;
            for (int i = 0; i < provinces.size(); i ++ ){
                String condition = jobType + "|||" + provinces.getJSONObject(i).getString("name");
                String todayPart = today ? TODAY_TEMPLATE : "";
                try {
                    result.add(String.format(JOB_TYPE_CITY_URL_TEMPLATE, URLEncoder.encode(jobType, "utf-8"), provinces.getJSONObject(i).getString("id"), todayPart, condition));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return result;
    }

    /**
     * 职位类型与城市检索的URL模板，第一个参数为职位类型，第二个参数为字典CITY中的id,第三个参数为当天模板
     */
    private static final String JOB_TYPE_CITY_URL_TEMPLATE = "http://www.chinahr.com/sou/?orderField=relate&keyword=%s&city=%s%s[@%s";
    static final String PAGE_TEMPLATE = "&page=%s";
    private static final String TODAY_TEMPLATE = "&companyType=0&degree=-1&refreshTime=1&workAge=-1";

    private static final Set<String> INTERESTED_JOB_SUBTYPE_NAMES = new HashSet<>();

    static {
            INTERESTED_JOB_SUBTYPE_NAMES.add("HTML5");
            INTERESTED_JOB_SUBTYPE_NAMES.add("web前端");
            INTERESTED_JOB_SUBTYPE_NAMES.add("JavaScript");
            INTERESTED_JOB_SUBTYPE_NAMES.add("U3D");
            INTERESTED_JOB_SUBTYPE_NAMES.add("前端开发");
            INTERESTED_JOB_SUBTYPE_NAMES.add("游戏开发");
            INTERESTED_JOB_SUBTYPE_NAMES.add("图形开发");
            INTERESTED_JOB_SUBTYPE_NAMES.add("UI设计师");
            INTERESTED_JOB_SUBTYPE_NAMES.add("UED设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("UE设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("UX设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("网页设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("网页制作");
            INTERESTED_JOB_SUBTYPE_NAMES.add("美工");
            INTERESTED_JOB_SUBTYPE_NAMES.add("设计经理");
            INTERESTED_JOB_SUBTYPE_NAMES.add("设计主管");
            INTERESTED_JOB_SUBTYPE_NAMES.add("设计师");
            INTERESTED_JOB_SUBTYPE_NAMES.add("美术指导");
            INTERESTED_JOB_SUBTYPE_NAMES.add("美术编辑");
            INTERESTED_JOB_SUBTYPE_NAMES.add("艺术指导");
            INTERESTED_JOB_SUBTYPE_NAMES.add("摄影师");
            INTERESTED_JOB_SUBTYPE_NAMES.add("摄像师");
            INTERESTED_JOB_SUBTYPE_NAMES.add("后期制作");
            INTERESTED_JOB_SUBTYPE_NAMES.add("艺术总监");
            INTERESTED_JOB_SUBTYPE_NAMES.add("设计总监");
            INTERESTED_JOB_SUBTYPE_NAMES.add("绘画");
            INTERESTED_JOB_SUBTYPE_NAMES.add("原画师");
            INTERESTED_JOB_SUBTYPE_NAMES.add("CAD设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("CAD制图");
            INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("三维设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("三维制作");
            INTERESTED_JOB_SUBTYPE_NAMES.add("3D制作");
            INTERESTED_JOB_SUBTYPE_NAMES.add("动画设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("特效设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("美编");
            INTERESTED_JOB_SUBTYPE_NAMES.add("美术设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("3D设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("多媒体");
            INTERESTED_JOB_SUBTYPE_NAMES.add("动画开发");
            INTERESTED_JOB_SUBTYPE_NAMES.add("家具设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("工艺品设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("珠宝设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("玩具设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("店面设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("展览设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("展示设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("陈列设计");
            INTERESTED_JOB_SUBTYPE_NAMES.add("工业设计");
    }

    public static void main(String[] args) {
        System.out.println(ChinaHrDictionary.CHINA_HR_CITY.size() + "  " + INTERESTED_JOB_SUBTYPE_NAMES.size());
        System.out.println(getInterestedJobTypeCityUrls(true));
        System.out.println(getInterestedJobTypeCityUrls(true).size());
    }
}
