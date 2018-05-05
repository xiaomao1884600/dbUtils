package com.doubeye.experiments.dataAnalyze.eEnrollment.generators;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.experiments.dataAnalyze.eEnrollment.StudentInfo;
import com.doubeye.experiments.dataGeneration.DataGenerator;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FirstEnrollmentGenerator extends DataGenerator{
    @Override
    public void generate() throws SQLException {
        StudentInfo studentInfo = getStudentInfo();
        try (ResultSet resultSet = SQLExecutor.executeQuery(connection, String.format(SQL_GET_STUDENT_FIRST_ENROLLMENT, studentInfo.getStudentId()))) {
            if (resultSet.next()) {
                studentInfo.setFirstEnrollmentDate(resultSet.getString("firstEnrollmentTime"));
            }
        }
    }

    private static final String SQL_GET_STUDENT_FIRST_ENROLLMENT = "SELECT\n" +
            "\tstudentid,\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\tDATE_ADD(FROM_UNIXTIME(MIN(dateline)), INTERVAL 0 HOUR)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_enrollment e\n" +
            "\t\tWHERE\n" +
            "\t\t\ts.studentid = e.studentid\n" +
            "\t\tAND e.dateline > s.dateline\n" +
            "\t\tGROUP BY\n" +
            "\t\t\tstudentid\n" +
            "\t) `firstEnrollmentTime`\n" +
            "FROM\n" +
            "\tt_student s\n" +
            "WHERE\n" +
            "\tstudentid = %s";


    public static void main(String[] args) throws Exception {
        String sql = "SELECT * FROM da_enrolled_1000 where student_id = 35116";
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANAYLZE", encrytKey);
        ResultSet rs = SQLExecutor.executeQuery(conn, sql)) {
            if (rs.next()) {
                String content = rs.getString("feedback_content");
                JSONArray result = JSONArray.fromObject(content);
            }

        }
    }
}
