package com.doubeye.experiments.dataAnalyze.eEnrollment.generators;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.jsonBuilder.JSONWrapper;
import com.doubeye.experiments.dataAnalyze.eEnrollment.StudentInfo;
import com.doubeye.experiments.dataGeneration.DataGenerator;
import net.sf.json.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicInformationGenerator extends DataGenerator {
    private static Logger logger = LogManager.getLogger(BasicInformationGenerator.class);
    @Override
    public void generate() throws SQLException {
        StudentInfo studentInfo = getStudentInfo();
        try (ResultSet resultSet = SQLExecutor.executeQuery(connection, String.format(SQL_GET_STUDENT_BASIC_INFORMATION, studentInfo.getStudentId()))) {
            if (resultSet.next()) {
                studentInfo.setAddTime(resultSet.getString("dateline"));
                studentInfo.setAge(resultSet.getInt("age"));
                studentInfo.setCityId(resultSet.getInt("cityid"));
                studentInfo.setEnrollLevel(resultSet.getInt("edulevel"));
            }
        }
    }

    private static final String SQL_GET_STUDENT_BASIC_INFORMATION = "SELECT\n" +
            "\ts.studentid,\n" +
            "\tcityid,\n" +
            "\tage,\n" +
            "\tedulevel,\n" +
            "\tDATE_ADD(FROM_UNIXTIME(s.dateline), INTERVAL 0 HOUR) dateline\n" +
            "FROM\n" +
            "\tt_student s\n" +
            "INNER JOIN t_studentinfo si ON s.studentid = si.studentid\n" +
            "INNER JOIN t_student_education se ON s.studentid = se.studentid\n" +
            "WHERE s.studentid = %s";
}
