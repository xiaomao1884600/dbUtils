package com.doubeye.spider.content.analyzer.boss.dictionary;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.content.analyzer.AbstractContentAnalyzer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class BossAllProvinceDictionaryPageAnalyzer extends AbstractContentAnalyzer{
    @Override
    public JSONArray doAnalyze() {
        JSONArray provinces = getProvinceLevel();
        JSONArray cities = getCityLevel();
        return mergeProvinceAndCity(provinces, cities);
    }

    private JSONArray getProvinceLevel() {
        JSONArray provinces = new JSONArray();
        Elements provinceElements = getDocument().getElementsByClass(CLASS_NAME_PROVINCE_ROOT_ELEMENT).first().children();
        provinceElements.forEach(provinceElement -> {
            if (provinceElement.hasAttr(PROPERTY_NAME_PROVINCE_CODE)) {
                JSONObject provinceObject = new JSONObject();
                String code = provinceElement.attr(PROPERTY_NAME_PROVINCE_CODE);
                String name = provinceElement.html();
                provinceObject.put("code", code);
                provinceObject.put("name", name);
                provinces.add(provinceObject);
            }
        });
        return provinces;
    }

    private JSONArray getCityLevel() {
        JSONArray provinces = new JSONArray();
        Elements cityUnderProvinceRootElements = getDocument().getElementsByClass(CLASS_NAME_CITY_ROOT_ELEMENT).first().children();
        for (int i = 1; i < cityUnderProvinceRootElements.size(); i ++) {
            JSONArray cities = new JSONArray();

            Elements cityElements = cityUnderProvinceRootElements.get(i).children();
            cityElements.forEach(cityElement -> {
                JSONObject cityObject = new JSONObject();
                String code = cityElement.attr(PROPERTY_NAME_CITY_CODE);
                String name = Jsoup.parse(cityElement.html()).text();
                cityObject.put("code", code);
                cityObject.put("name", name);
                cities.add(cityObject);
            });

            provinces.add(cities);
        }
        return provinces;
    }

    private JSONArray mergeProvinceAndCity(JSONArray provinces, JSONArray cities) {
        if (provinces.size() != cities.size()) {
            System.out.println("省份和城市的个数不匹配");
            System.out.println("provinces = " + provinces.toString());
            System.out.println("cities = " + cities.toString());
            throw new IllegalArgumentException("省份和城市的个数不匹配");
        }
        for (int i = 0; i < provinces.size(); i ++) {
            JSONObject provinceObject = provinces.getJSONObject(i);
            provinceObject.put("cities", cities.getJSONArray(i));
        }
        return provinces;
    }

    @Override
    public int getCurrentPage() {
        return 1;
    }

    private static final String CLASS_NAME_PROVINCE_ROOT_ELEMENT = "dorpdown-province";
    private static final String CLASS_NAME_CITY_ROOT_ELEMENT = "dorpdown-city";
    private static final String PROPERTY_NAME_PROVINCE_CODE = "ka";
    private static final String PROPERTY_NAME_CITY_CODE = "data-val";
    private static final String URL = "https://www.zhipin.com/";

    public static JSONArray getAllProvinces() {
        BossAllProvinceDictionaryPageAnalyzer analyzer = new BossAllProvinceDictionaryPageAnalyzer();
        analyzer.setContent(UrlContentGetter.getHtml(URL, UrlContentGetter.FIREFOX_HEADER));
        return analyzer.doAnalyze();
    }

    public static void main(String[] args) {
        JSONArray provinces = getAllProvinces();
        System.out.println(provinces);
        System.out.println(provinces.size());
    }
}
