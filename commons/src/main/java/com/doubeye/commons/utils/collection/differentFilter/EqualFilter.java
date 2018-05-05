package com.doubeye.commons.utils.collection.differentFilter;

import com.doubeye.commons.utils.collection.JSONObjectDifferentiationTeller;
import net.sf.json.JSONObject;

import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 */
public class EqualFilter implements DifferentFilter{
    /**
     * 比较结果
     */
    private JSONObject compareResult;

    /**
     * 构造器
     * @param compareResult 计较结果
     */
    public EqualFilter(JSONObject compareResult) {
        this.compareResult = compareResult;
    }


    @Override
    public JSONObject doFilter() {
        JSONObject result = new JSONObject();
        doFilter(compareResult, result, "");
        return result;
    }

    /**
     * 执行过滤结果
     * @param origin 过滤钱的结果
     * @param result 过滤结果
     * @param path 递归路径
     */
    private static void doFilter(JSONObject origin, JSONObject result, String path) {
        Set keySet = origin.keySet();
        for (Object keyObject : keySet) {
            String key = keyObject.toString();
            if (origin.get(key) instanceof JSONObject) {
                JSONObject object = origin.getJSONObject(key);
                /* 相等被过滤掉，因此这里被注释掉了
                if (object.containsKey(JSONObjectDifferentiationTeller.PROPERTY_RESULT_EQUAL) && object.getBoolean(JSONObjectDifferentiationTeller.PROPERTY_RESULT_EQUAL)) {
                    // doNothing
                }
                */
                //仅存在与第一个对象或第二个对象
                if (object.containsKey(JSONObjectDifferentiationTeller.PROPERTY_ONLY_EXISTS_IN_THE_FIRST) || object.containsKey(JSONObjectDifferentiationTeller.PROPERTY_ONLY_EXISTS_IN_THE_SECOND)) {
                    result.put(key, object);
                } else if (object.containsKey(JSONObjectDifferentiationTeller.PROPERTY_RESULT_NOT_EQUAL) && object.getBoolean(JSONObjectDifferentiationTeller.PROPERTY_RESULT_NOT_EQUAL)) {
                    //不等的结果包含JSONObjectDifferentiationTeller.NOT_EQUAL_PROPERTY_NUMBER数量的属性
                    if (object.keySet().size() == JSONObjectDifferentiationTeller.NOT_EQUAL_PROPERTY_NUMBER) {
                        result.put(key, object);
                    } else {
                        JSONObject nestedResult = new JSONObject();
                        doFilter(object, nestedResult, path + "/" + key);
                        result.put(key, nestedResult);
                    }
                }
            }
        }
    }
}
