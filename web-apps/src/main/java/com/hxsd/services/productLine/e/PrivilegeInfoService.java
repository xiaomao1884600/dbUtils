package com.hxsd.services.productLine.e;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.productLine.studentInfo.e.toBespokeInfo.PrivilegeInfo;
import com.hxsd.productLine.studentInfo.e.user.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/30.
 */
public class PrivilegeInfoService {
    public JSONArray getPermissionsByUserInfo(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String userInfo = RequestHelper.getString(parameters, "userInfo");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            PrivilegeInfo privilegeInfo = new PrivilegeInfo();
            privilegeInfo.setConn(conn);
            JSONArray result = new JSONArray();
            JSONObject userPrivilegeInfo = new JSONObject();
            //用户姓名和接量权限
            JSONArray userNameAndAssignFlag = privilegeInfo.getUserNameAndAssignFlag(userInfo);
            if (userNameAndAssignFlag.size() > 0) {
                userPrivilegeInfo.put("userNameAndAssignFlag", userNameAndAssignFlag);
            }
            //获得用户所属的用户组
            JSONArray userGroups = privilegeInfo.getUserGroups(userInfo);
            userPrivilegeInfo.put("userGroups", userGroups);
            List<String> userIds = new ArrayList<>();
            //获得用户组包含的角色
            List<String> groupIds = new ArrayList<>();
            for (int i = 0; i < userGroups.size(); i ++) {
                JSONObject userGroup = userGroups.getJSONObject(i);
                groupIds.add(userGroup.getString("id"));
                if (!userIds.contains(userGroup.getString("userid"))) {
                    userIds.add(userGroup.getString("userid"));
                }
            }
            JSONArray groupRoles = privilegeInfo.getGroupRoles(groupIds);
            userPrivilegeInfo.put("groupRoles", groupRoles);
            //获得角色包含的权限
            List<String> roleIds = new ArrayList<>();
            for (int i = 0; i < groupRoles.size(); i ++) {
                JSONObject groupRole = groupRoles.getJSONObject(i);
                roleIds.add(groupRole.getString("roleid"));
            }
            JSONArray permissions = privilegeInfo.getRolePermissions(roleIds);
            userPrivilegeInfo.put("permissions", permissions);
            //校区权限
            JSONArray userCampuses = privilegeInfo.getUserCampuses(userIds);
            userPrivilegeInfo.put("userCampuses", userCampuses);

            result.add(userPrivilegeInfo);
            return result;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray getUsersUnderGroup(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            conn = ConnectionManager.getConnection(dataSource, encrytKey);
            PrivilegeInfo privilegeInfo = new PrivilegeInfo();
            privilegeInfo.setConn(conn);
            String groupInfo = RequestHelper.getString(parameters, "groupInfo");
            return privilegeInfo.getUsersUnderGroup(groupInfo);
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray resetUserPassword(Map<String, String[]> parameters) throws Exception {
        Connection userTableConnection = null;
        Connection coreConnection = null;
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            userTableConnection = ConnectionManager.getConnection(dataSource, encrytKey);
            coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
            User user = new User();
            int userId = RequestHelper.getInt(parameters, "userId");
            user.setUserTableConnection(userTableConnection);
            user.setCoreConnection(coreConnection);
            user.setUserId(userId);
            JSONObject userInfo = user.setDefaultPassword(dataSource);
            JSONArray result = new JSONArray();
            result.add(userInfo);
            return result;
        } finally {
            ConnectionHelper.close(userTableConnection);
            ConnectionHelper.close(coreConnection);
        }
    }

    public JSONArray getDefaultPasswordUser(Map<String, String[]> parameters) throws SQLException {
        Connection coreConnection = null;
        try {
            coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
            User user = new User();
            user.setCoreConnection(coreConnection);
            return user.getDefaultPasswordUsers();
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    public JSONObject restorePassword(Map<String, String[]> parameters) throws Exception {
        Connection userTableConnection = null;
        Connection coreConnection = null;
        String originDataSource = RequestHelper.getString(parameters, "originDataSource");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try {
            userTableConnection = ConnectionManager.getConnection(originDataSource, encrytKey);
            coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
            User user = new User();
            int userId = RequestHelper.getInt(parameters, "userId");
            user.setUserTableConnection(userTableConnection);
            user.setCoreConnection(coreConnection);
            user.setUserId(userId);
            user.restorePassword(originDataSource);
            JSONObject obj = new JSONObject();
            obj.put("SUCCESS", true);
            return obj;
        } finally {
            ConnectionHelper.close(userTableConnection);
            ConnectionHelper.close(coreConnection);
        }
    }
}
