package com.doubeye.commons.application;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.connection.bean.DataSource;
import com.doubeye.commons.utils.encrypt.Encrypt3DES;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.1.0
 * 获得连接的助手类
 * history
 *  1.1.0 :
 *      + 所有的方法添加用户名密码解密的参数
 */
public class ConnectionManager {
    /**
     * EDU生成环境主库从库
     */
    public static final String DATASOURCE_EDU_MAIN = "EDU-PRODUCT-MAIN";
    /**
     * EDU生成环境日志库从库
     */
    public static final String DATASOURCE_LOG = "EDU-LOG";

    /**
     * 根据指定的数据源组标识，数据源标识获得数据库连接
     * @param dataSourceGroupIdentifier 数据源组标识
     * @param dataSourceIdentifier 数据源标识
     * @param key 数据库用户名密码加密key
     * @return 数据库连接对象
     * @throws SQLException SQL语句异常
     */
    public static Connection getConnection(String dataSourceGroupIdentifier, String dataSourceIdentifier, String key) throws Exception {
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            JSONObject configObject = ResultSetJSONWrapper.getJSONObjectFromSQL(coreConnection, String.format(SQL_GET_DATASOURCE_BY_GROUP_AND_DATASOURCE, dataSourceIdentifier, dataSourceGroupIdentifier));
            return getConnection(configObject, key);
        }
    }

    /**
     * 根据数据源标识返回数据连接对象
     * @param dataSourceIdentifier 数据源标识
     * @return 数据库连接对象
     * @param key 数据库用户名密码加密key
     * @throws SQLException SQL异常
     */
    public static Connection getConnection(String dataSourceIdentifier, String key) throws Exception {
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            JSONObject configObject = ResultSetJSONWrapper.getJSONObjectFromSQL(coreConnection, String.format(SQL_GET_DATASOURCE_BY_DATASOURCE, dataSourceIdentifier));
            return getConnection(configObject, key);
        }
    }



    /**
     * 根据数据源id返回数据连接信息
     * @param datasourceId 数据源id
     * @return 数据库连接信息
     * @param key 数据库用户名密码加密key
     * @throws SQLException SQL异常
     */
    public static DataSource getDataSourceById(int datasourceId, String key) throws Exception {
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            JSONObject configObject = ResultSetJSONWrapper.getJSONObjectFromSQL(coreConnection, String.format(SQL_GET_DATASOURCE_BY_ID, datasourceId));
            return getDataSource(configObject, key);
        }
    }


    /**
     * 根据数据源标识返回数据连接对象
     * @param datasourceId 数据源id
     * @return 数据库连接对象
     * @param key 数据库用户名密码加密key
     * @throws SQLException SQL异常
     */
    public static Connection getConnectionByDatasourceId(int datasourceId, String key) throws Exception {
        DataSource dataSource = getDataSourceById(datasourceId, key);
        ConnectionHelper targetConnectionHelper = (ConnectionHelper) RefactorUtils.getClassInstanceByName(dataSource.getHelperClassName());
        return targetConnectionHelper.getConnection(dataSource);
    }

    /**
     * 根据数据库连接配置对象返回数据库连接对象
     * @param configObject 数据库配置对象，格式为JSONObject
     * @param key 数据库用户名密码加密key
     * @return 数据库连接对象
     */
    private static Connection getConnection(JSONObject configObject, String key) throws Exception {
        DataSource dataSource = getDataSource(configObject, key);
        String connectionHelperClassName = configObject.getString("helperClassName");
        ConnectionHelper targetConnectionHelper = (ConnectionHelper) RefactorUtils.getClassInstanceByName(connectionHelperClassName);
        return targetConnectionHelper.getConnection(dataSource);
    }

    /**
     * 将数据库连接的JSONObject对象返回数据库连接信息
     * @param configObject 数据库配置对象，格式为JSONObject
     * @return 数据库连接信息
     * * @param key 数据库用户名密码加密key
     * @see DataSource
     */
    private static DataSource getDataSource(JSONObject configObject, String key) throws Exception {
        DataSource dataSource = new DataSource();
        RefactorUtils.fillByJSON(dataSource, configObject);
        //如果该配置被加密了，将用户名和密码解密
        if (!StringUtils.isEmpty(key)) {
            System.out.println(key);
            dataSource.setUserName(Encrypt3DES.decode(dataSource.getUserName(), key));
            dataSource.setPassword(Encrypt3DES.decode(dataSource.getPassword(), key));
        }
        return dataSource;
    }

    /**
     * 根据数据源组，数据源标识获得数据库连接信息的SQL语句
     */
    private static final String SQL_GET_DATASOURCE_BY_GROUP_AND_DATASOURCE = "SELECT username `userName`, password, host, port, defaultSchema `defaultSchema`, helperClassName `helperClassName` FROM core_datasource d, core_datasource_group g, core_datasource_type t\n" +
            "WHERE d.datasourceGroup = g.id AND d.datasourceType = t.id\n" +
            "AND d.identifier = '%s' AND g.identifier = '%s' AND d.is_public = 1";//强制加入了对公开的判断
    /**
     * 根据数据源标识，获得数据库连接信息
     */
    private static final String SQL_GET_DATASOURCE_BY_DATASOURCE = "SELECT\n" +
            "\tusername `userName`,\n" +
            "\tpassword,\n" +
            "\thost,\n" +
            "\tport,\n" +
            "\tdefaultSchema `defaultSchema`,\n" +
            "\thelperClassName `helperClassName`\n" +
            "FROM\n" +
            "\tcore_datasource d,\n" +
            "\tcore_datasource_type t\n" +
            "WHERE\n" +
            "d.datasourceType = t.id\n" +
            "AND d.identifier = '%s' " +
            "AND d.is_public = 1"; //强制加入了对公开的判断
    /**
     * 根据数据源id获得数据源对象
     */
    private static final String SQL_GET_DATASOURCE_BY_ID = "SELECT\n" +
            "\tusername `userName`,\n" +
            "\tpassword,\n" +
            "\thost,\n" +
            "\tport,\n" +
            "\tdefaultSchema `defaultSchema`,\n" +
            "\thelperClassName `helperClassName`\n" +
            "FROM\n" +
            "\tcore_datasource d,\n" +
            "\tcore_datasource_type t\n" +
            "WHERE\n" +
            "d.datasourceType = t.id\n" +
            "AND d.id = '%s' " +
            "AND d.is_public = 1";//强制加入了对公开的判断
}
