package com.hxsd.services.productLine.e.toBespokeInfoService;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.productLine.studentInfo.e.toBespokeInfo.AssignLog;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/22.
 */
public class AssignLogService {
    public static JSONArray getAssignLogsByRange(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            String start = RequestHelper.getString(parameters, "start");
            String end = RequestHelper.getString(parameters, "end");
            AssignLog assignLog = new AssignLog();
            assignLog.setConn(conn);
            return assignLog.getAssignLogByRange(start, end);
        }finally {
            ConnectionHelper.close(conn);
        }
    }

    public static JSONArray getAssignLogsByStudentId(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            String studentId = RequestHelper.getString(parameters, "studentId");
            AssignLog assignLog = new AssignLog();
            assignLog.setConn(conn);
            return assignLog.getAssignLogByStudentId(studentId);
        }finally {
            ConnectionHelper.close(conn);
        }
    }
}
