package com.doubeye.experiments.mahout.lr;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.mahout.classifier.sgd.CsvRecordFactory;
import org.apache.mahout.classifier.sgd.LogisticModelParameters;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * @author doubeye
 * 测试mahout 逻辑回归算法
 */
public class LogisticRegressionTest {

    public static void main(String[] args) throws IOException {
        LogisticModelParameters lmp = new LogisticModelParameters();
        PrintWriter output = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"), true);

        // 2: init params
        lmp.setLambda(0.001);
        lmp.setLearningRate(50);
        //总共有3种iris
        lmp.setMaxTargetCategories(3);
        //看起来除了class只有4种属性，先设定为4
        lmp.setNumFeatures(4);
        //这里使用的是guava里面的api
        List<String> targetCategories = Lists.newArrayList("Iris-setosa", "Iris-versicolor", "Iris-versicolor");
        lmp.setTargetCategories(targetCategories);
        // 需要进行预测的是class属性
        lmp.setTargetVariable("class");
        List<String> typeList = Lists.newArrayList("numeric", "numeric", "numeric", "numeric");
        List<String> predictorList = Lists.newArrayList("sepalLength", "sepalWidth", "petalLength", "petalWidth");
        lmp.setTypeMap(predictorList, typeList);

        // 3. load data
        //使用common-io进行文件读取
        List<String> raw = FileUtils.readLines(new File(
                "d:/workcode/dbUtils/experiments/src/main/resources/logisticRegression/iris.txt"));
        String header = raw.get(0);
        List<String> content = raw.subList(1, raw.size());
        // parse data
        CsvRecordFactory csv = lmp.getCsvRecordFactory();
        // !!!Note: this is a initialize step, do not skip this step
        csv.firstLine(header);

        // 4. begin to train
        OnlineLogisticRegression lr = lmp.createRegression();
        //对于小数据集我们多运行几次
        for(int i = 0; i < 100; i++) {
            for (String line : content) {
                Vector input = new RandomAccessSparseVector(lmp.getNumFeatures());
                int targetValue = csv.processLine(line, input);
                // 核心的一句！！！
                lr.train(targetValue, input);
            }
        }



        // 5. show model performance: show classify score
        double correctRate = 0;
        double sampleCount = content.size();

        for (String line : content) {
            Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
            int target = csv.processLine(line, v);
            // 分类核心语句!!!
            int score = lr.classifyFull(v).maxValueIndex();
            System.out.println("Target:" + target + "\tReal:" + score + "\tContent:" + line);
            if(score == target) {
                correctRate++;
            }
        }
        output.printf(Locale.ENGLISH, "Rate = %.2f%n", correctRate / sampleCount);


    }

}
