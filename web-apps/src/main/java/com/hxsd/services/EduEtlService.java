package com.hxsd.services;

import com.doubeye.commons.application.ApplicationCache;
import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.etl.EtlCase;
import com.doubeye.commons.etl.EtlCaseThreadRunner;
import com.doubeye.commons.etl.EtlCasesBunchThreadRunner;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/8/11.
 * EDU数据迁移服务
 */
public class EduEtlService {
    /**
     * 获得所有的方案
     * @param parameters 参数
     * @return 所有的迁移方案
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public JSONArray getAllEtlCases(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            if (dataSource.equals("E-PRODUCT")) {
                throw new RuntimeException("系统已经正式上线，请确认要进行此操作");
            }
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encryptKey);
            return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_GET_ALL_ETL_CASES);
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    /**
     * 运行指定方案
     * @param parameters 参数
     * @return 运行状态，包括重定向的地址
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public JSONArray runCase(Map<String, String[]> parameters) throws Exception {
        String dataSourceIdentifier = RequestHelper.getString(parameters, "dataSource");
        EtlCase newCase = new EtlCase();
        newCase.setDataSourceIdentifier(dataSourceIdentifier);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        newCase.getCase(RequestHelper.getInt(parameters, "id"));
        EtlCaseThreadRunner runner = new EtlCaseThreadRunner(newCase);
        String uuid = UUID.randomUUID().toString();
        ApplicationCache.getInstance().addTask(uuid, runner);
        new Thread(runner).start();
        JSONArray result = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("REDIRECT", uuid);
        result.add(obj);
        return result;
    }
    /**
     * 运行指定方案
     * @param parameters 参数，需要包含以下参数
     *                   dataSource 数据源
     *                   etlIds JSON数组，包含所有需要运行的etl的id
     * @return 运行状态，包括重定向的地址
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public JSONArray runCases(Map<String, String[]> parameters) throws Exception {
        String dataSourceIdentifier = RequestHelper.getString(parameters, "dataSource");
        String etlIds = RequestHelper.getString(parameters, "etlIds");
        List<EtlCase> etlCases = new ArrayList<>();
        String[] etlIdArray = etlIds.split(",");
        for (String etlId : etlIdArray) {
            EtlCase newCase = new EtlCase();
            newCase.setDataSourceIdentifier(dataSourceIdentifier);
            newCase.getCase(Integer.parseInt(etlId));
            etlCases.add(newCase);
        }
        EtlCasesBunchThreadRunner runner = new EtlCasesBunchThreadRunner(etlCases);
        String uuid = UUID.randomUUID().toString();
        ApplicationCache.getInstance().addTask(uuid, runner);
        new Thread(runner).start();
        JSONArray result = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("REDIRECT", uuid);
        result.add(obj);
        return result;
    }

    public JSONObject removeCase(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encryptKey);
            EtlCase etlCase = new EtlCase();
            etlCase.remove(RequestHelper.getInt(parameters, "id"));
            JSONObject obj = new JSONObject();
            obj.put("SUCCESS", true);
            return obj;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    public JSONObject addCase(Map<String, String[]> parameters) throws Exception {
        Connection conn = null;
        try {
            String dataSource = RequestHelper.getString(parameters, "dataSource");
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            conn = ConnectionManager.getConnection(dataSource, encryptKey);
            String caseName = RequestHelper.getString(parameters, "caseName");
            if (StringUtils.isEmpty(caseName)) {
                throw new RuntimeException("必须指定etl名称");
            }
            String originTableName = RequestHelper.getString(parameters, "originTableName");
            if (StringUtils.isEmpty(originTableName)) {
                throw new RuntimeException("必须指定源表名（远程表名）");
            }
            String targetTableName = RequestHelper.getString(parameters, "targetTableName");
            if (StringUtils.isEmpty(targetTableName)) {
                throw new RuntimeException("必须指定目标表名（实体表名）");
            }
            String primaryKey = RequestHelper.getString(parameters,"primaryKey");
            if (StringUtils.isEmpty(primaryKey)) {
                throw new RuntimeException("必须指定表的主键");
            }
            String condition = RequestHelper.getString(parameters, "condition");
            if (StringUtils.isEmpty(condition)) {
                condition = "";
            }
            EtlCase.addCase(dataSource, caseName, originTableName, targetTableName, primaryKey, condition, "");

            JSONObject obj = new JSONObject();
            obj.put("SUCCESS", true);
            return obj;
        } finally {
            ConnectionHelper.close(conn);
        }
    }

    private static final String SQL_GET_ALL_ETL_CASES = "SELECT id, name FROM etl_case WHERE hidden = 0";

    public static void main(String[] args) throws Exception {
        System.out.println(new EduEtlService().getAllEtlCases(null));
    }
}
