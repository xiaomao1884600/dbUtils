package com.hxsd.services.productLine.dataMining.recordAnalyze;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 智能语音分析服务
 * TODO 将所有的V2转换为正常，删除原有正常代码，升版本，+注释
 */
@SuppressWarnings("unused")
public class RecordAnalyzeService {


    public static  JSONArray getRecordsByCampus(Map<String, String[]> parameters) throws IOException {
        JSONArray result = new JSONArray();
        String campus = RequestHelper.getString(parameters, "campus");
        JSONObject results = CollectionUtils.toJSONArray(getResultFile(campus)).getJSONObject(0);

        Set<?> files = results.keySet();
        for (Object key : files) {
            String fileName = key.toString();
            result.add(getRecordEntry(results.getJSONObject(fileName), fileName, campus));
        }
        return result;
    }

    //private static final String RESULT_PATH = "C:\\Users\\zhanglu1782\\Desktop\\语音分析\\录音to文_阿里云转换结果_xwh_2017_10_10字\\录音to文字\\";
    private static final String RESULT_PATH = "C:\\Users\\zhanglu1782\\Desktop\\语音分析\\录音to文_阿里云转换结果_xwh_2017_10_17\\";
    private static final String RESULT_FILE_TEMPLATE = "%s_ok_data.json";
    private static final String OSS_ROOT_TEMPLATE = "http://edu-feedback-2.oss-cn-beijing.aliyuncs.com/record/%s/";

    private static String getResultFile(String campus) {
        return (RESULT_PATH + String.format(RESULT_FILE_TEMPLATE, campus));
    }


    private static JSONObject getRecordEntry(JSONObject recordInFile, String fileName, String campus) {
        JSONObject result = new JSONObject();
        result.put("file", String.format(OSS_ROOT_TEMPLATE, campus) + fileName);
        result.put("texts", recordInFile.getJSONArray("result"));
        return result;
    }

}
