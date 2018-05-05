package com.doubeye.spider.content.analyzer.job51.dictionary;

import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 将前程无忧网的职位json转换为JSONArray，前程无忧的职位类型JSON通过以下url获得
 * http://js.51jobcdn.com/in/js/2016/layer/funtype_array_c.js
 */
public class JobTypeDictionaryHelper {
    static JSONArray getAllJobTypes() {
        JSONObject jobTypeObject = null;
        try {
            jobTypeObject = JSONUtils.getJsonObjectFromFile(new File("d:/jobType.json"), "utf-8");
        } catch (IOException e) {
            return new JSONArray();
        }
        return JSONUtils.objectToArray(jobTypeObject, "id", "name");
    }
    public static void main(String[] args) throws IOException {
        JSONArray jobTypes = getAllJobTypes();
        for (int i = 0; i < jobTypes.size(); i ++) {
            JSONObject jobType = jobTypes.getJSONObject(i);
            if (!"其他".equals(jobType.getString("name"))) {
                System.out.println(jobType.getString("name"));
            }
        }
    }
}
