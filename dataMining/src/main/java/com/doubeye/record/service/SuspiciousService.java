package com.doubeye.record.service;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.Map;

/**
 * @author doubeuye
 * 查询可以电话的服务
 */
@SuppressWarnings("unused")
public class SuspiciousService {
    private static final String SQL_MULTI_CLIENT = "SELECT\n" +
            "\t\n" +
            "\ti.student_mobile,\n" +
            "\tcount(DISTINCT i.user_mobile ) AS t,\n" +
            "\tgroup_concat(DISTINCT i.username) username,\n" +
            "\tcount(*) t,\n" +
            "\tsum(i.billable) s,\n" +
            "\n" +
            "\tsum(i.billable) / count(DISTINCT i.user_mobile ) a\n" +
            "\t\n" +
            "FROM\n" +
            "\trecord_info i \n" +
            "WHERE\n" +
            "\ti.datetime BETWEEN '%s'\n" +
            "\tAND '%s'\n" +
            "\tAND i.campus_id = %s\n" +
            "\tAND i.monitorpath <> ''\n" +
            "\n" +
            "GROUP BY\n" +
            "\ti.student_mobile\n" +
            "\n" +
            "HAVING\n" +
            "\tt > 1\n" +
            "order by t desc";
    private static final String SQL_MULTI_ADA = "SELECT\n" +
            "\n" +
            "\ti.user_mobile,\n" +
            "\ti.username,\n" +
            "\ti.student_mobile,\n" +
            "\ti.studentid,\n" +
            "\ti.studentname,\n" +
            "\tcount( * ) AS t,\n" +
            "\t\n" +
            "\tsum(i.billable) s,\n" +
            "\tsum(i.billable) / count( * ) a\n" +
            "\t\n" +
            "FROM\n" +
            "\trecord_info i \n" +
            "WHERE\n" +
            "\ti.datetime BETWEEN '%s' \n" +
            "\tAND '%s' \n" +
            "\tAND i.campus_id = %s\n" +
            "\tAND i.monitorpath <> ''\n" +
            "GROUP BY\n" +
            "\ti.userid,\n" +
            "\ti.student_mobile\n" +
            "HAVING\n" +
            "\tcount( i.student_mobile ) > 2\n" +
            "order by i.userid, t desc";

    private static final String SQL_MULTI_CLIENT_WITH_PAGE = "SELECT SQL_CALC_FOUND_ROWS\n" +
            "\t\n" +
            "\ti.student_mobile,\n" +
            "\tcount(DISTINCT i.user_mobile ) AS t,\n" +
            "\tgroup_concat(DISTINCT i.username) username,\n" +
            "\tcount(*) t,\n" +
            "\tsum(i.billable) s,\n" +
            "\n" +
            "\tsum(i.billable) / count(DISTINCT i.user_mobile ) a\n" +
            "\t\n" +
            "FROM\n" +
            "\trecord_info i \n" +
            "WHERE\n" +
            "\ti.datetime BETWEEN '%s'\n" +
            "\tAND '%s'\n" +
            "\tAND i.campus_id = %s\n" +
            "\tAND i.monitorpath <> ''\n" +
            "\n" +
            "GROUP BY\n" +
            "\ti.student_mobile\n" +
            "\n" +
            "HAVING\n" +
            "\tt > 1\n" +
            "order by t desc LIMIT %d, %d";

    private static final String SQL_MULTI_ADA_WITH_PAGE = "SELECT SQL_CALC_FOUND_ROWS\n" +
            "\n" +
            "\ti.user_mobile,\n" +
            "\ti.username,\n" +
            "\ti.student_mobile,\n" +
            "\ti.studentid,\n" +
            "\ti.studentname,\n" +
            "\tcount( * ) AS t,\n" +
            "\t\n" +
            "\tsum(i.billable) s,\n" +
            "\tsum(i.billable) / count( * ) a\n" +
            "\t\n" +
            "FROM\n" +
            "\trecord_info i \n" +
            "WHERE\n" +
            "\ti.datetime BETWEEN '%s' \n" +
            "\tAND '%s' \n" +
            "\tAND i.campus_id = %s\n" +
            "\tAND i.monitorpath <> ''\n" +
            "GROUP BY\n" +
            "\ti.userid,\n" +
            "\ti.student_mobile\n" +
            "HAVING\n" +
            "\tcount( i.student_mobile ) > 2\n" +
            "order by t desc LIMIT %d, %d";

    private static final String SQL_SELECT_TOTAL_COUNT = "select FOUND_ROWS() rowCount";

    public JSONObject getSuspiciousInfo(Map<String, String[]> parameters) throws Exception {
        String startTime = RequestHelper.getString(parameters, "startTime");
        String endTime = RequestHelper.getString(parameters, "endTime");
        String campusId = RequestHelper.getString(parameters, "campusId");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            JSONArray multiAda = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_MULTI_ADA, startTime, endTime, campusId));
            JSONArray multiClient = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_MULTI_CLIENT, startTime, endTime, campusId));
            JSONObject data = new JSONObject();
            data.put("multiAda", multiAda);
            data.put("multiClient", multiClient);
            JSONObject result = ResponseHelper.getSuccessObject();
            result.put("suspicious", data);
            return result;
        }
    }

    public JSONObject getFrequentCalls(Map<String, String[]> parameters) throws Exception {
        String startTime = RequestHelper.getString(parameters, "startTime");
        String endTime = RequestHelper.getString(parameters, "endTime");
        String campusId = RequestHelper.getString(parameters, "campusId");
        int start = RequestHelper.getInt(parameters, "start");
        int size = RequestHelper.getInt(parameters, "size");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_MULTI_ADA_WITH_PAGE, startTime, endTime, campusId, start, size));
            JSONObject count = ResultSetJSONWrapper.getJSONObjectFromSQL(conn, SQL_SELECT_TOTAL_COUNT);
            return ResponseHelper.getSuccessObject(result, count.getLong("rowCount"));
        }
    }

    public JSONObject getMultiAda(Map<String, String[]> parameters) throws Exception {
        String startTime = RequestHelper.getString(parameters, "startTime");
        String endTime = RequestHelper.getString(parameters, "endTime");
        String campusId = RequestHelper.getString(parameters, "campusId");
        int start = RequestHelper.getInt(parameters, "start");
        int size = RequestHelper.getInt(parameters, "size");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_MULTI_CLIENT_WITH_PAGE, startTime, endTime, campusId, start, size));
            JSONObject count = ResultSetJSONWrapper.getJSONObjectFromSQL(conn, SQL_SELECT_TOTAL_COUNT);
            return ResponseHelper.getSuccessObject(result, count.getLong("rowCount"));
        }
    }
}
