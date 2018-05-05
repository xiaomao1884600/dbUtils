package com.hxsd.services.productLine.e.enrollment.goal;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.util.Map;

public class EnrollmentGoalService {
    private static final String SQL_SELECT = "SELECT\n" +
            "\ta.manager_userid,\n" +
            "\ta.manager_username,\n" +
            "\ta.adaid,\n" +
            "\ta.ada_username,\n" +
            "\ta.cnt,\n" +
            "\tIFNULL(g.enrolled_goal, 0) goal\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\ttermid,\n" +
            "\t\t\tmanager_userid,\n" +
            "\t\t\tmanager_username,\n" +
            "\t\t\tadaid,\n" +
            "\t\t\tada_username,\n" +
            "\t\t\tcount(*) cnt\n" +
            "\t\tFROM\n" +
            "\t\t\tetl_enroll_confirm\n" +
            "\t\tWHERE\n" +
            "\t\t\ttermid = %d\n" +
            "\t\tAND type = 1\n" +
            "\t\tGROUP BY\n" +
            "\t\t\ttermid,\n" +
            "\t\t\tmanager_userid,\n" +
            "\t\t\tmanager_username,\n" +
            "\t\t\tadaid,\n" +
            "\t\t\tada_username\n" +
            "\t) a\n" +
            "LEFT OUTER JOIN t_edu_enrolled_goal g ON a.termid = g.termid\n" +
            "AND a.adaid = g.adaid\n" +
            "%s\n" +
            "ORDER BY a.manager_userid, a.adaid";



    public JSONArray getGoalAchievement(Map<String, String[]> parameters) throws Exception {
        int term = RequestHelper.getInt(parameters, "term", 167);
        int managerUserId = RequestHelper.getInt(parameters, "managerUserId", 0);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            String teamCondition = managerUserId == 0 ? "" : " WHERE manager_userid = " + managerUserId + " ";
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT, term, teamCondition));
        }
    }
}
