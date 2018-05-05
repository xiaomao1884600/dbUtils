package com.doubeye.spider.content.analyzer.zhaopin;

import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.spider.content.analyzer.zhaopin.dictionary.ZhaopinDictionary;
import com.doubeye.spider.content.analyzer.zhaopin.dictionary.ZhaopinDictionary;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZhaopinUrlGenerator {
    /**
     * 智联招聘行业类别、城市、分页组合条件URL模板，参数为：dIndustry字典中的id，dCity中的名称。页数为&p=%d需要自行添加
     */
    private static final String URL_INDUSTRY_CITY_TEMPLATE = "http://sou.zhaopin.com/jobs/searchresult.ashx?in=%s&jl=%s&isadv=0";
    /**
     * 智联招聘职位类别，城市组合条件URL模板，参数为：ZHAOPIN_JOB_SUBTYPE字典中的jobType,ZHAOPIN_JOB_SUBTYPE中的id，dCity中的名称。页数为&p=%d需要自行添加
     */
    private static final String URL_JOB_SUBTYPE_CITY_TEMPLATE = "http://sou.zhaopin.com/jobs/searchresult.ashx?bj=%s&sj=%s&jl=%s&isadv=0%s[@%s";
    public static final String URL_PAGE_TEMPLATE = "&p=%d";
    public static final String URL_TODAY_TEMPLATE = "&pd=1";

    /**
     * 获得行业和城市组合查询的url，城市将返回中国的省和直辖市
     * 注意：没有拼接分页条件，分页格式为&p=%d，需要自行添加
     * @return 行业和城市组合查询的url
     */
    public static List<String> getIndustryCityUrls() {
        JSONArray cities = getProvinces();
        JSONArray industries = ZhaopinDictionary.ZHAOPIN_INDUSTRY;
        return getIndustryCityUrls(cities, industries);
    }

    public static List<String> getInterestedIndustryProvinceUrls() {
        JSONArray industries = ZhaopinDictionary.ZHAOPIN_INDUSTRY;
        JSONArray provinces = getProvinces();
        JSONArray interestedIndustry = new JSONArray();
        JSONObject industryFilter = new JSONObject();
        INTERESTED_INDUSTRY_NAMES.forEach(name -> {
            industryFilter.put("name", name);
            interestedIndustry.add(JSONUtils.findFirst(industries, industryFilter));
        });
        return getIndustryCityUrls(provinces, interestedIndustry);
    }

    public static List<String> getInterestedJobSubtypeCityUrls(boolean today) {
        JSONArray interestedJobSubtypes = new JSONArray();
        JSONArray provinces = getProvinces();
        JSONArray jobSubtypes = ZhaopinDictionary.ZHAOPIN_JOB_SUBTYPE;
        JSONObject jobSubtypeFilter = new JSONObject();
        INTERESTED_JOB_SUBTYPE_NAMES.forEach(name -> {
            jobSubtypeFilter.put("name", name);
            interestedJobSubtypes.add(JSONUtils.findFirst(jobSubtypes, jobSubtypeFilter));
        });
        return getJobSubtypeCityUrls(provinces, interestedJobSubtypes, today);
    }

    private static JSONArray getProvinces() {
        JSONArray cities = ZhaopinDictionary.ZHAOPIN_CITY;
        JSONObject cityFilter = new JSONObject();
        cityFilter.put("parentId", ZhaopinDictionary.PROVINCE_LEVEL_PARENT_ID);
        return JSONUtils.findAll(cities, cityFilter);

    }

    private static List<String> getIndustryCityUrls(JSONArray cities, JSONArray industries) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < cities.size(); i ++) {
            JSONObject city = cities.getJSONObject(i);
            for (int j = 0; j < industries.size(); j ++) {
                JSONObject industry = industries.getJSONObject(j);
                result.add(String.format(URL_INDUSTRY_CITY_TEMPLATE, industry.getString("id"), city.getString("name")));
            }
        }
        return result;
    }
    private static List<String> getJobSubtypeCityUrls(JSONArray cities, JSONArray jobSubType, boolean today) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < cities.size(); i ++) {
            JSONObject city = cities.getJSONObject(i);
            for (int j = 0; j < jobSubType.size(); j ++) {
                JSONObject subtype = jobSubType.getJSONObject(j);
                String condition = subtype.getString("name") + "|||" + city.get("name");
                result.add(String.format(URL_JOB_SUBTYPE_CITY_TEMPLATE, subtype.getString("jobType"), subtype.getString("id"), city.getString("name"), (today ? URL_TODAY_TEMPLATE : ""), condition));
            }
        }
        return result;
    }



    public static void main(String[] args) {
        //System.out.println(getIndustryCityUrls().size());
        System.out.println(getInterestedJobSubtypeCityUrls(false).size());

    }

    public static final List<String> INTERESTED_INDUSTRY_NAMES = new ArrayList<>();

    static {
        INTERESTED_INDUSTRY_NAMES.add("互联网/电子商务");
        INTERESTED_INDUSTRY_NAMES.add("网络游戏");
        INTERESTED_INDUSTRY_NAMES.add("家居/室内设计/装饰装潢");
        INTERESTED_INDUSTRY_NAMES.add("礼品/玩具/工艺美术/收藏品/奢侈品");
        INTERESTED_INDUSTRY_NAMES.add("媒体/出版/影视/文化传播");
        INTERESTED_INDUSTRY_NAMES.add("娱乐/体育/休闲");
        INTERESTED_INDUSTRY_NAMES.add("广告/会展/公关");
    }

    private static final Set<String> INTERESTED_JOB_SUBTYPE_NAMES = new HashSet<>();

    static {
        INTERESTED_JOB_SUBTYPE_NAMES.add("广告创意/设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("广告创意/设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("广告创意/设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("广告美术指导");
        INTERESTED_JOB_SUBTYPE_NAMES.add("会展策划/设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("WEB前端开发");
        INTERESTED_JOB_SUBTYPE_NAMES.add("用户体验（UE/UX）设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏设计/开发");
        INTERESTED_JOB_SUBTYPE_NAMES.add("游戏界面设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("室内装潢设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("软装设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("橱柜设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("硬装设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("摄影师/摄像师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("后期制作");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术编辑/美术设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("灯光师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("影视策划/制作人员");
        INTERESTED_JOB_SUBTYPE_NAMES.add("艺术/设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("绘画");
        INTERESTED_JOB_SUBTYPE_NAMES.add("原画师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("CAD设计/制图");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("三维/3D设计/制作");
        INTERESTED_JOB_SUBTYPE_NAMES.add("特效设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("视觉设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术编辑/美术设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("多媒体/动画设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("包装设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("家具设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("家居用品设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("工艺品/珠宝设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("玩具设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("店面/展览/展示/陈列设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("工业设计");
        INTERESTED_JOB_SUBTYPE_NAMES.add("园林景观设计师");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计总监");
        INTERESTED_JOB_SUBTYPE_NAMES.add("平面设计经理/主管");
        INTERESTED_JOB_SUBTYPE_NAMES.add("美术教师");

    }
}
