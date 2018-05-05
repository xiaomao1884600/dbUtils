package com.doubeye.record.recognition;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author doubeye
 * 识别结果处理接口
 */
public interface RecognitionProcessing {
    /**
     * 设置识别结果
     * @param recognitionResult 识别结果
     */
    public void setRecognitionResult(JSONArray recognitionResult);

    /**
     * 获得识别结果
     * @return 识别结果
     */
    public JSONArray getRecognitionResult();

    /**
     * 设置对话的数据
     * @param data 对话数据
     */
    public void setData(JSONObject data);

    /**
     * 获得对话数据
     * @return 对话数据
     */
    public JSONObject getData();

    /**
     * 预处理
     */
    public void prepareData();

    /**
     * 执行处理
     */
    public void process();

    /**
     * 获得处理结果
     * @return 处理结果
     */
    public JSONObject getProcessResult();

    /**
     * 设置参数
     * @param parameter 参数
     */
    public void setParameter(Map<String, Object> parameter);

    /**
     * 获得参数
     * @return 参数
     */
    public Map<String, Object> getParameter();
}
