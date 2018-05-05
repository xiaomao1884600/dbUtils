package com.doubeye.commons.database.MySQL.administrator.importExportCase;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.bean.DataSource;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/19.
 * MySQL导入导出实体类
 */
public class ImportExportCaseEntity {
    /**
     * 数据库连接
     */
    private Connection conn;
    /**
     * 导入导出方案编号
     */
    private int id;
    /**
     * 方案名称
     */
    private String caseName;
    /**
     * 源数据源编号
     */
    private int originDatasourceId;
    /**
     * 源数据库
     */
    private String originDatabase;
    /**
     * 导入导出涉及到的源数据库表
     */
    private String originTableNames;
    /**
     * 目标数据源编号
     */
    private int targetDatasourceId;
    /**
     * 目标数据源中的目标数据库
     */
    private String targetDatabase;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @SuppressWarnings("unused")
    public String getCaseName() {
        return caseName;
    }
    @SuppressWarnings("unused")
    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    @SuppressWarnings("WeakerAccess")
    public String getOriginTableNames() {
        return originTableNames;
    }
    @SuppressWarnings("unused")
    public void setOriginTableNames(String originTableNames) {
        this.originTableNames = originTableNames;
    }
    @SuppressWarnings("WeakerAccess")
    public int getOriginDatasourceId() {
        return originDatasourceId;
    }
    @SuppressWarnings("unused")
    public void setOriginDatasourceId(int originDatasourceId) {
        this.originDatasourceId = originDatasourceId;
    }
    @SuppressWarnings("unused")
    public void setOriginDatabase(String originDatabase) {
        this.originDatabase = originDatabase;
    }
    @SuppressWarnings("WeakerAccess")
    public int getTargetDatasourceId() {
        return targetDatasourceId;
    }
    @SuppressWarnings("unused")
    public void setTargetDatasourceId(int targetDatasourceId) {
        this.targetDatasourceId = targetDatasourceId;
    }
    @SuppressWarnings("unused")
    public void setTargetDatabase(String targetDatabase) {
        this.targetDatabase = targetDatabase;
    }
    @SuppressWarnings("WeakerAccess")
    public String getOriginDatabase() {
        return originDatabase;
    }
    @SuppressWarnings("WeakerAccess")
    public String getTargetDatabase() {
        return targetDatabase;
    }

    
    /**
     * 获得所有导入导出方案
     * @return 所有的导入导出方案
     * @throws SQLException SQL异常
     */
    public JSONArray getAll() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL);
    }

    /**
     * 新建方案，并返回自动生成的主键id
     * @return 新增方案的主键id
     * @throws SQLException SQL异常
     */
    public int save() throws Exception {
        conn.setAutoCommit(false);
        try {
            SQLExecutor.execute(conn, String.format(SQL_INSERT, caseName, originDatasourceId, getEffectiveOriginSchema(), originTableNames, targetDatasourceId, getEffectiveTargetSchema()));
            int lastId = SQLExecutor.getLastInsertId(conn);
            conn.commit();
            return lastId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    /**
     * 更新方案
     * @throws SQLException SQL异常
     */
    public void update() throws SQLException {
        SQLExecutor.execute(conn, SQL_UPDATE, this);
    }

    /**
     * 删除方案
     * @throws SQLException SQL异常
     */
    public void delete() throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_DELETE, id));
    }


    /**
     * 根据id载入方案
     * @throws SQLException SQL异常
     */
    @SuppressWarnings("WeakerAccess")
    public void initById() throws SQLException {
        JSONObject entityObject = ResultSetJSONWrapper.getJSONObjectFromSQL(conn, String.format(SQL_SELECT_BY_ID, getId()));
        RefactorUtils.fillByJSON(this, entityObject);
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * 插入新的方案
     */
    private static final String SQL_INSERT = "INSERT INTO import_export_case(etl_case_name, origin_datasource, origin_schema, origin_tables, target_datasource, target_schema) VALUES (" +
            "'%s', %s, '%s', '%s', %s, '%s')";
    /**
     * 更新方案
     */
    private static final String SQL_UPDATE = "UPDATE import_export_case set etl_case_name = :caseName, origin_datasource = :originDatasourceId, " +
            "origin_schema = :originDatabase, origin_tables = :originTableNames, " +
            "target_datasource = :targetDatasourceId, target_schema = :targetDatabase WHERE id = :id";
    /**
     * 获取所有方案
     */
    private static final String SQL_SELECT_ALL = "SELECT\n" +
            "\tec.id,\n" +
            "\tetl_case_name `caseName`,\n" +
            "\torigin_datasource `originDatasourceId`,\n" +
            "\tds1.`name` `originDatasourceLabel`,\n" +
            "\torigin_schema `originDatabase`,\n" +
            "\torigin_tables `originTableNames`,\n" +
            "\ttarget_datasource `targetDatasourceId`,\n" +
            "\tds2.`name` `targetDatasourceLabel`,\n" +
            "\ttarget_schema `targetDatabase`\n" +
            "FROM\n" +
            "\timport_export_case ec\n" +
            "INNER JOIN core_datasource ds1 ON ec.origin_datasource = ds1.id\n" +
            "INNER JOIN core_datasource ds2 ON ec.target_datasource = ds2.id";
    /**
     * 根据id获得单个方案
     */
    private static final String SQL_SELECT_BY_ID = "SELECT\n" +
            "\tec.id,\n" +
            "\tetl_case_name `caseName`,\n" +
            "\torigin_datasource `originDatasourceId`,\n" +
            "\tds1.`name` `originDatasourceLabel`,\n" +
            "\torigin_schema `originDatabase`,\n" +
            "\torigin_tables `originTableNames`,\n" +
            "\ttarget_datasource `targetDatasourceId`,\n" +
            "\tds2.`name` `targetDatasourceLabel`,\n" +
            "\ttarget_schema `targetDatabase`\n" +
            "FROM\n" +
            "\timport_export_case ec\n" +
            "INNER JOIN core_datasource ds1 ON ec.origin_datasource = ds1.id\n" +
            "INNER JOIN core_datasource ds2 ON ec.target_datasource = ds2.id WHERE ec.id = %s";
    /**
     * 删除方案
     */
    private static final String SQL_DELETE = "DELETE FROM import_export_case WHERE id = %s";

    /**
     * 获得有效的数据源数据库schema，如果没有指定数据源的schema，则使用数据源配置的默认schema
     * @return 有效的数据源数据库schema
     * @throws SQLException SQL异常
     */
    private String getEffectiveOriginSchema() throws Exception {
        return getEffectiveSchema(originDatasourceId, originDatabase);
    }
    /**
     * 获得有效的目标数据库schema，如果没有指定schema，则使用数据源配置的默认schema
     * @return 有效的目标数据库schema
     * @throws SQLException SQL异常
     */
    private String getEffectiveTargetSchema() throws Exception {
        return getEffectiveSchema(targetDatasourceId, targetDatabase);
    }

    /**
     * 为getEffectiveOriginSchema与getEffectiveTargetSchema提供的统一方法，如果显示提供schemaName，则使用此schemaName，
     * 否则使用数据源id配置的默认schemaName
     * @param dataSourceId 数据源id
     * @param schemaName 指定的schema名称
     * @return 返回有效的schemaName
     * @throws SQLException SQL异常
     */
    private static  String getEffectiveSchema(int dataSourceId, String schemaName) throws Exception {
        if (StringUtils.isEmpty(schemaName)) {
            String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            DataSource dataSource = ConnectionManager.getDataSourceById(dataSourceId, encrytKey);
            return dataSource.getDefaultSchema();
        } else {
            return schemaName;
        }
    }
}
