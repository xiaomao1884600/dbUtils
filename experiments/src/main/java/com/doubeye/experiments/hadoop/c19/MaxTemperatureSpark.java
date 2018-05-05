package com.doubeye.experiments.hadoop.c19;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class MaxTemperatureSpark {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperatureSpark <input path> <output path>");
            System.exit(-1);
        }


        SparkConf conf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext("local", "MaxTemperatureSpark", conf);
        JavaRDD<String> lines = sc.textFile(args[0]);

        JavaRDD<String[]> records = lines.map(new Function<String, String[]>() {
            @Override
            public String[] call(String s) throws Exception {
                return s.split("\t");
            }
        });

        JavaRDD<String[]> filterd = records.filter(new Function<String[], Boolean>() {
            @Override
            public Boolean call(String[] rec) throws Exception {
                return rec[1].equals("9999") && rec[2].matches("01459");
            }
        });

        JavaPairRDD<Integer, Integer> tuples = filterd.mapToPair(
                new PairFunction<String[], Integer, Integer>() {
                    @Override
                    public Tuple2<Integer, Integer> call(String[] rec) throws Exception {
                        return new Tuple2<Integer, Integer>(Integer.parseInt(rec[0]), Integer.parseInt(rec[1]));
                    }
                }
        );

        JavaPairRDD<Integer, Integer> maxTemps = tuples.reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) throws Exception {
                        return Math.max(integer, integer2);
                    }
                }
        );

        maxTemps.saveAsTextFile(args[1]);

    }
}
