package com.doubeye.commons.utils.collection;

import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 用来比较两个JSONObject是否相等
 */
public class JSONObjectDifferentiationTeller {
    // private List<String> bothRootKey;
    private JSONObject object1;
    private JSONObject object2;
    private JSONObject compareResult = new JSONObject();
    public void compare() {
        compare(object1, object2, compareResult, "/");
    }

    //需要定义一个对比结果结构
    private static void compare(JSONObject firstObject, JSONObject secondObject, JSONObject compareResult, String path) {
        //List<String> keysInBothObject = new ArrayList<>();
        Set firstKeySet = firstObject.keySet();
        for (Object keyObject : firstKeySet) {
            String key = keyObject.toString();
            if (secondObject.containsKey(key)) {
                //keysInBothObject.add(key);
                Object obj1 = firstObject.get(key);
                Object obj2 = secondObject.get(key);
                if (obj1.equals(obj2)) {
                    saveEqual(path, key, compareResult);
                } else {
                    if (obj1 instanceof JSONArray) {
                        //同时是数组
                        if ((obj2 instanceof JSONArray)) {
                            saveNotEqual(path, key, compareResult, obj1, obj2);
                        }
                    } else if (obj1 instanceof JSONObject) {
                        saveNotEqual(path, key, compareResult);
                        // 当两个对象都是JSONObject
                        //if (obj2 instanceof JSONObject && !(obj2 instanceof JSONArray)) {
                        if (obj2 instanceof JSONObject) {
                            compare((JSONObject) obj1, (JSONObject) obj2, compareResult, path + "/" + key);
                        } else {
                            //第一个为JSONObject，第二个不是时，直接认为不等，并给出不等结果
                            saveNotEqual(path, key, compareResult, obj1, obj2);
                        }
                    } else {
                        // 简单类型不等
                        saveNotEqual(path, key, compareResult, obj1, obj2);
                    }
                }
            } else {
                //仅在第一个对象中存在
                saveOnlyInFirst(path, key, compareResult);
            }
        }
        Set secondKeySet = secondObject.keySet();
        for (Object keyObject : secondKeySet) {
            String key = keyObject.toString();
            if (!firstKeySet.contains(key)) {
                saveOnlyInSecond(path, key, compareResult);
            }
        }
    }

    /**
     * 保存不相等的比较结果
     * @param path 不相等的key路径
     * @param key key
     * @param compareResult 比较结果对象
     * @param firstObjectString 第一个对象的字符串
     * @param secondObjectString 第二个对象的字符串
     */
    private static void saveNotEqual(String path, String key, JSONObject compareResult, Object firstObjectString, Object secondObjectString) {
        JSONObject node = JSONUtils.findResultObjectByPathAndKey(compareResult, path);
        JSONObject result = new JSONObject();
        result.put(PROPERTY_RESULT_NOT_EQUAL, true);
        JSONObject differentObject = new JSONObject();
        differentObject.put(PROPERTY_FIRST_OBJECT_VALUE, firstObjectString.toString());
        differentObject.put(PROPERTY_SECOND_OBJECT_VALUE, secondObjectString.toString());
        result.put(PROPERTY_DIFFERENCES, differentObject);
        node.put(key, result);
    }

    /**
     * 保存比较结果
     * @param path 比较结果的路径
     * @param key key
     * @param propertyName 结果属性名，定义了结果类型
     * @param compareResult 比较结果对象
     */
    private static void saveResult(String path, String key, String propertyName, /*boolean value,*/ JSONObject compareResult) {
        JSONObject node = JSONUtils.findResultObjectByPathAndKey(compareResult, path);
        JSONObject result = new JSONObject();
        result.put(propertyName, true/*value*/);
        node.put(key, result);
    }

    /**
     * 保存相等结果
     * @param path 结果路径
     * @param key key
     * @param compareResult 比较结果对象
     */
    private static void saveEqual(String path, String key, JSONObject compareResult) {
        saveResult(path, key, PROPERTY_RESULT_EQUAL, /*true*,*/ compareResult);
    }

