package com.doubeye.datamining.recordanalyze;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import datamining.recordanalyze.bean.UserBean;
import datamining.recordanalyze.bean.UserLeaderBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 */
public class AuthorizationSaver {
    private static void saveUser(Connection conn) throws Exception {
        JSONArray allUser = JSONUtils.getJsonArrayFromFile(new File("d:/ectask.txt"), "gbk");
        saveUser(conn, allUser);
    }


    public static void saveUser(Connection conn, JSONArray allUser) throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_TRUNCATE_TABLE, "t_ectask"));
        for (int i = 0; i < allUser.size(); i++) {
            UserBean user = new UserBean();
            JSONObject userObject = allUser.getJSONObject(i);
            if (!userObject.containsKey("campusid") || "null".equalsIgnoreCase(userObject.getString("campusid"))) {
                userObject.put("campusid", -1);
            }
            if (!userObject.containsKey("extension") || "null".equalsIgnoreCase(userObject.getString("extension")) || org.apache.commons.lang.StringUtils.isEmpty(userObject.getString("extension"))) {
                userObject.put("extension", -1);
            }
            RefactorUtils.fillByJSON(user, userObject);
            String sql = StringUtils.format(SQL_INSERT_USER, JSONObject.fromObject(user));
            SQLExecutor.execute(conn, sql);
        }
    }


    private static void saveUserLeader(Connection conn) throws Exception {
        SQLExecutor.execute(conn, String.format(SQL_TRUNCATE_TABLE, "t_user_leader"));
        List<String> userLeaderList = CollectionUtils.loadFromFile("d:/user_leader.txt");
        JSONArray userLeader = new JSONArray();
        for (String entry : userLeaderList) {
            userLeader.add(JSONObject.fromObject(entry));
        }
        saveUserLeader(conn, userLeader);
    }

    public static void saveUserLeader(Connection conn, JSONArray userLeader) throws Exception {
        SQLExecutor.execute(conn, String.format(SQL_TRUNCATE_TABLE, "t_user_leader"));

        for (int i = 0; i < userLeader.size(); i ++) {
            UserLeaderBean bean = new UserLeaderBean();
            JSONObject userLeaderObject = userLeader.getJSONObject(i);
            if (org.apache.commons.lang.StringUtils.isEmpty(userLeaderObject.getString("group_extension"))) {
                userLeaderObject.put("group_extension", -1);
            }
            if (org.apache.commons.lang.StringUtils.isEmpty(userLeaderObject.getString("group_userorder"))) {
                userLeaderObject.put("group_userorder", -1);
            }
            try {
                RefactorUtils.fillByJSON(bean, userLeaderObject);
            } catch (Exception e) {
                break;
            }
            String sql = StringUtils.format(SQL_INSERT_USER_LEADER, JSONObject.fromObject(bean));
            SQLExecutor.execute(conn, sql);
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-DEV", "")) {
            saveUser(conn);
            saveUserLeader(conn);
        }
    }

    public static final String SQL_INSERT_USER = "INSERT INTO t_ectask(leaderid, userid, username, loginname, termid, campusid, extension, userorder) VALUES (([{leaderid}]), ([{userid}]), '([{username}])', '([{loginname}])', ([{termid}]), ([{campusid}]), ([{extension}]),  ([{userorder}]))";
    public static final String SQL_INSERT_USER_LEADER = "INSERT INTO t_user_leader(group_leaderid, group_loginname, group_username, group_userorder, group_termid, group_campusid, group_extension, u_userid, u_loginname, u_username ) VALUES (([{group_leaderid}]), '([{group_loginname}])', '([{group_username}])', ([{group_userorder}]), ([{group_termid}]), ([{group_campusid}]), ([{group_extension}]), ([{u_userid}]), '([{u_loginname}])', '([{u_username}])' )";

    public static final String SQL_TRUNCATE_TABLE = "TRUNCATE TABLE %s";

}
