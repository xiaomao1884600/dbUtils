package com.hxsd.monitor.productLine.me;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/9.
 */
@DisallowConcurrentExecution
public class MeDatabaseConnectionMonitor implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
        try {
            ConnectionLog connectionLog = new ConnectionLog();
            int connectionStatus = 1;
            int queryStatus = 1;
            String connectionException = "";
            String queryException = "";
            Connection conn = null;
            try {
                long connectionStart = System.currentTimeMillis() / 1000;
                try {
                    String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
                    conn = ConnectionManager.getConnection("ME-TEST", encrytKey);
                } catch (Exception e) {
                    connectionException = e.getMessage();
                    connectionStatus = 0;
                }
                long connectionReturn = System.currentTimeMillis() / 1000;
                try {
                    ResultSet rs = SQLExecutor.executeQuery(conn, SQL_TEST_QUERY);
                } catch (SQLException e) {
                    queryStatus = 0;
                    queryException = e.getMessage();
                }
                long queryReturn = System.currentTimeMillis() / 1000;
                long connectionCost = connectionReturn - connectionStart;
                long queryConst = queryReturn - connectionReturn;

                connectionLog.setConnectionStart(connectionStart);
                connectionLog.setConnectionReturn(connectionReturn);
                connectionLog.setConnectionCost(connectionCost);
                connectionLog.setConnectionStatus(connectionStatus);
                connectionLog.setQueryStart(connectionReturn);
                connectionLog.setQueryReturn(queryReturn);
                connectionLog.setQueryCost(queryConst);
                connectionLog.setQueryStatus(queryStatus);

                connectionLog.setConnectionException(connectionException);
                connectionLog.setQueryException(queryException);
            } finally {
                ConnectionHelper.close(conn);
            }
            try {
                connectionLog.save(coreConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            ConnectionHelper.close(coreConnection);
        }
    }

    private static final String SQL_TEST_QUERY = "SELECT 1 FROM DUAL";

    private class ConnectionLog {
        private long connectionStart;
        private long connectionReturn;
        private long connectionCost;
        private int connectionStatus;
        private long queryStart;
        private long queryReturn;
        private long queryCost;
        private int queryStatus;
        private String connectionException;
        private String queryException;

        public long getConnectionStart() {
            return connectionStart;
        }

        public void setConnectionStart(long connectionStart) {
            this.connectionStart = connectionStart;
        }

        public long getConnectionReturn() {
            return connectionReturn;
        }

        public void setConnectionReturn(long connectionReturn) {
            this.connectionReturn = connectionReturn;
        }

        public long getConnectionCost() {
            return connectionCost;
        }

        public void setConnectionCost(long connectionCost) {
            this.connectionCost = connectionCost;
        }

        public long getQueryStart() {
            return queryStart;
        }

        public void setQueryStart(long queryStart) {
            this.queryStart = queryStart;
        }

        public long getQueryReturn() {
            return queryReturn;
        }

        public void setQueryReturn(long queryReturn) {
            this.queryReturn = queryReturn;
        }

        public long getQueryCost() {
            return queryCost;
        }

        public void setQueryCost(long queryCost) {
            this.queryCost = queryCost;
        }

        public int getConnectionStatus() {
            return connectionStatus;
        }

        public void setConnectionStatus(int connectionStatus) {
            this.connectionStatus = connectionStatus;
        }

        public int getQueryStatus() {
            return queryStatus;
        }

        public void setQueryStatus(int queryStatus) {
            this.queryStatus = queryStatus;
        }

        public String getConnectionException() {
            return connectionException;
        }

        public void setConnectionException(String connectionException) {
            this.connectionException = connectionException;
        }

        public String getQueryException() {
            return queryException;
        }

        public void setQueryException(String queryException) {
            this.queryException = queryException;
        }

        public void save(Connection conn) throws SQLException {
            SQLExecutor.execute(conn, String.format(SQL_INSERT_ME_CONNECTION_LOG, connectionStart, connectionReturn, connectionCost, queryStart, queryReturn, queryCost, connectionStatus, queryStatus, connectionException, queryException));
        }

        private static final String SQL_INSERT_ME_CONNECTION_LOG = "INSERT INTO me_connection_log VALUES (%d, %d, %d, %d, %d, %d, %d, %d, '%s', '%s')";
    }
}
