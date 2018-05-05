package com.doubeye.record.recognition.task.post;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * 将成功上传的识别任务保存在任务表中，表名为record_task_upload_tencent
 * 该操作依赖于模板中的uploaded属性，格式为JSONArray，保存的内容包括任务的id以及录音id
 * @see TencentAsrTaskPoster
 */
public class TencentAsrTaskSaver extends AbstractOperation{
    @Override
    public void run() {
        JSONObject sharedResult = getSharedResult();
        if (sharedResult.containsKey(PropertyNameConstants.PROPERTY_NAME.UPLOADED.toString())) {
            JSONArray records = sharedResult.getJSONArray(PropertyNameConstants.PROPERTY_NAME.UPLOADED.toString());
            for (int i = 0; i < records.size(); i ++) {
                JSONObject record = records.getJSONObject(i);
                try {
                    SQLExecutor.execute(getConnection(), String.format(SQL_INSERT_TASK_UPLOAD,
                            record.getString(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString()),
                            record.getLong(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID.toString())));
                } catch (SQLException e) {
                    //TODO log exception
                    e.printStackTrace();
                }
            }
        }
    }

    public static Operation getInstance(Connection conn) {
        TencentAsrTaskSaver taskSaver = new TencentAsrTaskSaver();
        taskSaver.setConnection(conn);
        return taskSaver;
    }

    private static final String SQL_INSERT_TASK_UPLOAD = "INSERT INTO record_task_upload_tencent(record_id, request_id) values('%s', %d) ON DUPLICATE KEY UPDATE\n" +
            " request_id = VALUES(request_id)";

}
