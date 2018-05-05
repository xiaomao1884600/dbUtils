package com.doubeye.core.systemProperties;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.core.opration.template.AbstractOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 版本属性操作
 * 在objectParameters中需提供以下参数
 *  identifier 版本的标示符
 */
public class VersionPropertyIncrementOperation extends AbstractOperation{

    /**
     * 属性名常量
     */
    public static final String PROPERTY_NAME_IDENTIFIER = "identifier";

    private static final String SQL_UPDATE_BY_VERSION_NAME = "UPDATE core_properties SET value = value + 1 WHERE identifier = '([{identifier}])'";

    @Override
    public void run() throws SQLException {
        SQLExecutor.execute(conn, StringUtils.format(SQL_UPDATE_BY_VERSION_NAME, objectParameter));
    }

    public static VersionPropertyIncrementOperation getInstance(Connection conn, String versionIdentifier) {
        VersionPropertyIncrementOperation operation = new VersionPropertyIncrementOperation();
        operation.setConnection(conn);
        JSONObject parameters = new JSONObject();
        parameters.put(PROPERTY_NAME_IDENTIFIER, versionIdentifier);
        operation.setParameters(parameters);
        return operation;
    }
}
