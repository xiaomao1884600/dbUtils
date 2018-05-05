package com.doubeye.spider.content.analyzer.tongcheng;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.spider.content.analyzer.tongcheng.dictionary.TongChengDictionary;
import com.doubeye.spider.content.analyzer.tongcheng.dictionary.TongChengDictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TongChengUrlGenerator {
    public static List<String> getInterestedJobTypeCityUrls(boolean today) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < TongChengDictionary.TONG_CHENG_CITY.size(); i ++){
            JSONObject province = TongChengDictionary.TONG_CHENG_CITY.getJSONObject(i);
            String provinceName = province.keys().next().toString();
            JSONArray citiesInProvince = province.getJSONArray(provinceName);
            for (int city = 0; city < citiesInProvince.size(); city ++) {
                String cityCode = citiesInProvince.getJSONObject(city).getString("code");
                INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.forEach(element -> {
                    String[] jobSubtypeCodeName = element.split("\\|");
                    String jobSubtypeCode = jobSubtypeCodeName[0];
                    String jobSubtypeName = jobSubtypeCodeName[1];
                    String condition = jobSubtypeName + "|||" + provinceName;
                    String todayPart = today ? String.format(TODAY_TEMPLATE, DateTimeUtils.today(DATE_FORMAT), DateTimeUtils.tomorrow(DATE_FORMAT)) : "";
                    result.add(String.format(JOB_TYPE_CITY_URL_TEMPLATE, cityCode, jobSubtypeCode, todayPart, condition));
                });
            }
        }
        return result;
    }

    /**
     * 职位类型与城市检索的URL模板，第一个参数为城市的code，第二个参数为jobType的code
     */
    private static final String JOB_TYPE_CITY_URL_TEMPLATE = "http://%s.58.com/%s/%s&bd=1[@%s";
    public static final String PAGE_TEMPLATE = "&page=%s";
    private static final String TODAY_TEMPLATE = "?postdate=%s_%s";
    private static final String DATE_FORMAT = "yyyyMMdd";



    private static final Set<String> INTERESTED_JOB_SUBTYPE_CODE_AND_NAME = new HashSet<>();

    static {
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("dengguangshi|灯光师");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("wtxieying|摄影师/摄像师");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("yingshicehua|影视/后期制作");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("zptaobaomeigong|淘宝美工");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("chuangyizongjian|创意指导/总监");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("fengmiansj|广告设计/制作");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("meibian|美编/美术设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("jiajusj|家具/家居用品设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("sheji|平面设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("meishuzhidao|美术指导");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("zhanshizhuanghuang|店面/陈列/展览设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("gongyisheji|工艺/珠宝设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("duomeiti|多媒体/动画设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("baozhuangsheji|产品/包装设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("shineisheji|装修装潢设计");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("shejizhitu|CAD设计/制图");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("youxisheji|游戏设计/开发");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("wangzhanmeigong|网页设计/制作");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("cadsheji|语音/视频/图形");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("zhaomingsheji|灯光/照明设计工程师");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("cbyishua|排版设计/制作");
        INTERESTED_JOB_SUBTYPE_CODE_AND_NAME.add("fangwusj|园林/景观设计");
    }

    public static void main(String[] args) {
        System.out.println(getInterestedJobTypeCityUrls(true));
        System.out.println(getInterestedJobTypeCityUrls(true).size());
    }
}
