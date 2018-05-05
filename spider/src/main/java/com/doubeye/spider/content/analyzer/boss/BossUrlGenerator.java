package com.doubeye.spider.content.analyzer.boss;

import com.doubeye.spider.content.analyzer.boss.dictionary.BossDictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BossUrlGenerator {
    public static List<String> getInterestedJobTypeCityUrls() {
        List<String> result = new ArrayList<>();
        INTERESTED_JOB_SUBTYPE_NAMES.forEach(jobType -> {
            JSONArray provinces = BossDictionary.BOSS_CITY;
            for (int i = 0; i < provinces.size(); i ++ ){
                JSONObject province = provinces.getJSONObject(i);
                JSONArray cities = province.getJSONArray("cities");
                for (int j = 0; j < cities.size(); j ++) {
                    String condition = jobType + "|||" + cities.getJSONObject(j).getString("name");
                    result.add(String.format(JOB_TYPE_CITY_URL_TEMPLATE, jobType, cities.getJSONObject(j).getString("code"), condition));
                }
            }
        });
        return result;
    }

    /**
     * 职位类型与城市检索的URL模板，第一个参数为职位名称，第二个参数为cityCode
     */
    private static final String JOB_TYPE_CITY_URL_TEMPLATE = "https://www.zhipin.com/job_detail/?query=%s&scity=%s&source=2[@%s";
    public static final String PAGE_TEMPLATE = "&page=%s";

    private static final Set<String> INTERESTED_JOB_SUBTYPE_NAMES = new HashSet<>();

    static {
        INTERESTED_JOB_SUBTYPE_NAMES.add("HTML5");
        INTERESTED_JOB_SUBTYPE_NAMES.add("web前端");
        INTERESTED_JOB_SUBTYPE_NAMES.add("JavaScript");
        INTERESTED_JOB_SUBTYPE_NAMES.add("U3D");
        INTERESTED_JOB_SUBTYPE_NAMES.add("COCOS2DX");
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
        INTERESTED_JOB_SUBTYPE_NAMES.add("三维/CAD/制图");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美工");
        INTERESTED_JOB_SUBTYPE_NAMES.add("包装设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("设计师助理");
        INTERESTED_JOB_SUBTYPE_NAMES.add("动画设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("插画师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("无线交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("网页交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("硬件交互设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("UX设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计经理");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计总");
        INTERESTED_JOB_SUBTYPE_NAMES.add("交互设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("交互设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("工业设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("橱柜设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("家具设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("家居设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("珠宝设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("室内设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("陈列设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("景观设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("广告创意");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术指导");
        INTERESTED_JOB_SUBTYPE_NAMES.add("摄影/影像师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视频编辑");
        INTERESTED_JOB_SUBTYPE_NAMES.add("后期制作");
        INTERESTED_JOB_SUBTYPE_NAMES.add("影视制作");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术教师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("Unity 3D培训讲师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("Web前端培训讲师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("动漫培训讲师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("UI设计培训讲师");

        INTERESTED_JOB_SUBTYPE_NAMES.add("建筑设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("园林设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("内外饰设计工程师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("UI设计培训讲师");

    }

    public static void main(String[] args)  {
        System.out.println(getInterestedJobTypeCityUrls());
        System.out.println(getInterestedJobTypeCityUrls().size());
        //File imageFile = new File("D:/2_2.jpg");
        //Tesseract instance = new Tesseract();

        //instance.setDatapath("D:\\workcode\\dbUtils\\experiments\\src\\main\\resources");

        //将验证码图片的内容识别为字符串
        //System.out.println(instance.doOCR(imageFile));
    }
}
