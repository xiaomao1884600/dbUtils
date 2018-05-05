package com.doubeye.spider.content.analyzer.zhaopin.dictionary;

import net.sf.json.JSONObject;

public class IndustryDecoder extends GeneralKeyValueDecoder {

    @Override
    protected void decode(JSONObject result, String[] entry) {
        result.put("industryClass", entry[2]);
    }
}
