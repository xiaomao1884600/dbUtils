package com.doubeye.experiments.mahout.lr;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.classifier.sgd.CsvRecordFactory;
import org.apache.mahout.classifier.sgd.LogisticModelParameters;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;

import java.io.*;
import java.sql.Connection;
import java.util.*;

/**
 * @author doubeye
 * 根据开学前3天的签到，签退，教师打分，缴费比例，以及开课后是否有缴费动作，判断学生是可能退学，来预测第四天及以后是否有退学
 */
public class JudgeStudentDrop {
    public static void main(String[] args) throws Exception {
        LogisticModelParameters lmp = new LogisticModelParameters();
        PrintWriter output = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"), true);

        // 2: init params
        lmp.setLambda(0.001);
        lmp.setLearningRate(50);
        //总共有2中可能，退学（drop），不退学（no）,退班（out）
        lmp.setMaxTargetCategories(3);
        //只使用签到数，签退数以及第一类的三种平均分
        lmp.setNumFeatures(13);
        //退学，不退学
        List<String> targetCategories = Lists.newArrayList("drop", "no", "out");
        lmp.setTargetCategories(targetCategories);
        // 需要进行预测的是drop属性
        lmp.setTargetVariable("drop");
        //使用签到、签退的和第二类平均值数据作为训练
        //List<String> typeList = Lists.newArrayList("numeric", "numeric", "numeric", "numeric", "numeric");
        //List<String> predictorList = Lists.newArrayList("login", "logout", "a_q1_t2", "a_q2_t2", "a_q3_t2");
        //使用签到、签退的和第一类平均值数据作为训练
        //List<String> typeList = Lists.newArrayList("numeric", "numeric", "numeric", "numeric", "numeric");
        //List<String> predictorList = Lists.newArrayList("login", "logout", "a_q1_t1", "a_q2_t1", "a_q3_t1");
        //使用前三天的数据来作为判断
        List<String> typeList = new ArrayList<>();
        //9 + 2 + 2
        for (int i = 1; i <= 13; i ++) {
            typeList.add("numeric");
        }
        List<String> predictorList = new ArrayList<>();
        //签到次数
        predictorList.add("login");
        //签退次数
        predictorList.add("logout");
        for (int i = 1; i <= 3; i ++) {
            for (int j = 1; j <= 3; j ++) {
                predictorList.add(String.format("d%d_q%d", i, j));
            }
        }
        //缴费比例
        predictorList.add("feePercent");
        //开课后是否有缴费动作
        predictorList.add("paid");
        lmp.setTypeMap(predictorList, typeList);

        // 3. load data 训练集
        //使用common-io进行文件读取
        List<String> raw = FileUtils.readLines(new File(
                "d:/workcode/dbUtils/experiments/src/main/resources/logisticRegression/20180407_training.txt"));
                //"d:/workcode/dbUtils/experiments/src/main/resources/logisticRegression/20180328_150412.txt"));
        String header = raw.get(0);
        List<String> content = raw.subList(1, raw.size());
        // parse data
        CsvRecordFactory csv = lmp.getCsvRecordFactory();
        // !!!Note: this is a initialize step, do not skip this step
        csv.firstLine(header);

        // 4. begin to train
        OnlineLogisticRegression lr = lmp.createRegression();
        //对于小数据集我们多运行几次
        for (int i = 0; i < 100; i++) {
            for (String line : content) {
                Vector input = new RandomAccessSparseVector(lmp.getNumFeatures());
                int targetValue = 0;
                try {
                    targetValue = csv.processLine(line, input);
                } catch (Exception e) {
                    System.out.println("error_" +  line);
                    throw  e;
                }
                // 核心的一句！！！
                lr.train(targetValue, input);
            }
       }


        // 5. show model performance: show classify score
        //List<String> enrolledStudents = loadAllEnrolled("logisticRegression/all_enrolled.txt");
        List<String> enrolledStudents = loadAllEnrolled("logisticRegression/20180427_091218.txt");
        /*
         * 已确认退学的学号列表
         */
        List<String> confirmedDrop = new ArrayList<>();
        /*
         * 预测会退学，但未退学学号列表
         */
        List<String> expectingDrop = new ArrayList<>();

        double correctRate = 0;
        double sampleCount = enrolledStudents.size();

        for (String line : enrolledStudents) {
            Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
            int target = 0;
            try {
                target = csv.processLine(line, v);
            } catch (Exception e) {
                //
            }

            // 分类核心语句!!!
            int score = lr.classifyFull(v).maxValueIndex();

            //System.out.println("Target:" + target + "\tReal:" + score + "\tContent:" + line);


            if (score == 0) {
                String studentNumber = StringUtils.substringAfterLast(line, CommonConstant.SEPARATOR.COMMA.toString());
                if (target == score) {
                    System.out.println("BINGO     Real:" + target + "\tExpected:" + score + "\tContent:" + line);
                    confirmedDrop.add(studentNumber);
                } else {
                    expectingDrop.add(studentNumber);
                    System.out.println("FALSE POSITIVE     Real:" + target + "\tExpected:" + score + "\tContent:" + line);
                }
            } else if (score == 1){
                if (target != score) {
                    System.out.println("FALSE NEGATIVED     Real:" + target + "\tExpected:" + score + "\tContent:" + line);
                }
            }
            if (score == target) {
                correctRate++;
            }
        }
        output.printf(Locale.ENGLISH, "Rate = %.2f%n", correctRate / sampleCount);
        output.printf(Locale.ENGLISH, "成功预测退班的学生数量为%d", confirmedDrop.size());


        //com.doubeye.commons.utils.file.FileUtils.toFile("d:/confirmDrop.txt", new StringBuilder(CollectionUtils.split(confirmedDrop, CommonConstant.SEPARATOR.COMMA.toString() + "\r\n", CommonConstant.SEPARATOR.SINGLE_QUOTE_MARK.toString())));
        //com.doubeye.commons.utils.file.FileUtils.toFile("d:/expectingDrop.txt", new StringBuilder(CollectionUtils.split(expectingDrop, CommonConstant.SEPARATOR.COMMA.toString() + "\r\n", CommonConstant.SEPARATOR.SINGLE_QUOTE_MARK.toString())));
        StudentEnrollHelper helper = new StudentEnrollHelper();
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection eProductionConnection = ConnectionManager.getConnection("E-PRODUCT", encryptKey)) {
            helper.setConn(eProductionConnection);
            helper.setStudentNumbers(expectingDrop);
            helper.setTermId(168);
            List<String> expectation = helper.getEnrollmentInfo();
            Map<String, String> signInData = loadSignInData(enrolledStudents);
            List<String> appendedExpectation = new ArrayList<>();
            //载入退学学生学号列表
            List<String> droppedStudentNumber = helper.getDropStudentNumber("2018-04-26", "2018-04-29");
            for (String element : expectation) {
                String[] fieldException = element.split(",");
                if (fieldException.length == 8) {
                    String studentNumber = fieldException[2];
                    String signInEntry = signInData.get(studentNumber);
                    String appendExpectation = appendExpectation(element, signInEntry) + "." + (droppedStudentNumber.contains(studentNumber) ? "true" : "false");
                    appendedExpectation.add(appendExpectation);

                }
            }
            com.doubeye.commons.utils.file.FileUtils.toFile("d:/expectingDrop.txt", JSONArray.fromObject(appendedExpectation));
        }
    }

    private static List<String> loadAllEnrolled(String fileName) throws IOException {
        return CollectionUtils.loadFromFile(Thread.currentThread().getContextClassLoader().getResource(fileName).getFile());
    }

    private static Map<String, String> loadSignInData(List<String> data) {
        Map<String, String> result = new HashMap<>();
        String studentNumber;
        for (String element : data) {
            studentNumber = StringUtils.substringAfterLast(element, CommonConstant.SEPARATOR.COMMA.toString());
            result.put(studentNumber, element);
        }
        return result;
    }

    private static String appendExpectation(String expectation, String signIn) {
        StringBuilder result = new StringBuilder();
        result.append(expectation);
        String[] signInArray = signIn.split(CommonConstant.SEPARATOR.COMMA.toString());
        if (signInArray.length == 25) {
            for (int i = 0; i < 11; i ++) {
                result.append(CommonConstant.SEPARATOR.COMMA.toString()).append(signInArray[i]);
            }
        }
        return result.toString();
    }

}
