package com.doubeye.spider.content.analyzer;

import net.sf.json.JSONObject;

/**
 * 每个职位抓取结果的id获得器，用来唯一确认一条记录
 */
public interface RecordIdGetter {
    String getId(JSONObject jobObject);
}
