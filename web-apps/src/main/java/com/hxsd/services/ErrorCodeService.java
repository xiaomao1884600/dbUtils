package com.hxsd.services;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.ConnectionHelper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by zhanglu1782 on 2016/7/14.
 */
public class ErrorCodeService {
    public JSONArray getAllErrorCodes(Map<String, String[]> parameters) throws SQLException, ClassNotFoundException {
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        try {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, SQL_GET_ALL_ERROR_CODE);
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ErrorCodeService errorCodeService = new ErrorCodeService();
        System.out.println(errorCodeService.getAllErrorCodes(null));
    }

    private static final String SQL_GET_ALL_ERROR_CODE = "SELECT error_code 'errorCode', error_message 'errorMessage' FROM dict_error_message WHERE ID > 0";
}
