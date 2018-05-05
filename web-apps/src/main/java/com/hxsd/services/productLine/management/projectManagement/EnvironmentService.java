package com.hxsd.services.productLine.management.projectManagement;


import com.doubeye.commons.application.GlobalApplicationContext;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.ConnectionHelper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@SuppressWarnings("unused")
public class EnvironmentService {
    public JSONArray getAllEnvironments(Map<String, String[]> parameters) throws SQLException {
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        try {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, SQL_SELECT_ALL_ENVIRONMENTS);
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    private static final String SQL_SELECT_ALL_ENVIRONMENTS = "SELECT id, name FROM dict_environment";
}
