package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author doubeye
 * 将任务表（record_task_upload_tencent）中的识别结果删除，本类将读取模板共享存储中的savedRequestIds属性
 */
public class TencentAsrTaskRemover extends AbstractOperation{

    @Override
    public void run() throws SQLException, Exception {
        JSONObject sharedResult = getSharedResult();
        List<String> savedIds = CollectionUtils.getListFromJSONArray(
                sharedResult.getJSONArray(PropertyNameConstants.PROPERTY_NAME.SAVED_REQUEST_IDS.toString()));
        String savedIdsString = CollectionUtils.split(savedIds,
                CommonConstant.SEPARATOR.COMMA.toString(),
                CommonConstant.SEPARATOR.SINGLE_QUOTE_MARK.toString());
        SQLExecutor.execute(getConnection(), String.format(SQL_DELETE_RECORD_TASK_UPLOAD_TENCENT, savedIdsString));
    }

    private static final String SQL_DELETE_RECORD_TASK_UPLOAD_TENCENT = "DELETE FROM record_task_upload_tencent WHERE request_id in ('%s')";

    public static Operation getInstance(Connection connection) {
        TencentAsrTaskRemover instance = new TencentAsrTaskRemover();
        instance.setConnection(connection);
        return instance;
    }
}
