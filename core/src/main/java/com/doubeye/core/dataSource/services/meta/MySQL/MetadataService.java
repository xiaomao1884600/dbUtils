package com.doubeye.core.dataSource.services.meta.MySQL;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.jsonBuilder.JSONWrapper;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 根据dataId获得数据源下所有被授权的数据库
 */
@SuppressWarnings("unused")
public class MetadataService {
    /**
     * 根据数据源ID获得所有被授权的Schema
     *
     * @param parameters 参数对象
     * @return 数据源ID下所有被授权的Schema
     * @throws SQLException SQL异常
     */
    public JSONArray getAllSchemasByDataSourceId(Map<String, String[]> parameters) throws Exception {
        int datasourceId = RequestHelper.getInt(parameters, "datasourceId");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnectionByDatasourceId(datasourceId, encryptKey);
             ResultSet rs = conn.getMetaData().getCatalogs()) {
            JSONArray result = new JSONArray();
            while (rs.next()) {
                JSONObject database = new JSONObject();
                database.put("database", rs.getString(1));
                database.put(("name"), rs.getString(1));
                result.add(database);
            }
            return result;
        }
    }

    /**
     * 获得Schema下所有的数据表
     *
     * @param parameters 参数对象
     * @return Schema下所有的数据表
     * @throws SQLException SQL异常
     */
    public JSONArray getAllTablesInSchema(Map<String, String[]> parameters) throws Exception {
        int datasourceId = RequestHelper.getInt(parameters, "datasourceId");
        String schema = RequestHelper.getString(parameters, "database");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnectionByDatasourceId(datasourceId, encryptKey);
             ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_ALL_NONE_FEDERATED_TABLES, schema))) {
            return JSONWrapper.getJSON(rs);
        }
    }

    /**
     * 获得指定schema下指定表的大小
     * @param parameters 参数，包括如下值
     *                   tables 逗号分隔的表名
     *                   schema 数据表所在的schema名
     * @return 数据库大小信息
     * @throws Exception 异常
     */
    public JSONArray getTableSizeInfo(Map<String, String[]> parameters) throws Exception{
        String[] tables = RequestHelper.getString(parameters, "tables").split(",");
        String schema = RequestHelper.getString(parameters, "schema");
        String dataSource = RequestHelper.getString(parameters, "dataSource");
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection(dataSource, encryptKey)) {
            for (int i = 0; i < tables.length; i ++) {
                StringBuilder builder = new StringBuilder();
                tables[i] = builder.append("'").append(tables[i]).append("'").toString();
            }
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_TABLE_SIZE, schema, CollectionUtils.toString(tables, ",")));
        }
    }

    private static final String SQL_SELECT_ALL_NONE_FEDERATED_TABLES = "SELECT table_name `TABLE_NAME` FROM information_schema.`TABLES` WHERE UPPER(`ENGINE`) <> 'FEDERATED' AND TABLE_SCHEMA = '%s'";
    private static final String SQL_SELECT_TABLE_SIZE = "SELECT table_name `tableName`, table_comment `comment`, round((DATA_LENGTH + INDEX_LENGTH)/ 1048576, 2) size , TABLE_ROWS `rowCount` FROM information_schema.tables WHERE TABLE_SCHEMA='%s' AND TABLE_NAME IN (%s)";
}
