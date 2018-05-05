package com.doubeye.temperary;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;

public class ExportRecordAnalyzeResult {
    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", "")) {
            JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT);
            for (int i = 0; i < result.size(); i ++) {
                JSONObject record = result.getJSONObject(i);
                StringBuilder builder = new StringBuilder();
                builder.append(record.getString("record_id")).append("\r\n");
                builder.append(record.getString("analyze_info")).append("\r\n");
                FileUtils.toFile("d:/analyze_info.txt", builder, true);
            }
        }
    }
    private static final String SQL_SELECT = "SELECT\n" +
            "\trecord_id, analyze_info\n" +
            "FROM\n" +
            "\trecord_analyze\n" +
            "WHERE\n" +
            "\trecord_id IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\t*\n" +
            "\t\tFROM\n" +
            "\t\t\t(\n" +
            "\t\t\t\tSELECT\n" +
            "\t\t\t\t\trecord_id\n" +
            "\t\t\t\tFROM\n" +
            "\t\t\t\t\trecord_info\n" +
            "\t\t\t\tWHERE\n" +
            "\t\t\t\t\tbillable > 120\n" +
            "\t\t\t\tAND billable < 300\n" +
            "\t\t\t\tAND datetime > DATE_SUB(NOW(), INTERVAL 1 DAY)\n" +
            "\t\t\t\tLIMIT 50\n" +
            "\t\t\t) a\n" +
            "\t)";

}
