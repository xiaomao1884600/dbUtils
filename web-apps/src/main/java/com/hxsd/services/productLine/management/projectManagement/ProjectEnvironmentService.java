package com.hxsd.services.productLine.management.projectManagement;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.core.systemProperties.SystemProperties;
import com.doubeye.core.systemProperties.SystemPropertiesConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ProjectEnvironmentService {

    /**
     * 获取所有项目环境说明
     * @param parameters 参数，无
     * @return 所有的项目环境说明
     * @throws SQLException SQL异常
     */
    public JSONObject getAllProjectEnvironments(Map<String, String[]> parameters) throws SQLException {
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        try {
            JSONObject result = ResponseHelper.getSuccessObject();
            JSONArray data = ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, SQL_SELECT_ALL_PROJECT_ENVIRONMENTS);
            result.put("projectEnvironments", data);
            result.put("version", SystemProperties.getInt(coreConnection, VERSION_PROPERTY_NAME));
            return result;
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    /**
     * 保存项目环境说明
     * @return 执行结果，并将项目环境说明的编号返回
     */
    public JSONObject saveProjectEnvironment(Map<String, String[]> parameters) throws SQLException {
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        JSONObject projectEnvironmentObject = JSONObject.fromObject(RequestHelper.getString(parameters, "data"));
        ProjectEnvironment projectEnvironment = new ProjectEnvironment();
        RefactorUtils.fillByJSON(projectEnvironment, projectEnvironmentObject);
        try {
            coreConnection.setAutoCommit(false);
            if (projectEnvironment.getId() == 0) {
                try {
                    SQLExecutor.execute(coreConnection, SQL_INSERT_PROJECT_ENVIRONMENT, projectEnvironment);
                    projectEnvironment.setId(SQLExecutor.getLastInsertId(coreConnection));
                } catch (SQLException e) {
                    coreConnection.rollback();
                    throw e;
                }
            } else {
                SQLExecutor.execute(coreConnection, SQL_UPDATE_PROJECT_ENVIRONMENT, projectEnvironment);
            }
            int version = increaseVersion(coreConnection);
            coreConnection.commit();
            JSONObject successObject = ResponseHelper.getSuccessObject();
            successObject.put("id", projectEnvironment.getId());
            successObject.put("version", version);
            return successObject;
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    /**
     * 删除指定的项目环境说明
     * @param parameters 参数， 包括：
     *                   id :项目环境说明的id
     * @return 执行结果
     * @throws SQLException SQL异常
     */
    public JSONObject removeProjectEnvironment(Map<String, String[]> parameters) throws SQLException {
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        String id = RequestHelper.getString(parameters,"id");
        try {
            coreConnection.setAutoCommit(false);
            SQLExecutor.execute(coreConnection, String.format(SQL_DELETE_PROJECT_ENVIRONMENT, id));
            int version = increaseVersion(coreConnection);
            coreConnection.commit();
            JSONObject result = ResponseHelper.getSuccessObject();
            result.put("version", version);
            return result;
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    public JSONObject downloadHosts(Map<String, String[]> agrs) throws SQLException {
        JSONArray allProjectEnvironments;
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        int version = 0;
        try {
            allProjectEnvironments = ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, SQL_SELECT_ALL_PROJECT_ENVIRONMENTS);
            version = SystemProperties.getInt(coreConnection, VERSION_PROPERTY_NAME);
        } finally {
            ConnectionHelper.close(coreConnection);
        }
        List<String> hostsContent = new ArrayList<>();
        hostsContent.add(START);
        hostsContent.add(String.format(CONTENT_VERSION, version));
        for (int i = 0; i < allProjectEnvironments.size(); i ++ ) {
            ProjectEnvironment projectEnvironment = new ProjectEnvironment();
            RefactorUtils.fillByJSON(projectEnvironment, allProjectEnvironments.getJSONObject(i));
            if (!StringUtils.isEmpty(projectEnvironment.getIp())) {
                hostsContent.add("#" + projectEnvironment.getName());
                hostsContent.add(projectEnvironment.getIp() + " " + projectEnvironment.getDomainName());
            }
        }
        hostsContent.add(END);
        return ResponseHelper.getDownloadStringListContentObject(hostsContent, "hosts");
    }

    private static int increaseVersion(Connection conn) throws SQLException {
        SystemProperties.increasePropertyValue(conn, VERSION_PROPERTY_NAME);
        return SystemProperties.getInt(conn, VERSION_PROPERTY_NAME);
    }

    private static final String SQL_SELECT_ALL_PROJECT_ENVIRONMENTS = "SELECT\n" +
            "\tpe.id,\n" +
            "\tproject_id `projectId`,\n" +
            "\tp.name `projectName`,\n" +
            "\tenvironment_id `environmentId`,\n" +
            "\te.name `environmentName`,\n" +
            "\tpe.name,\n" +
            "\tdomain_name `domainName`,\n" +
            "\tip,\n" +
            "\tstable\n" +
            "FROM\n" +
            "\tproject_environment pe\n" +
            "INNER JOIN project p ON pe.project_id = p.id\n" +
            "INNER JOIN dict_environment e ON pe.environment_id = e.id";
    private static final String SQL_INSERT_PROJECT_ENVIRONMENT = "INSERT INTO project_environment(project_id, environment_id, name, domain_name, ip, stable) VALUES (:projectId, :environmentId, :name, :domainName, :ip, :stable )";
    private static final String SQL_UPDATE_PROJECT_ENVIRONMENT = "UPDATE project_environment SET project_id = :projectId, environment_id = :environmentId, name = :name, domain_name = :domainName, ip = :ip, stable = :stable WHERE id = :id";
    private static final String SQL_DELETE_PROJECT_ENVIRONMENT = "DELETE FROM project_environment WHERE id = %s";
    private static final String START = "#dbUtils (by doubeye for hxsd project and environment management) generated this file, add to your os hosts file. content starting ----";
    private static final String END = "#dbUtils generated hosts ended ----";
    private static final String CONTENT_VERSION = "#content version %s";
    private static final String VERSION_PROPERTY_NAME = SystemPropertiesConstant.SYSTEM_PROPERTY_ENV_VERSION;
}
