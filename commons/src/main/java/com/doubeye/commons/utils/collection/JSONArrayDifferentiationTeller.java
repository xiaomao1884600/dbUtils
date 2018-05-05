package com.doubeye.commons.utils.collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 比较两个JSONArray的异同
 */
public class JSONArrayDifferentiationTeller {
    /**
     * 每个对象的key值，改值用来判断那两个对象进行比较
     */
    private String objectKeyPropertyName;
    /**
     * 第一个JSONArray
     */
    private JSONArray array1;
    /**
     * 第二个JSONArray
     */
    private JSONArray array2;
    /**
     * 在两个Array中都存在的键
     */
    private List<String> bothKeys = new ArrayList<>();
    /**
     * 数据在两个Array中相等的键
     */
    private List<String> identicalKeys = new ArrayList<>();
    /**
     * 仅在第一个Array中存在的键
     */
    private List<String> keyOnlyInArray1 = new ArrayList<>();
    /**
     * 仅在第二个Array中存在的键
     */
    private List<String> keyOnlyInArray2 = new ArrayList<>();
    /**
     * 在两个Array中都存在，但内容不等的键及信息
     */
    private List<Map<String, JSONObject>> diffs = new ArrayList<>();
    /**
     * 查看compare方法是否被执行，用来让获得结果前进行执行compare方法
     */
    private boolean compareExecuted = false;

    /**
     * 执行比较
     *
     * 对于Array中每个JSONObject来说
     * 1. 首先查看是否在另外一个Array中存在
     * 2. 如果存在，则比较两个JSONObject的字符串形式是否相等，并记录结果
     * 3. 如果不存在，则记录key只在第一个Array中存在
     */
    public void compare() {
        //以第一个Array为基础进行比较
        for (int i = 0; i < array1.size(); i ++) {
            JSONObject obj1 = array1.getJSONObject(i);
            String obj1Key = obj1.getString(objectKeyPropertyName);
            for (int j = 0; j < array2.size(); j ++) {
                JSONObject obj2 = array2.getJSONObject(j);
                String obj2Key = obj2.getString(objectKeyPropertyName);
                if (obj1Key.equals(obj2Key)) {
                    bothKeys.add(obj1Key);
                    if (obj1.toString().trim().equals(obj2.toString().trim())) {
                        identicalKeys.add(obj1Key);
                    } else {
                        Map<String, JSONObject> diff = new HashMap<>();
                        diff.put("obj1", obj1);
                        diff.put("obj2", obj2);
                        diffs.add(diff);
                    }
                    break;
                }
            }
            if (!bothKeys.contains(obj1Key)) {
                keyOnlyInArray1.add(obj1Key);
            }
        }
        //然后比较第二个结果集，此时仅需要比较不同的部分就可以了
        for (int i = 0; i < array2.size(); i ++) {
            JSONObject obj2 = array2.getJSONObject(i);
            String obj2Key = obj2.getString(objectKeyPropertyName);
            //如果已经是相等的，则无需在比较了
            if (identicalKeys.contains(obj2Key)) {
                continue;
            }
            // 如果不是两个都包含的话，则仅obj2相同
            if (!bothKeys.contains(obj2Key)) {
                keyOnlyInArray2.add(obj2Key);
            }
        }
        compareExecuted = true;
    }
    @SuppressWarnings("unused")
    public String getObjectKeyPropertyName() {
        return objectKeyPropertyName;
    }

    public void setObjectKeyPropertyName(String objectKeyPropertyName) {
        this.objectKeyPropertyName = objectKeyPropertyName;
    }

    public void setArray1(JSONArray array1) {
        this.array1 = array1;
        compareExecuted = false;
    }

    public void setArray2(JSONArray array2) {
        this.array2 = array2;
        compareExecuted = false;
    }

    public List<String> getBothKeys() {
        if (!compareExecuted) {
            compare();
        }
        return bothKeys;
    }

    public List<String> getIdenticalKeys() {
        if (!compareExecuted) {
            compare();
        }
        return identicalKeys;
    }

    public List<String> getKeyOnlyInArray1() {
        if (!compareExecuted) {
            compare();
        }
        return keyOnlyInArray1;
    }

    public List<String> getKeyOnlyInArray2() {
        if (!compareExecuted) {
            compare();
        }
        return keyOnlyInArray2;
    }

    public List<Map<String, JSONObject>> getDiffs() {
        if (!compareExecuted) {
            compare();
        }
        return diffs;
    }

    /**
     * 判断传入的两个Array是否相等
     * @return 两个Array是否相等
     */
    public boolean equal() {
        if (!compareExecuted) {
            compare();
        }
        return (getKeyOnlyInArray1().size() + getKeyOnlyInArray2().size() + getDiffs().size()) == 0;
    }
}
