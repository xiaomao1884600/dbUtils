package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import com.sun.org.apache.regexp.internal.RE;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * 保存腾讯能识别结果，该类直接依赖与TencentAsrResultFormatter,
 * 该类读取模板中的results属性，并将其中的识别结果保存到数据表中（record_analyze_tencent）
 * 该类将所有保存成功的请求id保存在模板共享结果集中的savedRequestIds属性，方便后续处理
 * @see TencentAsrResultFormatter
 */
public class TencentAsrResultSaver extends AbstractOperation{
    @Override
    public void run() {
        Connection conn = getConnection();
        List<String> requestIds = new ArrayList<>();
        JSONObject sharedResult = getSharedResult();
        JSONArray results = sharedResult.getJSONArray(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString());
        for (int i = 0; i < results.size(); i ++) {
            try {
                JSONObject record = results.getJSONObject(i);
                if (record.containsKey(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString())) {
                    if (record.containsKey(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString())) {
                        SQLExecutor.execute(conn, String.format(SQL_INSERT_RECORD_ANALYZE_TENCENT,
                                record.getString(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString()),
                                record.getString(PropertyNameConstants.PROPERTY_NAME.CONVERSATION.toString())));
                    }
                    requestIds.add(record.getString(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID.toString()));
                }
            } catch (SQLException e) {
                //todo log SQL Exception
            }
        }
        sharedResult.put(PropertyNameConstants.PROPERTY_NAME.SAVED_REQUEST_IDS.toString(),
                JSONArray.fromObject(requestIds));
    }


    public static Operation getInstance(Connection conn) {
        TencentAsrResultSaver instance = new TencentAsrResultSaver();
        instance.setConnection(conn);
        return instance;
    }

    private static final String SQL_INSERT_RECORD_ANALYZE_TENCENT = "INSERT INTO record_analyze_tencent(record_id, result) values('%s', '%s') ON DUPLICATE KEY UPDATE\n" +
            " result = VALUES(result)";
}
