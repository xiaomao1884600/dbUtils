package com.doubeye.commons.etl;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.1.0
 * ETL方案
 */
public class EtlCase {
    private long costTime = 0;
    @SuppressWarnings("unused WeakerAccess")
    public long getCostTime() {
        return costTime;
    }

    private String dataSourceIdentifier = null;

    public void setDataSourceIdentifier(String dataSourceIdentifier) {
        this.dataSourceIdentifier = dataSourceIdentifier;
    }
    @SuppressWarnings("unused")
    public String getOriginTableName() {
        return originTableName;
    }
    @SuppressWarnings("WeakerAccess")
    public void setOriginTableName(String originTableName) {
        this.originTableName = originTableName;
    }
    @SuppressWarnings("unused")
    public String getTargetTableName() {
        return targetTableName;
    }
    @SuppressWarnings("WeakerAccess")
    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
    @SuppressWarnings("unused")
    public String getOriginKey() {
        return originKey;
    }
    @SuppressWarnings("WeakerAccess")
    public void setOriginKey(String originKey) {
        this.originKey = originKey;
    }
    @SuppressWarnings("WeakerAccess")
    public String getCaseName() {
        return caseName;
    }
    @SuppressWarnings("WeakerAccess")
    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }
    @SuppressWarnings("unused")
    public String getOriginCondition() {
        return originCondition;
    }
    @SuppressWarnings("WeakerAccess")
    public void setOriginCondition(String originCondition) {
        this.originCondition = originCondition;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public float getProgress() {
        return progress;
    }

    /**
     * 源数据表名
     */
    private String originTableName;
    /**
     * 目标数据表名
     */
    private String targetTableName;
    /**
     * 每次执行的记录数
     */
    private int step;
    /**
     * 元数据表的主键
     */
    private String originKey;
    /**
     * 方案名称
     */
    private String caseName;
    /**
     * 过滤元数据表数据的条件
     */
    private String originCondition;
    /**
     * 数据源
     */
    private String datasource;
    /**
     * 运行进度
     */
    private float progress = 0f;
    /**
     * 获得数据表的字段名
     */
    private static final String SQL_SELECT_TABLE_COLUMN = "SELECT column_name FROM information_schema.COLUMNS where TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s';";
    /**
     * 添加etl方案
     */
    private static final String SQL_INSERT_CASE = "INSERT INTO etl_case(name, origin_table_name, target_table_name, origin_table_columns, target_table_columns, step, `key`, origin_condition, datasource) VALUES ('%s', '%s', '%s', '%s', '%s', %d, '%s', '%s', '%s')";
    /**
     * 目标表和源表的对应关系
     */
    private Map<String, List<String>> relations;
    /**
     * 是否处于调试状态
     */
    private static final boolean DEBUG = false;
    /**
     * 根据表的信息，获得源和目标的对应关系
     * <Red 注意：目前假设两个表的字段名一直，如果不一致，则需要去数据库中手动修改/>
     */
    private void generateEtlRelation() throws Exception {
        relations = new HashMap<>();
        List<String> origin = new ArrayList<>();
        List<String> target = new ArrayList<>();
        relations.put("origin", origin);
        relations.put("target", target);
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection(dataSourceIdentifier, encrytKey)){
            ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_TABLE_COLUMN, conn.getCatalog(), targetTableName));
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                origin.add(columnName);
                target.add(columnName);
            }
        }
    }
    @SuppressWarnings("WeakerAccess")
    public void saveCase() throws Exception {
        String originTableColumns = JSONArray.fromObject(relations.get("origin")).toString();
        String targetTableColumns = JSONArray.fromObject(relations.get("target")).toString();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection(dataSourceIdentifier, encrytKey)){
            SQLExecutor.execute(conn, String.format(SQL_INSERT_CASE, caseName, originTableName, targetTableName, originTableColumns, targetTableColumns, step, originKey, originCondition, datasource));
        }
    }

    /**
     * 根据id得到方案
     */
    private static final String SQL_GET_CASE_BY_ID = "SELECT * FROM etl_case WHERE id = %d";

    /**
     * 根据id获得方案
     * @param caseId 方案编号
     * @throws SQLException SQL异常
     */
    public void getCase(int caseId) throws Exception {
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection(dataSourceIdentifier, encrytKey);
             ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_GET_CASE_BY_ID, caseId))){
            getCase(rs);
        }
    }

    /**
     * 根据方案名称获得方案
     */
    private static final String SQL_GET_CASE_BY_NAME = "SELECT * FROM etl_case WHERE name = '%s'";

    /**
     * 根据方案名称获得方案
     * @param caseName 方案名称
     * @throws SQLException SQL异常
     */
    @SuppressWarnings("WeakerAccess")
    public void getCase(String caseName) throws Exception {
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection(dataSourceIdentifier, encrytKey);
             ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_GET_CASE_BY_NAME, caseName))){
            getCase(rs);
        }
    }

    /**
     * 根据数据库返回的数据源获得数据源
     * @param rs 数据库查询结果集
     * @throws SQLException SQL异常
     */
    private void getCase(ResultSet rs) throws SQLException {
        while (rs.next()) {
            caseName = rs.getString("name");
            originTableName = rs.getString("origin_table_name");
            targetTableName = rs.getString("target_table_name");
            String originColumns = rs.getString("origin_table_columns");
            String targetColumns = rs.getString("target_table_columns");
            step = rs.getInt("step");
            originKey = rs.getString("key");
            originCondition = rs.getString("origin_condition");
            datasource = rs.getString("datasource");
            relations = new HashMap<>();
            /*
            relations.put("origin", fromString(originColumns));
            relations.put("target", fromString(targetColumns));
            */
            relations.put("origin", CollectionUtils.split(originColumns.substring(1, originColumns.length() - 2).replace("\"",""), ","));
            relations.put("origin", CollectionUtils.split(targetColumns.substring(1, targetColumns.length() - 2).replace("\"",""), ","));
        }
    }

    /**
     * 根据删除方案
     */
    private static final String SQL_DELETE_CASE = "DELETE FROM etl_case WHERE id = %d";

    /**
     * 根据id删除方案
     * @param id 方案编号
     * @throws SQLException SQL异常
     */
    public void remove(int id) throws Exception {
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection(dataSourceIdentifier, encrytKey)){
            SQLExecutor.execute(conn, String.format(SQL_DELETE_CASE, id));
        }
    }

    /**
     * 获得表格的记录数
     */
    private static final String SQL_GET_COUNT = "SELECT COUNT(*) cnt FROM %s";

    /**
     * 运行方案
     * @param startPage 开始页数
     * @throws Exception 异常
     */
    private void runCase(int startPage) throws Exception {
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        String sql = getInsertSQL();
        try (Connection conn = ConnectionManager.getConnection(dataSourceIdentifier, encrytKey);
             ResultSet rs = SQLExecutor.executeQuery(conn, String.format(SQL_GET_COUNT, originTableName))){

            rs.next();
            long rowCount = rs.getLong("cnt");


            //删除目标表所有数据
            if (!DEBUG) {
                SQLExecutor.execute(conn, "TRUNCATE TABLE " + targetTableName);
            }

            System.out.println("总共需要迁移的记录数为 " + rowCount);
            long startTime = System.currentTimeMillis();
            double times = Math.ceil(rowCount * 1.0 / step);
            for (long i = startPage; i <= times; i ++) {
                String limit = getSQLLimit(i, step);
                String pagedSQL = sql + limit;
                long t1 = System.currentTimeMillis();
                if (!DEBUG) {
                    SQLExecutor.execute(conn, pagedSQL);
                }
                Thread.sleep(200);
                long t2 = System.currentTimeMillis();
                progress = (float)(i * 100.0f / times);
                System.out.println(String.format("已经升级了%d页数据，占%.2f%%，花费%d秒，共花费%d秒", i, progress, (t2 - t1) / 1000, (t2 - startTime) / 1000));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("升级所有数据花费" + ((endTime - startTime) / 1000) + "秒");
            costTime = (endTime - startTime) / 1000;
        }
    }

    /**
     * 运行方案
     * @throws Exception 异常
     */
    @SuppressWarnings("WeakerAccess")
    public void runCase() throws Exception {
        runCase(1);
    }

    /**
     * TODO MySQL写法
     * 获得LIMIT语句
     * @param startPage 开始页数
     * @param step 每页页数
     * @return SQL的LIMIT子句
     */
    private String getSQLLimit(long startPage, long step) {
        return " LIMIT " + ((startPage - 1) * step) + ", " + step;
    }

    /**
     * 生成INSERT语句
     * @return insert语句
     * @throws Exception 异常
     */
    private String getInsertSQL() throws Exception {
        List<String> origin = relations.get("origin");
        List<String> target = relations.get("target");
        if ((origin.size() == 0) || (origin.size() != target.size())) {
            int minSize = Math.min(origin.size(), target.size());
            for (int i = 0; i < minSize; i ++) {
                System.err.println(origin.get(i) + "=>" + target.get(i));
            }
            throw new Exception("源数据表和目的数据表的字段个数不匹配，请检查");
        }
        StringBuilder stringBuilder = new StringBuilder().append("INSERT INTO ").append(targetTableName).append(" (");
        for (String e : target) {
            stringBuilder.append(e).append(',');
        }
        stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1)).append(") SELECT ");
        for (String e : origin) {
            stringBuilder.append(e.replace(";;", ",")).append(",");
        }
        stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1)).append(" FROM ").append(originTableName);
        if (originCondition != null && originCondition.length() > 0) {
            stringBuilder.append(" WHERE ").append(originCondition);
        }
        stringBuilder.append(" ORDER BY ").append(originKey);
        return stringBuilder.toString();
    }

    /**
     * 获得方案描述
     * @return 方案描述
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("升级方案名称(caseName):").append(caseName).append("\n");
        sb.append("原始数据表名(originTableName):").append(originTableName).append("\n");
        sb.append("目标数据表名（targetTableName）:").append(targetTableName).append("\n");
        sb.append("每次抽取记录数(step):").append(step).append("\n");
        sb.append("原始数据过滤条件（origin_condition）:").append(originCondition).append("\n");
        sb.append("原始数据源名称(datasource)").append(datasource).append("\n");
        sb.append("字段对应关系:\n");
        int columnCount = relations.get("origin").size();
        List<String> origin = relations.get("origin");
        List<String> target = relations.get("target");
        for (int i = 0; i < columnCount; i ++) {
            sb.append("\t").append(origin.get(i).replace(";;", ",")).append(" => ").append(target.get(i)).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {

        EtlCase newCase = new EtlCase();
        newCase.getCase("学生表到学生主表");
        System.out.println(newCase.toString());
        newCase.runCase(0);

        String caseName = "无效学生表111";
        // addCase(caseName, "f_student_from_old", "t_student_invalid", "studentid", "invalid = 1", "hxsdedusystem");

        run(caseName);

    }

    /**
     * 添加方案
     * @param dataSourceIdentifier 数据源描述
     * @param caseName 方案名称
     * @param originTableName 源数据表名
     * @param targetTableName 目标数据表名
     * @param originKey 源数据表的主键
     * @param condition 过滤条件
     * @param datasource 数据源
     * @throws SQLException SQL异常
     */
    public static void addCase(String dataSourceIdentifier, String caseName, String originTableName, String targetTableName, String originKey, String condition, String datasource) throws Exception {
        EtlCase newCase = new EtlCase();
        newCase.setDataSourceIdentifier(dataSourceIdentifier);
        newCase.setCaseName(caseName);
        newCase.setOriginTableName(originTableName);
        newCase.setTargetTableName(targetTableName);
        newCase.setStep(100000);
        newCase.setOriginKey(originKey);
        newCase.generateEtlRelation();
        newCase.setOriginCondition(condition);
        newCase.setDatasource(datasource);
        newCase.saveCase();
    }

    /**
     * 根据方案名称运行方案
     * @param caseName 方案名称
     * @throws Exception 异常
     */
    @SuppressWarnings("unused")
    private static void run(String caseName) throws Exception {
        EtlCase newCase = new EtlCase();
        newCase.getCase(caseName);
        System.out.println(newCase.toString());
        newCase.runCase();
    }

}