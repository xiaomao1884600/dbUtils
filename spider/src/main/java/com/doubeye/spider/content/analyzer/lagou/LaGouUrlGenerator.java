package com.doubeye.spider.content.analyzer.lagou;

import com.doubeye.spider.content.analyzer.lagou.dictionary.LaGouDictionary;
import com.doubeye.spider.content.analyzer.job51.dictionary.Job51Dictionary;
import com.doubeye.spider.content.analyzer.lagou.dictionary.LaGouDictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 拉勾网职位url生成器
 */
public class LaGouUrlGenerator {

    public static List<String> getInterestedJobTypeCityUrls() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < LaGouDictionary.LAGOU_JOB_TYPE.size(); i ++) {
            JSONObject jobTypeObject = LaGouDictionary.LAGOU_JOB_TYPE.getJSONObject(i);
            String name = jobTypeObject.getString("name");
            if (INTERESTED_JOB_SUBTYPE_NAMES.contains(name)) {
                String condition = name + "|||全国";
                result.add(String.format(URL_WITHOUT_CITY_TEMPLATE, jobTypeObject.getString("urlSection"), condition));
            }
        }
        return result;
    }


    /**
     * 无城市过滤的职位URL模板，其中参数为字典表中的urlSection属性
     */
    private static final String URL_WITHOUT_CITY_TEMPLATE = "https://www.lagou.com/zhaopin/%s/[@%s";
    static final String PAGE_TEMPLATE = "%s/";

    public static Set<String> INTERESTED_JOB_SUBTYPE_NAMES = new HashSet<>();

    static {
        INTERESTED_JOB_SUBTYPE_NAMES.add("HTML5");
        INTERESTED_JOB_SUBTYPE_NAMES.add("web前端");
        INTERESTED_JOB_SUBTYPE_NAMES.add("JavaScript");
        INTERESTED_JOB_SUBTYPE_NAMES.add("U3D");
        INTERESTED_JOB_SUBTYPE_NAMES.add("COCOS2D-X");
        INTERESTED_JOB_SUBTYPE_NAMES.add("前端开发其它");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网页产品设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网页设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("Flash设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("APP设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("UI设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术设计师（2D/3D）");
        INTERESTED_JOB_SUBTYPE_NAMES.add("广告设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("多媒体设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("原画师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏特效");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏界面设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏场景");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏角色");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏动作");
        INTERESTED_JOB_SUBTYPE_NAMES.add("交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("无线交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网页交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("交互设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("C++");
        INTERESTED_JOB_SUBTYPE_NAMES.add("C");
        INTERESTED_JOB_SUBTYPE_NAMES.add("C#");
        INTERESTED_JOB_SUBTYPE_NAMES.add("全栈工程师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("Ruby");
        INTERESTED_JOB_SUBTYPE_NAMES.add("Node.js");
        INTERESTED_JOB_SUBTYPE_NAMES.add("产品经理");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网页产品经理");
        INTERESTED_JOB_SUBTYPE_NAMES.add("SEO");
    }

    public static void main(String[] args) {
        List<String> urls = LaGouUrlGenerator.getInterestedJobTypeCityUrls();
        System.out.println(urls.size());
        System.out.println(urls);
    }
}
