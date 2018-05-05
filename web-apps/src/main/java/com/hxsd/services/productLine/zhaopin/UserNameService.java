package com.hxsd.services.productLine.zhaopin;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
@SuppressWarnings("unused")
public class UserNameService {

    private static final String SQL_SELECT_USER_BY_NAME = "SELECT id, username, CONCAT(departmentFirst, '-', departmentSecond, '-', `group`, '-', position) post, phone FROM info_essential WHERE username = '%s'";
    public JSONArray getUserByName(Map<String, String[]> parameters) throws Exception {
        String userName = RequestHelper.getString(parameters, "userName");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection enrollConnection = ConnectionManager.getConnection("ZHAOPIN-PRODUCT", encrytKey)){
            return ResultSetJSONWrapper.getJSONArrayFromSQL(enrollConnection, String.format(SQL_SELECT_USER_BY_NAME, userName));
        }
    }

    private static final String SQL_UPDATE_USERNAME_BY_USERNAME = "UPDATE info_essential SET username = '%s' WHERE id = %d";
    public JSONObject changeUserName(Map<String, String[]> parameters) throws Exception {
        String userName = RequestHelper.getString(parameters, "userName");
        int id = RequestHelper.getInt(parameters, "id");
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection enrollConnection = ConnectionManager.getConnection("ZHAOPIN-PRODUCT", encrytKey)){
            SQLExecutor.execute(enrollConnection, String.format(SQL_UPDATE_USERNAME_BY_USERNAME, userName, id));
            return ResponseHelper.getSuccessObject();
        }
    }
}
