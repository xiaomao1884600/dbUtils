package com.doubeye.core.opration.template;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 操作接口
 */
public interface Operation {
    /**
     * 设置数据库连接
     * @param conn 数据库连接
     */
    void setConnection(Connection conn);

    /**
     * 获得数据库连接
     * @return 数据库连接
     */
    Connection getConnection();

    /**
     * 运行
     */
    void run() throws SQLException, Exception;

    /**
     * 设置运行的参数
     * @param parameters 参数
     */
    void setParameters(JSONObject parameters);

    /**
     * 设置运行的参数
     * @param parameters 参数
     */
    void setParameters(JSONArray parameters);

    /**
     * 获得本操作的执行结果
     * @return 本操作执行结果
     */
    JSONObject getResult();

    /**
     * 获得操作间共享的运行结果
     * @return 共享的运行结果
     */
    JSONObject getSharedResult();

    /**
     * 设置所属的操作模板
     * @param operationTemplate 操作模板实例
     */
    void setOperationTemplate(OperationTemplate operationTemplate);
}
