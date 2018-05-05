package com.doubeye.experiments.recommandEngine;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SingleNumberPagedExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.collection.baidu.AliKeyValueConverter;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * 通过passport和edu用户信息生成阿里云使用的用户信息
 */
public class UserGenerator {
    /*
    public void doExport() throws SQLException, NoSuchFieldException, IllegalAccessException, IOException {
        passportId = -1;
        passportUpperLimit = passportId + databaseBunchCount;
        BufferedWriter writer = new BufferedWriter(new FileWriter(exportFileName));
        ResultSet users = null;
        try {
            while (!started || hasData) {
                users = getUsers();
                StringBuilder content = generateText(users);
                writeToFile(writer, content);
                passportUpperLimit = passportId + databaseBunchCount;
            }
        } finally {
            writer.flush();
            writer.close();
            ConnectionHelper.close(users);
        }
    }
    */
    private long lastPasspordId = 0l;

    public void doExport() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(exportFileName));
        SingleNumberPagedExecutor pagedExecutor = new SingleNumberPagedExecutor() {
            /*
            @Override
            public void afterRetrieveData(ResultSet result) throws Exception {
                StringBuilder builder = new StringBuilder();
                if (lastPasspordId == result.getLong("passportid")) {
                    return;
                }
                Long passportId = result.getLong("passportid");
                lastPasspordId = passportId;
                JSONObject userInfo = new JSONObject();
                userInfo.put("age", result.getInt("age"));
                userInfo.put("gender", result.getInt("gender"));
                userInfo.put("comeFromE", result.getInt("comeFromE"));
                String comeFromPassports = result.getString("comeFromPassport");
                userInfo.put("comeFromPassport", CollectionUtils.toJSONArray(comeFromPassports, ",").toString());
                String faculties = result.getString("faculties");
                userInfo.put("faculties", CollectionUtils.toJSONArray(faculties, ",").toString());

                userInfo.put("city", result.getString("cityid"));
                builder.append(passportId).append("\t").append(AliKeyValueConverter.toAliKeyValue(userInfo)).append("\n");
                writer.write(builder.toString());
            }
            */

            @Override
            public void afterRetrieveData(JSONArray result) throws Exception {
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < result.size(); i ++) {
                    JSONObject entry = result.getJSONObject(i);
                    if (lastPasspordId == entry.getLong("passportId")) {
                        return;
                    }

                    Long passportId = entry.getLong("passportid");
                    lastPasspordId = passportId;
                    JSONObject userInfo = new JSONObject();
                    userInfo.put("age", entry.getInt("age"));
                    userInfo.put("gender", entry.getInt("gender"));
                    userInfo.put("comeFromE", entry.getInt("comeFromE"));
                    String comeFromPassports = entry.getString("comeFromPassport");
                    userInfo.put("comeFromPassport", CollectionUtils.toJSONArray(comeFromPassports, ",").toString());
                    String faculties = entry.getString("faculties");
                    userInfo.put("faculties", CollectionUtils.toJSONArray(faculties, ",").toString());

                    userInfo.put("city", entry.getString("cityid"));
                    builder.append(passportId).append("\t").append(AliKeyValueConverter.toAliKeyValue(userInfo)).append("\n");
                }

                writer.write(builder.toString());
            }
        };
        pagedExecutor.setConn(conn);
        pagedExecutor.setAutoIncrementColumnName("passportid");
        pagedExecutor.setRecordCountPerPage(1000);
        pagedExecutor.setSqlTemplate(SQL_SELECT_ALL_USER_TEMPLATE);

        try {
            pagedExecutor.run();
        } finally {
            writer.flush();
            writer.close();
        }
    }
    /**
     * 导出文件名
     */
    private String exportFileName;
    /**
     * 每次从数据库中取得数据的条数，该条数不是精确值
     */
    private int databaseBunchCount = 1000;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public int getDatabaseBunchCount() {
        return databaseBunchCount;
    }

    public void setDatabaseBunchCount(int databaseBunchCount) {
        this.databaseBunchCount = databaseBunchCount;
    }

    private Connection conn;
/*
    private long passportId;
    private long passportUpperLimit;
    private boolean hasData = false;
    private boolean started = false;
*/
    /**
     * 获取数据的语句
     */
    /*
    private static final String SQL_SELECT_ALL_USER = "SELECT\n" +
            "\tp.passportid, comeFromPassport, IFNULL(gender, 0) gender, IFNULL(age, 0) age, IFNULL(comeFromE, 0) `comeFromE`, IFNULL(cityid, '0') cityid\n" +
            "FROM\n" +
            "\tt_user_from_passport p\n" +
            "LEFT OUTER JOIN t_student_from_edu e ON p.passportid = e.passportid\n" +
            "WHERE p.passportid > :passportId AND p.passportId <= :passportUpperLimit";
   */
    /**
     * 获取数据的语句
     */
    private static final String SQL_SELECT_ALL_USER_TEMPLATE = "SELECT\n" +
            "\tp.passportid, comeFromPassport, IFNULL(gender, 0) gender, IFNULL(age, 0) age, IFNULL(comeFromE, 0) `comeFromE`, IFNULL(cityid, '0') cityid, IFNULL(faculties, '') faculties\n" +
            "FROM\n" +
            "\tt_user_from_passport p\n" +
            "LEFT OUTER JOIN t_student_from_edu e ON p.passportid = e.passportid\n" +
            "WHERE p.passportid > :start AND p.passportId <= :end";

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("REC-ENGINE-228", encrytKey);
        UserGenerator generator = new UserGenerator();
        generator.setDatabaseBunchCount(1000);
        generator.setConn(conn);
        generator.setExportFileName("d:/generatedUser.txt");
        // generator.doExport();
        generator.doExport();
    }
}
