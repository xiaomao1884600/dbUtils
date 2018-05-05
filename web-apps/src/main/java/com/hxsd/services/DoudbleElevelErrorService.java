package com.hxsd.services;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/11/26.
 */
public class DoudbleElevelErrorService {

    private static final  String SQL = "SELECT enrollment.enrollmentid, enrollment.studentid, student.studentname, student.mobile, student.ecuserid, `user`.username\n" +
            "#, enrollment.receivabletuition, clazz.tuition AS '班级定价',  (clazz.tuition - enrollment.receivabletuition) as cha, enrollment.status  \n" +
            "from student\n" +
            "INNER JOIN enrollment on enrollment.studentid = student.studentid\n" +
            "inner JOIN feelog on feelog.studentid = student.studentid\n" +
            "inner JOIN clazz on enrollment.clazzid = clazz.clazzid\n" +
            "INNER JOIN user ON user.userid = student.ecuserid\n" +
            "where \n" +
            "enrollment.doubleeleven = 1\n" +
            "and enrollment.`status` in (0,1,6)\n" +
            "and enrollment.termid in (152, 153)\n" +
            "and feelog.feepaytypeid in (11,12,16,17)\n" +
            "AND clazz.tuition - enrollment.receivabletuition > 1\n" +
            "GROUP BY enrollment.enrollmentid";

    public JSONArray getAllErrorInfo(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection("EDU_PRODUCT_SLAVE", "EDU-MAIN", encrytKey);
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL);
        } finally {
            ConnectionHelper.close(conn);
        }
    }
}
