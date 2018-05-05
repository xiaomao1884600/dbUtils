package com.doubeye.experiments.dataGeneration;

import com.doubeye.experiments.dataAnalyze.eEnrollment.StudentInfo;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataGenerator {
    private StudentInfo studentInfo;

    protected Connection connection;

    public abstract void generate() throws SQLException;

    public StudentInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
    }

    public void setConnection(Connection conn) {
        this.connection = conn;
    }
}
