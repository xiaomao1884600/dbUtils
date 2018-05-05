package com.doubeye.experiments.dataAnalyze.eEnrollment;

import com.doubeye.commons.database.sql.SQLExecutor;

import java.sql.Connection;
import java.sql.SQLException;

public class StudentInfoDAO {
    private Connection conn;

    private String tableName;

    public static final String SQL_INSERT_STUDENT_INFO = "INSERT INTO %s(" +
            "student_id,\n" +
            "age,\n" +
            "city_id,\n" +
            "faculty_id,\n" +
            "edu_level,\n" +
            "add_time,\n" +
            "first_enrollment_date,\n" +
            "feedback_count,\n" +
            "feedback_detail,\n" +
            "feedback_content,\n" +
            "enroll_level) VALUES(%d, %d, %d, %d, %d, '%s', %s, %d, '%s', '%s', '%d')";

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void insert(StudentInfo studentInfo) throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_INSERT_STUDENT_INFO,
                tableName,
                studentInfo.getStudentId(),
                studentInfo.getAge(),
                studentInfo.getCityId(),
                studentInfo.getFacultyId(),
                studentInfo.getEduLevel(),
                studentInfo.getAddTime(),
                studentInfo.getFirstEnrollmentDate() == null ? null : "'" + studentInfo.getFirstEnrollmentDate() + "'",
                studentInfo.getFeedbackCount(),
                studentInfo.getFeedbackDetails().toString(),
                studentInfo.getFeedbackContents().toString(),
                studentInfo.getEnrollLevel()));
    }

    public void cleanExistData() throws SQLException {
        SQLExecutor.execute(conn, "TRUNCATE TABLE " + tableName);
    }
}
