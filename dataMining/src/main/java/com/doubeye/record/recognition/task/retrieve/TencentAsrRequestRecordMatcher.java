package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author doubeye
 * 将腾讯ASR识别请求id与录音编号对应
 * 该类从模板的共享结果中的records属性取得要处理的结果集，并将想对应的RECORD_ID添加到结果中的每条记录中
 */
public class TencentAsrRequestRecordMatcher extends AbstractOperation{
    @Override
    public void run() throws SQLException, Exception {
        Connection conn = getConnection();
        List<String> requestIds = new ArrayList<>();
        JSONObject sharedResult = getSharedResult();
        JSONArray results = sharedResult.getJSONArray(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString());
        for (int i = 0; i < results.size(); i ++) {
            JSONObject record = results.getJSONObject(i);
            requestIds.add(record.getString(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID.toString()));
        }
        if (requestIds.size() > 0) {
            JSONArray matchedArray = ResultSetJSONWrapper.getJSONArrayFromSQL(conn,
                    String.format(SQL_SELECT_RECORD_TASK_UPLOAD_TENCENT,
                            CollectionUtils.split(requestIds,
                                    CommonConstant.SEPARATOR.COMMA.toString(),
                                    CommonConstant.SEPARATOR.SINGLE_QUOTE_MARK.toString())));
            for (int i = 0; i < matchedArray.size(); i++) {
                JSONObject matchPair = matchedArray.getJSONObject(i);
                String requestId = matchPair.getString(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID.toString());
                int index = requestIds.indexOf(requestId);
                if (index >= 0 && index < results.size()) {
                    results.getJSONObject(index).put(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString(),
                            matchPair.getString(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString()));
                }
            }
            //TODO 验证这种方式后，result中的数据是否包含recordid

        }
    }

    private static final String SQL_SELECT_RECORD_TASK_UPLOAD_TENCENT = "SELECT record_id `recordId`, request_id `requestId` FROM record_task_upload_tencent WHERE request_id IN (%s)";

    public static Operation getInstance(Connection conn) {
        TencentAsrRequestRecordMatcher instance = new TencentAsrRequestRecordMatcher();
        instance.setConnection(conn);
        return instance;
    }
}
