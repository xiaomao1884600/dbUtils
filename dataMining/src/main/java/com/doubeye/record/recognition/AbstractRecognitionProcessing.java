package com.doubeye.record.recognition;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author doubeye
 * 抽象的识别结果处理类
 */
public abstract class AbstractRecognitionProcessing implements RecognitionProcessing{

    private JSONArray recognitionResult;
    private JSONObject data;
    private JSONObject processResult;
    Map<String, Object> parameter;

    @Override
    public void setRecognitionResult(JSONArray recognitionResult) {
        this.recognitionResult = recognitionResult;
    }

    @Override
    public JSONArray getRecognitionResult() {
        return recognitionResult;
    }

    @Override
    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject getData() {
        return data;
    }

    @Override
    public void prepareData() {
        //无需进行预处理
    }


    @Override
    public JSONObject getProcessResult() {
        return processResult;
    }

    @Override
    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }

    @Override
    public Map<String, Object> getParameter() {
        return parameter;
    }
}
