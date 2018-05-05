package com.doubeye.experiments.mahout.lr;


import com.doubeye.commons.utils.randomizer.ListRandomizer;
import com.doubeye.commons.utils.randomizer.RangeRandomizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 学生分类数据生成器
 */
public class RandomStudentAttendantGenerator {
    private RangeRandomizer rangeRandomizer;
    private ListRandomizer<String> listRandomizer;
    private RangeRandomizer floatRangeRamdomizer;
    public RandomStudentAttendantGenerator() {
        rangeRandomizer = new RangeRandomizer(0, 6);
        floatRangeRamdomizer = new RangeRandomizer(0f, 5f);
        List<String> drop = new ArrayList();
        drop.add("drop");
        drop.add("no");
        listRandomizer = new ListRandomizer(drop);
    }

    public List<List<Object>> generate() {
        List<List<Object>> students = new ArrayList<>();
        for (int i = 1; i <= 200; i ++) {
            List<Object> student = new ArrayList<>();
            //17表示签到，签退，3道题5天的随机数
            for (int j = 1; j <= 17; j ++) {
                student.add(rangeRandomizer.get());
            }
            for (int j = 1; j<= 6; j ++) {
                student.add(floatRangeRamdomizer.get());
            }
            student.add(listRandomizer.get());
            students.add(student);
        }
        return students;
    }

    public void format(List<List<Object>> students) {
        students.forEach(student -> {
            student.forEach(entry -> {
                if (entry instanceof Number) {
                    System.out.print(entry.toString() + ",");
                } else {
                    System.out.print(entry.toString());
                }
            });
            System.out.println();
        });
    }

    private static List<String> generateColumnNames() {
        List<String> result = new ArrayList<>();
        result.add("login");
        result.add("logout");
        for (int i = 1; i <= 5; i ++) {
            for (int j = 1; j <= 3; j ++) {
                result.add(String.format("d%d_q%d", i, j));
            }
        }
        for (int i = 1; i <= 3; i ++) {
            for (int j = 1; j <= 2; j ++) {
                result.add(String.format("a_q%d_t%d", i, j));
            }
        }
        result.add("drop");
        return result;
    }

    public static void main(String[] args) {
        RandomStudentAttendantGenerator generator = new RandomStudentAttendantGenerator();
        generator.format(generator.generate());
        List<String> columns = generateColumnNames();
        columns.forEach(column -> System.out.print(column + ','));
    }
}
