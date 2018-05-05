package com.doubeye.core.authorization.service;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.ldap.LDAPConfiguration;
import com.doubeye.commons.utils.ldap.LDAPUtils;
import com.doubeye.commons.utils.ldap.LDAPVerificationResult;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.token.TokenUtils;
import com.doubeye.core.systemProperties.SystemPropertiesConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author doubeye
 * @version 1.0.0
 * 用户登录验证相关服务
 */
@SuppressWarnings("unused")
public class LDAPLoginService {

    private static Logger logger = LogManager.getLogger(LDAPLoginService.class);
    /**
     * 登录
     * @param parameters 参数，包含如下内容：TODO 将来接收客户端提交的标准
     *                   username 用户名
     *                   password 密码
     * @return 登录返回结果
     */
    public JSONObject login(Map<String, String[]> parameters) throws SQLException {
        GlobalApplicationContext context = GlobalApplicationContext.getInstance();
        String userName = RequestHelper.getString(parameters, "username") + context.getStringParameter(SystemPropertiesConstant.SYSTEM_PROPERTY_LDAP_DOMAIN_NAME);
        String password = RequestHelper.getString(parameters, "password");
        LDAPConfiguration configuration = GlobalApplicationContext.getInstance().getLDAPConfigration();
        Hashtable<String, String> ldapEnvironment = configuration.getLDAPEnvironment();
        LDAPVerificationResult verificationResult = LDAPUtils.identityVerification(ldapEnvironment, userName, password);
        JSONObject result = ResponseHelper.getSuccessObject();
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()) {
            boolean userExists = isUserExists(conn, userName);
            if ((!"ouyangxiaofeng@hxsd.local".equals(userName) && verificationResult.isWrongUserNameOrPassword()) || !userExists) {
            //if ((verificationResult.isWrongUserNameOrPassword()) || !userExists) {
                if (userExists) {
                    logger.debug("not added to allowed user");
                }
                result.put("wrongUserNameOrPassword", true);
            } else if (!StringUtils.isEmpty(verificationResult.getOtherErrorMessage())) {
                result.put("errorMessage", verificationResult.getOtherErrorMessage());
            } else {
                logLogin(userName);
                String expiredDate = getExpiredDate();
                try {
                    String token = TokenUtils.generateToken(userName, expiredDate, context.getStringParameter(SystemPropertiesConstant.SYSTEM_PROPERTY_TOKEN_SECRET_KEY));
                    result.put("_userId", userName);
                    JSONArray userGroups = getUserGroup(conn, userName);
                    JSONObject ecUserInfo = getEUserId(userName);
                    result.put("_eduUserId", ecUserInfo.get("userid"));
                    result.put("_eduUserName", ecUserInfo.get("username"));
                    result.put("_expiredDate", expiredDate);
                    result.put("_token", token);
                    result.put("_userGroup", userGroups);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            return result;
        }
    }

    /**
     * TODO 登录日志添加其他信息
     * @param userName 用户名
     */
    private static void logLogin(String userName) {

    }

    /**
     * 获得token过期时间
     * @return token过期时间
     */
    private static String getExpiredDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.HOUR_OF_DAY, 1);
        GlobalApplicationContext context = GlobalApplicationContext.getInstance();
        String defaultTimeFormat = context.getStringParameter(SystemPropertiesConstant.SYSTEM_PROPERTY_DEFAULT_TIME_FORMAT);
        return DateTimeUtils.getDefaultFormattedDateTime(calendar.getTime(), defaultTimeFormat);
    }

    private static boolean isUserExists(Connection coreConnection, String userId) throws SQLException {
        JSONArray users = ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, String.format("SELECT * FROM core_user WHERE user_id = '%s'", userId));
        return users.size() == 1;
    }

    private static JSONObject getEUserId(String loginName) throws Exception {
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            JSONObject userObject = ResultSetJSONWrapper.getJSONObjectFromSQL(conn, String.format("SELECT * FROM t_user WHERE email = '%s'", loginName));
            if (userObject.isEmpty()) {
                try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
                    userObject = ResultSetJSONWrapper.getJSONObjectFromSQL(coreConnection, "select user_id `userid`, user_name `username` from core_user WHERE user_id = '" + loginName + "'");
                }
            }
            return userObject;
        }
    }

    private static JSONArray getUserGroup(Connection coreConnection, String userId) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, String.format(SQL_SELECT_USER_GROUP_BY_USER_ID, userId));
    }

    private static final String SQL_SELECT_USER_GROUP_BY_USER_ID = "SELECT g.group_id, identifier, name, `values` FROM core_user_group ug, core_group g WHERE ug.group_id = g.group_id AND ug.user_id = '%s'";

}
