package com.doubeye.experiments.recommandEngine;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SingleNumberPagedExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;

public class ItemGenerator {
    public void doExport() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(exportFileName));
        SingleNumberPagedExecutor pagedExecutor = new SingleNumberPagedExecutor() {
            /*
            @Override
            public void afterRetrieveData(ResultSet result) throws Exception {
                StringBuilder builder = new StringBuilder();
                long id = result.getLong("item_id");
                String category = result.getString("category");
                String keywords = result.getString("keywords");
                String description = result.getString("description");
                String properties = result.getString("properties");
                String bizinfo = result.getString("bizinfo");
                builder.append(id).append("\t").append(category).append("\t").append(keywords).append("\t")
                        .append(description).append("\t").append(properties).append("\t").append(bizinfo).append("\n");
                writer.write(builder.toString());
            }
            */
            @Override
            public void afterRetrieveData(JSONArray result) throws Exception {
                for (int i = 0; i < result.size(); i++) {
                    JSONObject entry = result.getJSONObject(i);
                    StringBuilder builder = new StringBuilder();
                    long id = entry.getLong("item_id");
                    String category = entry.getString("category");
                    String keywords = entry.getString("keywords");
                    String description = entry.getString("description");
                    String properties = entry.getString("properties");
                    String bizinfo = entry.getString("bizinfo");
                    builder.append(id).append("\t").append(category).append("\t").append(keywords).append("\t")
                            .append(description).append("\t").append(properties).append("\t").append(bizinfo).append("\n");
                    writer.write(builder.toString());
                }
            }
        };
        pagedExecutor.setConn(conn);
        pagedExecutor.setAutoIncrementColumnName("item_id");
        pagedExecutor.setRecordCountPerPage(1000);
        pagedExecutor.setSqlTemplate(SQL_SELECT_ALL_ARTICLES_TEMPLATE);
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

    private static final String SQL_SELECT_ALL_ARTICLES_TEMPLATE = "SELECT\n" +
            "\tid item_id,\n" +
            "\tIFNULL(faculties, '') category,\n" +
            "\t'' keywords,\n" +
            "\tintroduction description,\n" +
            "\t'' properties,\n" +
            "\t'' bizinfo\n" +
            "FROM\n" +
            "\tt_user_articles a\n" +
            "LEFT OUTER JOIN t_student_from_edu u ON a.passport_id = u.passportid\n" +
            "WHERE id > :start AND id <= :end";

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("REC-ENGINE-228", encrytKey);
        ItemGenerator generator = new ItemGenerator();
        generator.setDatabaseBunchCount(1000);
        generator.setConn(conn);
        generator.setExportFileName("d:/generatedItem.txt");
        generator.doExport();
    }
}
