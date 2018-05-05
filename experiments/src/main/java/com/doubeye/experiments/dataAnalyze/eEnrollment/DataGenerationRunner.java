package com.doubeye.experiments.dataAnalyze.eEnrollment;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.experiments.dataAnalyze.eEnrollment.generators.BasicInformationGenerator;
import com.doubeye.experiments.dataAnalyze.eEnrollment.generators.FeedbackGenerator;
import com.doubeye.experiments.dataAnalyze.eEnrollment.generators.FirstEnrollmentGenerator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DataGenerationRunner {
    private List<String> studentIds;
    private DataGeneratorCollection collection;
    private StudentInfoDAO dao;

    public void run() {
        studentIds.forEach(studentId -> {
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setStudentId(Integer.parseInt(studentId));
            collection.setStudentInfo(studentInfo);
            collection.generate();
            //保存studentinfo
            try {
                dao.insert(studentInfo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }


    public void setCollection(DataGeneratorCollection collection) {
        this.collection = collection;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        DataGenerationRunner runner = new DataGenerationRunner();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection eduConnection = ConnectionManager.getConnection("E-PRODUCT", encrytKey);
            Connection resultConnection = ConnectionManager.getConnection("DATA-ANAYLZE", encrytKey)) {
            DataGeneratorCollection collection = getDataGenerationCollection();
            runner.setCollection(collection);
            runner.setStudentIds(getStudentIds("D:\\workcode\\dbUtils\\experiments\\target\\classes\\dataAnalyze\\enrollment\\报名的学生id.txt"));
            collection.setConnection(eduConnection);
            StudentInfoDAO dao = new StudentInfoDAO();
            dao.setConn(resultConnection);
            dao.setTableName("da_enrolled_1000");
            runner.setDao(dao);
            dao.cleanExistData();
            runner.run();
        }
    }

    private static DataGeneratorCollection getDataGenerationCollection() {
        DataGeneratorCollection collection = new DataGeneratorCollection();
        collection.addDataGenerationItem(new BasicInformationGenerator());
        collection.addDataGenerationItem(new FirstEnrollmentGenerator());
        collection.addDataGenerationItem(new FeedbackGenerator());
        return collection;
    }

    private static List<String> getStudentIds(String fileName) throws IOException {
        return CollectionUtils.loadFromFile(fileName);
    }

    public void setDao(StudentInfoDAO dao) {
        this.dao = dao;
    }
}
