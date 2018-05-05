package com.doubeye.spider.content.analyzer.job51.dictionary;

import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧城市，省份助手，所需要的json通过以下url获得
 * http://js.51jobcdn.com/in/js/2016/layer/area_array_c.js
 */
@SuppressWarnings("unused")
public class ProvinceDictionaryHelper {
    /**
     * 获得所有省、直辖市
     * @return 省、直辖市的JSONArray
     */
    static JSONArray getAllProvinces() {
        JSONObject areaObject;
        try {
            areaObject = JSONUtils.getJsonObjectFromFile(new File("d:/areas.json"), "utf-8");
        } catch (IOException e) {
            return new JSONArray();
        }
        JSONArray cities = JSONUtils.objectToArray(areaObject, "id", "name");
        System.out.println(cities.toString());
        System.out.println(cities.size());
        JSONArray provinces = new JSONArray();
        for (int i = 0; i < cities.size(); i ++ ) {
            JSONObject city = cities.getJSONObject(i);
            if (city.getString("id").endsWith("0000") && !"360000".equals(city.getString("id"))) {
                provinces.add(city);
            }
        }
        return provinces;
    }

    private static final String PROVINCE_SUFFIX = "0000";
    private static final String FOREIGN_ID = "360000";
}
