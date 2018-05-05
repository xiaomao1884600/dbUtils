package com.hxsd.productLine.studentInfo.e.toBespokeInfo;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/22.
 */
public class AssignLog {
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    Connection conn;
    public JSONArray getAssignLogByRange(String start, String end) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ASSIGN_LOG, start, end));
    }

    public JSONArray getAssignLogByStudentId(String studentId) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ASSIGN_LOG_BY_STUDENT_ID, studentId));
    }

    private static final String SQL_SELECT_ASSIGN_LOG = "SELECT params from t_assignlog WHERE dateline BETWEEN '%s' AND '%s'";
    private static final String SQL_SELECT_ASSIGN_LOG_BY_STUDENT_ID = "SELECT params FROM t_assignlog WHERE INSTR(params,'\"studentid\":%s,') > 0";
}
