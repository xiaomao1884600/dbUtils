package com.hxsd.services.productLine.me;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/2/13.
 */
public class SignOutService {
    private static final String SQL_SELECT_SIGN_IN_COUNT = "select count(*) cnt from attendance where attendancedate = curdate() AND  signin > 0 ";
    private static final String SQL_SELECT_SIGN_OUT_COUNT = "select count(*) cnt from attendance where attendancedate = curdate() AND  signout > 0 ";

    public JSONArray getSignOutCount(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection("ME-PRODUCT", encrytKey);
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_SIGN_OUT_COUNT);
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONArray getSignInCount(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection("ME-PRODUCT", encrytKey);
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_SIGN_IN_COUNT);
        } finally {
            ConnectionHelper.close(conn);
        }
    }
}
