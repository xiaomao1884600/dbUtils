package com.doubeye.spider.content.analyzer.zhaopin.dictionary;

import com.doubeye.commons.utils.collection.zhaopin.KeyValueDecoder;
import net.sf.json.JSONObject;

import java.util.Arrays;

public class GeneralKeyValueDecoder implements KeyValueDecoder {
    private int valueCount = 3;

    @Override
    public JSONObject decode(String[] entry) {
        if (entry.length != valueCount) {
            throw new RuntimeException("输入的字符串格式不符：" + Arrays.toString(entry));
        } else {
            JSONObject keyValue = new JSONObject();
            keyValue.put("id", entry[0]);
            keyValue.put("name", entry[1]);
            decode(keyValue, entry);
            return keyValue;
        }
    }

    protected void decode(JSONObject result, String[] entry) {

    }
    @Override
    public void setValueCount(int valueCount) {
        this.valueCount = valueCount;
    }
}
