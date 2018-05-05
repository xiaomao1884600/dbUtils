package com.doubeye.core.opration.template;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;

/**
 * @author doubeye
 * @version 1.0.0
 * 抽象的操作接口实现，添加对数据库连接的设置
 */
public abstract class AbstractOperation implements Operation{
    protected JSONObject runResult = new JSONObject();
    protected Connection conn;
    protected JSONObject objectParameter;
    protected JSONArray arrayParameter;

    private OperationTemplate operationTemplate;
    @Override
    public void setConnection(Connection conn) {
        this.conn = conn;
    }
    @Override
    public Connection getConnection() {
        return conn;
    }

    @Override
    public JSONObject getResult() {
        return runResult;
    }

    @Override
    public void setParameters(JSONObject parameter) {
        this.objectParameter = parameter;
    }
    @Override
    public void setParameters(JSONArray parameters) {
        this.arrayParameter = parameters;
    }

    @Override
    public void setOperationTemplate(OperationTemplate operationTemplate) {
        this.operationTemplate = operationTemplate;
    }

    @Override
    public JSONObject getSharedResult() {
        if (operationTemplate != null) {
            return operationTemplate.getMergedResult();
        } else {
            return getResult();
        }
    }
}
