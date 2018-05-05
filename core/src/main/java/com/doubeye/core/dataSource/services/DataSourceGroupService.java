package com.doubeye.core.dataSource.services;


import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 获得数据源组的服务
 */
@SuppressWarnings("unused")
public class DataSourceGroupService {

    /**
     * 获得所有的数据源组
     * @param parameters 参数
     * @return 所有的数据源组
     * @throws SQLException SQL异常
     */
    public JSONArray getAllDataSourceGroups(Map<String, String[]> parameters) throws SQLException {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_GET_ALL_DATA_SOURCE_GROUPS);
        }
    }
    /**
     * 获得所有EDU的数据源组
     * @param parameters 参数
     * @return 所有EDU的数据源组
     * @throws SQLException 异常
     */
    public JSONArray getAllEDUDataSourceGroups(Map<String, String[]> parameters) throws SQLException {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_GET_ALL_EDU_DATA_SOURCE_GROUPS);
        }
    }

    /**
     * 获得所有E的数据源组
     * @param parameters 参数
     * @return 所有E的数据源组
     * @throws SQLException SQL异常
     */
    public JSONArray getAllEDataSources(Map<String, String[]> parameters) throws SQLException {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_GET_ALL_E_DATA_SOURCE);
        }
    }

    /**
     * 获得所有数据源
     * @param parameters 参数对象
     * @return 所有数据源，格式为JSONArray
     * @throws SQLException SQL异常
     */
    public JSONArray getAllPublicSources(Map<String, String[]> parameters) throws SQLException {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()) {
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_GET_ALL_DATA_SOURCE);
        }
    }

    /**
     * 获得所有的数据源组
     */
    private static final String SQL_GET_ALL_DATA_SOURCE_GROUPS = "SELECT * from core_datasource_group";
    /**
     * 获得EDU数据源组
     */
    private static final String SQL_GET_ALL_EDU_DATA_SOURCE_GROUPS = "SELECT * from core_datasource_group WHERE UPPER(identifier) LIKE 'EDU%'";
    /**
     * 获得所有E数据源
     */
    private static final String SQL_GET_ALL_E_DATA_SOURCE = "SELECT * from core_datasource WHERE UPPER(identifier) LIKE 'E-%' AND is_public = 1";//强制加入了对公开的判断
    /**
     * 获得所有数据源
     */
    private static final String SQL_GET_ALL_DATA_SOURCE = "SELECT id, identifier, CONCAT(name, '-', substr(host, 1, 20), ':', port) name, datasourceType,username,password,host,port,defaultSchema,datasourceGroup from core_datasource WHERE is_public = 1";//强制加入了对公开的判断
}
