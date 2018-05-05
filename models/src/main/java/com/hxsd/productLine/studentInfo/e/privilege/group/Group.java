package com.hxsd.productLine.studentInfo.e.privilege.group;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/20.
 */
public class Group {

    public JSONArray getAllGroups() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL_GROUPS);
    }

    public JSONArray getAllUserByGroup(String permissionKey) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ALL_USER_BY_PERMISSION, permissionKey));
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    private Connection conn;

    private static final String SQL_SELECT_ALL_GROUPS = "SELECT id, name FROM t_group";
    private static final String SQL_SELECT_ALL_USER_BY_PERMISSION = "SELECT\n" +
            "\tUSER.userid,\n" +
            "\tUSER.loginname,\n" +
            "\tUSER.username\n" +
            "FROM\n" +
            "\tt_permission AS per\n" +
            "INNER JOIN t_role_permission AS role ON per.permissionid = role.permissionid\n" +
            "INNER JOIN t_role_group AS rgroup ON rgroup.role_id = role.roleid\n" +
            "INNER JOIN t_users_group AS usergroup ON usergroup.groupid = rgroup.groupid\n" +
            "INNER JOIN t_user AS USER ON USER.userid = usergroup.user_id\n" +
            "WHERE\n" +
            "\t1 = 1\n" +
            "AND per.params = '%s'\n" +
            "AND USER.deleted = 0\n" +
            "GROUP BY\n" +
            "\tUSER.userid";
}
