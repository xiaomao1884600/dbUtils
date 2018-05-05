package com.doubeye.core.authorization.helper.core;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.core.authorization.bean.UserBean;
import com.doubeye.core.opration.template.AbstractOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author core数据库中，添加网校ADA用户的助手方法
 */
public class CoreOnlineAdaUserHelper extends AbstractOperation {

    public static CoreOnlineAdaUserHelper getInstance(Connection conn, UserBean userBean) {
        CoreOnlineAdaUserHelper helper = new CoreOnlineAdaUserHelper();
        helper.setConnection(conn);
        JSONObject parameters = new JSONObject();
        parameters.put("user", userBean);
        helper.setParameters(parameters);
        return helper;
    }
    @Override
    public void run() throws SQLException, Exception {
        JSONObject userBean = objectParameter.getJSONObject("user");
        Connection conn = getConnection();
        JSONArray users = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, StringUtils.format(SQL_SELECT_CORE_USER, userBean));
        if (users.size() == 0) {
            SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_CORE_USER, userBean));
            SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_CORE_USER_ROLE, userBean));
            SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_CORE_USER_GROUP, userBean));
        }
    }

    public static final String SQL_SELECT_CORE_USER = "SELECT * FROM core_user WHERE user_id = '([{domainName}])@hxsd.local'";
    private static final String SQL_INSERT_CORE_USER = "insert into core_user(user_id, user_name) values ('([{domainName}])@hxsd.local', '([{userName}])');";
    private static final String SQL_INSERT_CORE_USER_ROLE = "insert into core_user_role(user_id, role_id) values ('([{domainName}])@hxsd.local', 7);";
    private static final String SQL_INSERT_CORE_USER_GROUP = "insert into core_user_group(user_id, group_id) values ('([{domainName}])@hxsd.local', 2);";
}
