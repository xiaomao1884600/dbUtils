package com.doubeye.commons.utils.elasticsearch;

import net.sf.json.JSONArray;

/**
 * @author doubeye
 * @version 1.0.0
 * 在从数据库导ES抽取数据是，在保存前需要进行的转换操作
 */
public interface RecordProcessor {
    /**
     * 保存前对结果集进行处理
     * @param records 结果集
     */
    void doProcess(JSONArray records);
}
