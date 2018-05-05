package com.hxsd.productLine.studentInfo.e.user;

import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/15.
 */
public class User {

    public JSONObject setDefaultPassword(String dataSource) throws SQLException {
        ResultSet rs = null;
        try {
            rs = SQLExecutor.executeQuery(coreConnection, String.format(SQL_SELECT_RESET_INFO_FROM_LOG, dataSource, userId));
            if (rs.next()) {
                throw new RuntimeException("此用户已经重置过密码，无需再次重置");
            }
        } finally {
            ConnectionHelper.close(rs);
        }
        //取得用户现有的密码，salt和域标记
        JSONArray originInfoes = ResultSetJSONWrapper.getJSONArrayFromSQL(userTableConnection, String.format(SQL_SELECT_ORIGIN_PASSWORD_INFO, userId));
        JSONObject originInfo = null;
        if (originInfoes.size() > 0) {
            originInfo = originInfoes.getJSONObject(0);
        } else {
            throw new RuntimeException("没有找到用户，id为" + userId);
        }
        String originPassword = originInfo.getString("password");
        String originSalt = originInfo.getString("salt");
        String domainUser = originInfo.getString("domainuser");
        String userName = originInfo.getString("username");
        try {
            //重置密码
            SQLExecutor.execute(userTableConnection, String.format(SQL_RESET_PASSWORD, DEFAULT_PASSWORD, DEFAULT_SALT, DEFAULT_SALT, USE_PASSOWRD, userId));
            //保存原有密码
            SQLExecutor.execute(coreConnection, String.format(SQL_INSERT_RESET_PASSWORD_LOG, dataSource, userId, userName, domainUser, originPassword, originSalt, DEFAULT_PASSWORD, DEFAULT_SALT));
            return originInfo;
        } catch (Exception e) {
            //如果发生异常，一定要将密码恢复回去
            SQLExecutor.execute(userTableConnection, String.format(SQL_RESET_TO_ORIGIN_PASSWORD, originPassword, originSalt.replace("'", "\'"), domainUser, userId));
            throw new RuntimeException(e);
        }
    }

    public JSONArray getDefaultPasswordUsers() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, SQL_SELECT_ALL_DEFAULT_PASSPORT_USERS);
    }

    public void restorePassword(String originDataSource) throws SQLException {
        //取得用户现有的密码，salt和域标记
        JSONArray originInfoes = ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, String.format(SQL_SELECT_RESET_INFO_FROM_LOG, originDataSource, userId));
        JSONObject originInfo = null;
        if (originInfoes.size() > 0) {
            originInfo = originInfoes.getJSONObject(0);
        } else {
            throw new RuntimeException("没有找到用户，id为" + userId);
        }
        String originPassword = originInfo.getString("old_password");
        String originSalt = originInfo.getString("old_salt");
        String domainUser = originInfo.getString("old_domain_flag");
        SQLExecutor.execute(userTableConnection, String.format(SQL_RESET_TO_ORIGIN_PASSWORD, originPassword, originSalt, domainUser, userId));
        SQLExecutor.execute(coreConnection, String.format(SQL_DELETE_RESET_LOG, originDataSource, userId));
    }

    public void setUserTableConnection(Connection userTableConnection) {
        this.userTableConnection = userTableConnection;
    }

    public void setCoreConnection(Connection coreConnection) {
        this.coreConnection = coreConnection;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private Connection userTableConnection = null;
    private Connection coreConnection = null;
    private int userId;

    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_SALT = "OPf";
    private static final int USE_PASSOWRD = 0;
    /**
     * 获得原始的用户密码信息 userId
     */
    private static final String SQL_SELECT_ORIGIN_PASSWORD_INFO = "SELECT CONCAT(username, '--', loginname) username, `password`, salt, domainuser, userName FROM t_user WHERE userid = %d";
    /**
     * 获得所有的被重置密码的用户
     */
    private static final String SQL_SELECT_ALL_DEFAULT_PASSPORT_USERS = "SELECT datasource, userid, username, old_domain_flag, old_password, old_salt FROM function_reset_password_log";
    /**
     * 设置用户的密码， password, salt, salt, domain, userId
     */
    private static final String SQL_RESET_PASSWORD = "UPDATE t_user SET `password` =  MD5(concat(MD5('%s'), '%s')), salt = '%s', domainuser = %s WHERE userid = %d";
    /**
     * 回复原始密码
     */
    private static final String SQL_RESET_TO_ORIGIN_PASSWORD = "UPDATE t_user SET `password` =  '%s', salt = '%s', domainuser = %s WHERE userid = %d";
    /**
     * userId, domainUser, originPassword, originSalt, password, salt
     */
    private static final String SQL_INSERT_RESET_PASSWORD_LOG = "INSERT INTO function_reset_password_log(datasource, userid, username, old_domain_flag, old_password, old_salt, `password`, salt) VALUES ('%s', %d, '%s', %s, '%s', '%s', '%s', '%s')";
    /**
     * 获得修改之前的密码信息 userid
     */
    private static final String SQL_SELECT_PASSWORD_INFO_BEFORE_CHANGE = "SELECT userid, username, old_domain_flag, old_password, old_salt FROM function_reset_password_log WHERE userid = %d";
    /**
     * 查询用户是否被重置过密码
     */
    private static final String SQL_SELECT_RESET_INFO_FROM_LOG = "SELECT * FROM function_reset_password_log WHERE datasource = '%s' AND userid = %s";
    /**
     * 删除重置密码日志
     */
    private static final String SQL_DELETE_RESET_LOG = "DELETE FROM function_reset_password_log WHERE datasource = '%s' AND userid = %s";
}
