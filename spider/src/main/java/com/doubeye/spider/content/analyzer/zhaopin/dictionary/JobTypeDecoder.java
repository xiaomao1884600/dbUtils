package com.doubeye.spider.content.analyzer.zhaopin.dictionary;


import net.sf.json.JSONObject;

public class JobTypeDecoder extends GeneralKeyValueDecoder {
    @Override
    protected void decode(JSONObject result, String[] entry) {
        result.put("jobTypeClass", entry[2]);
    }
}