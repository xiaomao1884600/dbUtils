package com.doubeye.core.opration.template;


import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 操作模板
 */
@SuppressWarnings("unused")
public class OperationTemplate {
    /**
     * 操作器列表
     */
    private List<Operation> operations = new ArrayList<>();
    /**
     * 数据库连接对象
     */
    private Set<Connection> connections = new HashSet<>();
    /**
     * 合并的运行结果
     */
    private JSONObject mergedResult = new JSONObject();
    /**
     * 调试用的运行结果
     */
    private JSONObject debugRunResult = new JSONObject();
    /**
     * 是否启动事务，true，即启动事务。特殊的情况下，不启动事务，或者由调用者启动事务，将此属性设置为false
     */
    private boolean needTransaction = true;

    /**
     * 添加一个操作
     * @param operation 操作对象
     */
    public void addOperation(Operation operation) {

        operations.add(operation);
        operation.setOperationTemplate(this);

        if (operation.getConnection() != null) {
            connections.add(operation.getConnection());
        }
    }

    /**
     * 运行所有操作
     * @throws SQLException SQL异常
     */
    public void run() throws Exception {
        if (needTransaction) {
            prepareConnections();
        }
        try {
            for (Operation operation : operations) {
                operation.run();
                JSONObject operationResult = operation.getResult();
                mergedResult.putAll(operationResult);
                debugRunResult.put(operation.getClass().getName() + "-" + operation.hashCode(), operationResult);
            }
            if (needTransaction) {
                commit();
            }
        } catch (Exception e) {
            if (needTransaction) {
                rollback();
            }
            throw e;
        }
    }

    /**
     * 提交事务
     * @throws SQLException SQL异常
     */
    private void commit() throws SQLException {
        for (Connection conn : connections) {
            conn.commit();
        }
    }

    /**
     * 回滚事务
     * @throws SQLException SQL异常
     */
    private void rollback() throws SQLException {
        for (Connection conn : connections) {
            conn.rollback();
        }
    }

    /**
     * 将所有连接设置为非自动提交事务
     * @throws SQLException SQL异常
     */
    private void prepareConnections() throws SQLException {
        for (Connection conn : connections) {
            conn.setAutoCommit(false);
        }
    }

    public JSONObject getMergedResult() {
        return mergedResult;
    }

    public JSONObject getDebugRunResult() {
        return debugRunResult;
    }

    public boolean isNeedTransaction() {
        return needTransaction;
    }

    public void setNeedTransaction(boolean needTransaction) {
        this.needTransaction = needTransaction;
    }
}
