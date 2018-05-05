package com.doubeye.core.authorization.helper.online;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.core.authorization.bean.UserBean;
import com.doubeye.core.opration.template.AbstractOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * record后台中，在线用户助手
 */
public class RecordOnlineHelper extends AbstractOperation {

    public static RecordOnlineHelper getInstance(Connection conn, UserBean userBean) {
        RecordOnlineHelper helper = new RecordOnlineHelper();
        helper.setConnection(conn);
        JSONObject parameters = new JSONObject();
        parameters.put("user", userBean);
        helper.setParameters(parameters);
        return helper;
    }

    @Override
    public void run() throws SQLException, Exception {
        JSONObject userBean = objectParameter.getJSONObject("user");
        JSONArray users = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, StringUtils.format(SQL_SELECT_RECORD_ONLINE_USER, userBean));
        if (users.size() == 0) {
            SQLExecutor.execute(getConnection(), StringUtils.format(SQL_INSERT_RECORD_ONLINE_USER, userBean));
            SQLExecutor.execute(getConnection(), StringUtils.format(SQL_INSERT_RECORD_TEAM_USER, userBean));
        }
    }

    /**
     * record数据库网校用户组表，
     * userid 用户id， 在网校用户id前加wx_
     * username 用户名
     * loginname 域账号
     * mobile 手机号
     * extension 分机号
     * role 角色
     * group 销售组名称
     * wx_user_id 网校用户id
     * team_num 销售组id
     */
    private static final String SQL_INSERT_RECORD_ONLINE_USER = "INSERT INTO `wx_user`(userid, username, loginname, mobile, extension, role, `group`, wx_user_id, team_num)\n" +
            "VALUES ('wx_([{userId}])', '([{userName}])', '([{domainName}])@hxsd.local', '([{mobile}])', '([{phone}])', 'ADA', '([{teamName}])', '([{userId}])', '([{teamId}])');";

    /**
     * record 数据库用户分组信息
     * userid 用户id， 在网校用户id前加wx_
     * username 用户名
     * loginname 域账号
     * team_num 销售组id
     */
    private static final String SQL_INSERT_RECORD_TEAM_USER = "INSERT INTO `t_team_user`(userid, username, loginname, team_num)\n" +
            "VALUES ('wx_([{userId}])', '([{userName}])', '([{domainName}])@hxsd.local', ([{teamId}]))";

    private static final String SQL_SELECT_RECORD_ONLINE_USER = "SELECT * FROM wx_user WHERE userid='wx_([{userId}])'";
    public static final String SQL_SELECT_RECORD_ONLINE_USER_BY_DOMAIN_NAME = "SELECT * FROM wx_user WHERE loginname='([{domainName}])@hxsd.local'";
}
