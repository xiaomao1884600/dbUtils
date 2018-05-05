package com.doubeye.experiments.dataAnalyze.eEnrollment;

import com.doubeye.experiments.dataGeneration.DataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataGeneratorCollection extends DataGenerator {
    private List<DataGenerator> generators = new ArrayList<>();


    @Override
    public void generate() {
        generators.forEach(generator -> {
            try {
                generator.generate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public void setConnection(Connection conn) {
        this.connection = conn;
        generators.forEach(generator -> generator.setConnection(conn));
    }

    @Override
    public void setStudentInfo(StudentInfo studentInfo) {
        generators.forEach(generator -> generator.setStudentInfo(studentInfo));
    }

    public void addDataGenerationItem(DataGenerator item) {
        generators.add(item);
    }
}
