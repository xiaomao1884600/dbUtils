package com.hxsd.productLine.studentInfo.e.toBespokeInfo;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.collection.CollectionUtils;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/30.
 */
public class PrivilegeInfo {
    private Connection conn;

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    public JSONArray getUserCampuses(List<String> userIds) throws SQLException {
        if (userIds.size() > 0) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_USER_CAMPUS, CollectionUtils.split(userIds, ",")));
        } else {
            return new JSONArray();
        }
    }
    public JSONArray getUserNameAndAssignFlag(String userInfo) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_USER_NAME_AND_ASSIGN_FLAG, userInfo, userInfo, userInfo));
    }
    public JSONArray getUserGroups(String userInfo) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_USER_GROUPS, userInfo, userInfo, userInfo));
    }

    public JSONArray getGroupRoles(List<String> groupsIds) throws SQLException {
        if (groupsIds.size() > 0) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_GROUP_ROLES, CollectionUtils.split(groupsIds, ",", "'")));
        } else {
            return new JSONArray();
        }
    }

    public JSONArray getRolePermissions(List<String> roleIds) throws SQLException {
        if (roleIds.size() > 0) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ROLE_PERMISSTION, CollectionUtils.split(roleIds, ",", "'")));
        } else {
            return new JSONArray();
        }
    }

    public JSONArray getUsersUnderGroup(String groupName) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_USERS_UNDER_GROUP, groupName));
    }

    private static final String SQL_SELECT_USER_NAME_AND_ASSIGN_FLAG = "SELECT userid, username, loginname, IF(autoassign = 1, '可以接量', '无权接量') autoassign FROM t_user WHERE CONVERT(userid, CHAR) = '%s' OR username = '%s' OR loginname = '%s'";
    private static final String SQL_SELECT_USER_GROUPS =
            "SELECT CONCAT(username, '(', CONVERT(userid, CHAR), '_', loginname, ')') user, g.name, IFNULL(g.id, 0) id, u.userid FROM t_user u \n" +
            "LEFT OUTER JOIN t_users_group ug ON u.userid = ug.user_id \n" +
            "LEFT OUTER JOIN t_group g ON ug.groupid = g.id\n" +
            "WHERE CONVERT(u.userid, CHAR) = '%s' OR u.username = '%s' OR u.loginname = '%s'";
    private static final String SQL_SELECT_GROUP_ROLES = "SELECT DISTINCT r.`name`, r.roleid FROM t_role_group rg \n" +
            "LEFT OUTER JOIN t_role r ON rg.role_id = r.roleid\n" +
            "WHERE rg.groupid IN (%s) AND r.roleid IS NOT NULL;";

    private static final String SQL_SELECT_ROLE_PERMISSTION = "SELECT p.name, params FROM t_role_permission rp \n" +
            "LEFT OUTER JOIN t_permission p ON rp.permissionid = p.permissionid\n" +
            "WHERE roleid IN (%s)";
    private static final String SQL_USER_CAMPUS = "SELECT\n" +
            "\ttitle\n" +
            "FROM\n" +
            "\tt_user\n" +
            "INNER JOIN t_usercampus ON t_user.userid = t_usercampus.userid\n" +
            "INNER JOIN t_campus ON t_usercampus.campusid = t_campus.campusid\n" +
            "WHERE\n" +
            "\tt_user.userid = %s";

    private static final String SQL_SELECT_USERS_UNDER_GROUP = "SELECT \n" +
            "\tusername\n" +
            "FROM\n" +
            "\tt_users_group ug,\n" +
            "\tt_group g,\n" +
            "\tt_user u\n" +
            "WHERE\n" +
            "\tg.id = ug.groupid\n" +
            "AND ug.user_id = u.userid\n" +
            "AND g.NAME = '%s'";
}
