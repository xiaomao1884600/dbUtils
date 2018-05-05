package com.doubeye.spider.content.analyzer.zhaopin.dictionary;

import net.sf.json.JSONObject;

public class JobSubtypeDecoder extends GeneralKeyValueDecoder {
    @Override
    protected void decode(JSONObject result, String[] entry) {
        result.put("jobType", entry[2]);
    }
}
