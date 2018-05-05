package com.doubeye.log.replay.result.comparator;

import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * @author doubeye
 * 个人电话贡献度结果格式化工具
 */
public class AdaContributionResultComparator {

    public JSONArray compare(JSONArray first, JSONArray second) {
        JSONArray result = new JSONArray();
        for(int i = 0; i < first.size(); i ++) {
            JSONObject entryFromFirst = first.getJSONObject(i);
            JSONObject condition = new JSONObject();
            String id = entryFromFirst.getString("id");
            condition.put("id", id);
            JSONObject entryFromSecond = JSONUtils.findFirst(second, condition);
            if (entryFromSecond == null) {
                JSONObject notExistsResult = new JSONObject();
                notExistsResult.put("id", id);
                notExistsResult.put("result", "在第二个中不存在");
                result.add(notExistsResult);
            } else if (!entryFromFirst.toString().equals(entryFromSecond.toString())){
                JSONArray personCallNumberResult = compare(entryFromFirst, entryFromSecond, "person_call_number", "student_id");
                JSONArray personCallLengthResult = compare(entryFromFirst, entryFromSecond, "person_call_length", "student_id");
                JSONArray personCallIntervalResult = compare(entryFromFirst, entryFromSecond, "person_call_interval", "hour");
                JSONObject diff = new JSONObject();
                diff.put("id", id);
                if (personCallNumberResult.size() > 0) {
                    diff.put("personCallNumberResult", personCallNumberResult);
                }
                if (personCallLengthResult.size() > 0) {
                    diff.put("personCallLengthResult", personCallLengthResult);
                }
                if (personCallIntervalResult.size() > 0) {
                    diff.put("personCallIntervalResult", personCallIntervalResult);
                }
                result.add(diff);
            }
        }
        for(int i = 0; i < second.size(); i ++) {
            JSONObject entryFromSecond = second.getJSONObject(i);
            JSONObject condition = new JSONObject();
            String id = entryFromSecond.getString("id");
            condition.put("id", id);
            JSONObject entryFromFirst = JSONUtils.findFirst(first, condition);
            if (entryFromFirst == null) {
                JSONObject notExistsResult = new JSONObject();
                notExistsResult.put("id", id);
                notExistsResult.put("result", "在第一个中不存在");
                result.add(notExistsResult);
            }
        }
        return result;
    }

    public JSONArray compare(JSONObject first, JSONObject second, String propertyName, String idPropertyName) {
        JSONArray firstArray = first.getJSONArray(propertyName);
        JSONArray secondArray = second.getJSONArray(propertyName);
        return compareArray(firstArray, secondArray, idPropertyName);
    }

    /**
     * 对比统计内容
     * @param first
     * @param second
     * @param idPropertyName
     * @return
     */
    private static JSONArray compareArray(JSONArray first, JSONArray second, String idPropertyName) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < first.size(); i++) {
            JSONObject entryFromFirst = first.getJSONObject(i);
            JSONObject condition = new JSONObject();
            String id = entryFromFirst.getString(idPropertyName);
            condition.put(idPropertyName, id);
            JSONObject entryFromSecond = JSONUtils.findFirst(second, condition);
            if (entryFromSecond == null) {
                JSONObject notExistsResult = new JSONObject();
                notExistsResult.put(idPropertyName, id);
                notExistsResult.put("result", "在第二个中不存在");
                result.add(notExistsResult);
            } else {
                if (!entryFromFirst.toString().equals(entryFromSecond.toString())) {
                    JSONObject diff = new JSONObject();
                    diff.put(idPropertyName, id);
                    diff.put("first", entryFromFirst.toString());
                    diff.put("second", entryFromSecond.toString());
                    result.add(diff);
                }
            }
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        JSONArray es = JSONUtils.getJsonObjectFromFile(new File("d:/result/get_personal_contribute_AWExP_Wr8D-OKQzkPbmm_es.txt")).getJSONArray("data");
        JSONArray mysql = JSONUtils.getJsonObjectFromFile(new File("d:/result/get_personal_contribute_AWExP_Wr8D-OKQzkPbmm_mysql.txt")).getJSONArray("data");
        AdaContributionResultComparator comparator = new AdaContributionResultComparator();
        JSONArray result = comparator.compare(es, mysql);
        FileUtils.toFile("d:/result/get_personal_contribute_AWExP_Wr8D-OKQzkPbmm.json", new StringBuilder().append(result.toString()));


    }

}
