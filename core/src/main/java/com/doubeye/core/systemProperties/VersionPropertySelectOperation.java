package com.doubeye.core.systemProperties;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.core.opration.template.AbstractOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 版本查询操作
 */
public class VersionPropertySelectOperation extends AbstractOperation{

    private static final String SQL_SELECT_VERSION_BY_IDENTIFIER = "SELECT value FROM core_properties WHERE identifier = '%s'";

    @Override
    public void run() throws SQLException {
        JSONObject versionObject = ResultSetJSONWrapper.getJSONObjectFromSQL(getConnection(),
                String.format(SQL_SELECT_VERSION_BY_IDENTIFIER,
                        objectParameter.getString(VersionPropertyIncrementOperation.PROPERTY_NAME_IDENTIFIER)));
        getResult().put(PROPERTY_NAME_VERSION, versionObject.get(COLUMN_NAME_VALUE));
    }
    public static final String PROPERTY_NAME_VERSION = "version";
    private static final String COLUMN_NAME_VALUE = "value";

    public static VersionPropertySelectOperation getInstance(Connection conn, String versionIdentifier) {
        VersionPropertySelectOperation operation = new VersionPropertySelectOperation();
        operation.setConnection(conn);
        JSONObject parameters = new JSONObject();
        parameters.put(VersionPropertyIncrementOperation.PROPERTY_NAME_IDENTIFIER, versionIdentifier);
        operation.setParameters(parameters);
        return operation;
    }
}
