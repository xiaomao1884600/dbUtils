package com.doubeye.general;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;

public class GeneralJsonGenerator {

    private String sql;
    private Connection conn;

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public JSONArray generate() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, sql);
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String key = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");

        GeneralJsonGenerator generalJsonGenerator = new GeneralJsonGenerator();
        try (Connection conn = ConnectionManager.getConnection("E-PRODUCT", key)) {
            generalJsonGenerator.setConn(conn);
            generalJsonGenerator.setSql("SELECT campusid, title `name` FROM t_campus WHERE deleted = 0 ORDER BY sort");
            JSONArray result = generalJsonGenerator.generate();
            System.out.println(result);
        }
    }
}
