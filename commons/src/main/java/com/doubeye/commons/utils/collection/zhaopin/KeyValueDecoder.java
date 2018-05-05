package com.doubeye.commons.utils.collection.zhaopin;

import net.sf.json.JSONObject;

public interface KeyValueDecoder {
    public JSONObject decode(String[] entry);
    public void setValueCount(int valueCount);
}