    /**
     * 保存不等的结果
     * @param path 比较路径
     * @param key key
     * @param compareResult 比较结果对象
     */
    private static void saveNotEqual(String path, String key, JSONObject compareResult) {
        saveResult(path, key, PROPERTY_RESULT_NOT_EQUAL, /*true*,*/ compareResult);
    }

    /**
     * 保存在进存在第一个对象的结果
     * @param path 比较路径
     * @param key key
     * @param compareResult 比较结果对象
     */
    private static void saveOnlyInFirst(String path, String key, JSONObject compareResult) {
        saveResult(path, key, PROPERTY_ONLY_EXISTS_IN_THE_FIRST, /*true*,*/ compareResult);
    }
    /**
     * 保存在进存在第二个对象的结果
     * @param path 比较路径
     * @param key key
     * @param compareResult 比较结果对象
     */
    private static void saveOnlyInSecond(String path, String key, JSONObject compareResult) {
        saveResult(path, key, PROPERTY_ONLY_EXISTS_IN_THE_SECOND, /*true*,*/ compareResult);
    }

    /**
     * 相等的比较结果属性名
     */
    @SuppressWarnings("WeakerAccess")
    public static final String PROPERTY_RESULT_EQUAL = "_equals";
    /**
     * 不等的比较结果属性名
     */
    public static final String PROPERTY_RESULT_NOT_EQUAL = "_notEquals";
    /**
     * 不等比较结果中不同属性的第一个对象值的属性名
     */
    @SuppressWarnings("WeakerAccess")
    public static final String PROPERTY_FIRST_OBJECT_VALUE = "_firstObjectValue";

    /**
     * 不等比较结果中不同属性的第二个对象值的属性名
     */
    @SuppressWarnings("WeakerAccess")
    public static final String PROPERTY_SECOND_OBJECT_VALUE = "_secondObjectValue";

    /**
     * 不等的比较结果下不同结果的属性名
     */
    @SuppressWarnings("WeakerAccess")
    public static final String PROPERTY_DIFFERENCES = "_differences";
    /**
     * 属性仅存在第一个对象比较结果属性名
     */
    public static final String PROPERTY_ONLY_EXISTS_IN_THE_FIRST = "_onlyExistsInTheFirst";
    /**
     * 属性仅存在第二个对象比较结果属性名
     */
    public static final String PROPERTY_ONLY_EXISTS_IN_THE_SECOND = "_onlyExistsInTheSecond";
    /**
     * 不等比较结果的不等属性个数
     */
    public static final int NOT_EQUAL_PROPERTY_NUMBER = 2;

    /* TODO 放到单元测试中
    public static void main(String[] args) {
        String str1 = "{\"success\":true,\"errorMessage\":\"\",\"errorCode\":200,\"data\":{\"workstatus\":0,\"studentgradecount\":{\"noenrolled\":{\"total\":69716,\"enrolllevel\":{\"B\":61327,\"C\":3963,\"D\":1971,\"A\":2455}},\"enrolled\":46628,\"recover\":1954},\"unassignedstudent\":{\"allcampus\":[],\"total\":0},\"remindfeedback\":0}}";
        String str2 = "{\"success\":true,\"errorMessage\":\"\",\"errorCode\":200,\"data\":{\"workstatus\":0,\"studentgradecount\":{\"nglfdjfldjaoenrolled\":{\"total\":69716,\"enrolllevel\":{\"B\":61327,\"C\":3963,\"D\":1971}},\"enrolled\":45675,\"recover\":1954},\"unassignedstudent\":{\"allcampus\":['1'],\"total\":0},\"remindfeedback\":0}}";
        JSONObject obj1 = JSONObject.fromObject(str1);
        JSONObject obj2 = JSONObject.fromObject(str2);

        JSONObjectDifferentiationTeller teller = new JSONObjectDifferentiationTeller();
        teller.setObject1(obj1);
        teller.setObject2(obj2);
        teller.compare();
        JSONObject result = teller.getCompareResult();
        System.out.println(result.toString());
    }
    */
    public void setObject1(JSONObject object1) {
        this.object1 = object1;
    }

    public void setObject2(JSONObject object2) {
        this.object2 = object2;
    }

    public JSONObject getCompareResult() {
        return compareResult;
    }

}
