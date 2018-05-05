package com.hxsd.services.productLine.dataMining.recordAnalyze;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 录音相关日志文件清理服务
 */
public class LogManagerService {
    public JSONObject purgeLog(Map<String, String[]> parameters) throws Exception {
        String[] tables = RequestHelper.getString(parameters, "tables").split(",");
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        String purgeDate = RequestHelper.getString(parameters, "purgeDate");
        try (Connection conn = ConnectionManager.getConnection(dataSource, encryptKey)) {
            for (String table : tables) {
                //TODO 将打印替换为真正的执行SQLExecutor.execute(conn, String.format(SQL_DELETE_TABLE, table, purgeDate));
                System.out.println(String.format(SQL_DELETE_TABLE, table, purgeDate));
            }
        }
        return ResponseHelper.getSuccessObject();
    }

    private static final String SQL_DELETE_TABLE = "DELETE FROM %s WHERE created_at <= '%s'";
}
