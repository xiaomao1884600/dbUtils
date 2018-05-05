package com.hxsd.tempNeeds;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/9/6.
 */
public class CreateTableToJson {
    private static final String SQL_GET_FEDERATED_TABLE = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'newedu' AND engine = 'FEDERATED'";
    private static final String SQL_SHOW_CREATE_TABLE = "SHOW CREATE TABLE %s";

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        try (Connection conn = ConnectionManager.getConnectionByDatasourceId(0, "")) {
            ResultSet rs = SQLExecutor.executeQuery(conn, SQL_GET_FEDERATED_TABLE);
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                ResultSet rsCreateTable = SQLExecutor.executeQuery(conn, String.format(SQL_SHOW_CREATE_TABLE, tableName));
                if (rsCreateTable.next()) {
                    String showCreateTable = rsCreateTable.getString(2) + ";";
                    System.out.println(showCreateTable);
                }
            }
        }
    }
}
