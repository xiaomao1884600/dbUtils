package com.hxsd.services.productLine.e;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/1/11.
 */
public class SlowLogService {
    private static final String SQL_SELECT_SLOW_LOG = "select start_time, query_time, lock_time, rows_sent, rows_examined, db, last_insert_id, insert_id, server_id,thread_id  ,convert(sql_text, char) sql_text  from slow_log";

    public JSONArray getSlowLogs(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection("SLOW-LOG", encryptKey);
            String orderClause = " ";
            if (parameters.containsKey("order")) {
                JSONArray orderArray = JSONArray.fromObject(RequestHelper.getString(parameters, "order"));
                for (int i = 0; i < orderArray.size(); i ++) {
                    JSONObject obj = orderArray.getJSONObject(i);
                    orderClause += obj.getString("dataId") + " " + obj.getString("order") + ", ";
                }
                orderClause = " ORDER BY " + orderClause.substring(0, orderClause.lastIndexOf(", "));;
            }

            String pageClause = " ";
            if (parameters.containsKey("page")) {
                JSONObject pageObject = JSONObject.fromObject(RequestHelper.getString(parameters, "page"));
                int minRowNumber = pageObject.getInt("minRowNumber");
                int maxRowNumber = pageObject.getInt("maxRowNumber");
                maxRowNumber = maxRowNumber - minRowNumber + 1;
                pageClause = " LIMIT " + minRowNumber + ", " + maxRowNumber;
            }
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_SLOW_LOG + orderClause + pageClause);
        } finally {
            ConnectionHelper.close(conn);
        }
    }
}
