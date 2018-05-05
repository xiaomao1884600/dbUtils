package com.doubeye.temperary;

import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Set;


/**
 * @author doubeye
 * 将阿里云语音识别接口返回的数据按角色拆分
 */
public class AnalyzeResultRoleSeparator {
    public static void main(String[] args) throws IOException {
        String path = "d:/录音to文_阿里云转换结果_xwh_2017_10_17/";
        String filename = path + "xian_ok_data.json";

        JSONObject results = JSONUtils.getJsonObjectFromFile(new File(filename));
        StringBuilder separatedResultBuilder = new StringBuilder();
        Set<?> files = results.keySet();
        for (Object file : files) {
            JSONObject result = results.getJSONObject(file.toString());
            separatedResultBuilder.append(file.toString()).append("\r\n");
            separatedResultBuilder.append(doSeparate(result)).append("\r\n");
        }
        FileUtils.toFile(filename + ".separated", separatedResultBuilder);
    }

    private static String doSeparate(JSONObject result) {
        StringBuilder separatedResult = new StringBuilder();
        StringBuilder[] builders = new StringBuilder[2];
        for (int i = 0; i < builders.length; i ++) {
            builders[i] = new StringBuilder();
        }
        JSONArray sentences = result.getJSONArray("result");
        for (int i = 0; i < sentences.size(); i ++ ) {
            JSONObject sentence = sentences.getJSONObject(i);
            int channelId = sentence.getInt("channel_id");
            builders[channelId].append(sentence.getString("text")).append("\t");
        }
        for (int i = 0; i < builders.length; i ++) {
            separatedResult.append("角色").append(i).append(":").append("\r\n").append(builders[i].toString()).append("\r\n");
        }
        return separatedResult.toString();
    }
}
