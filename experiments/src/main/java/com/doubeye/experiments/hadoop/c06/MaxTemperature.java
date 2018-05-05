package com.doubeye.experiments.hadoop.c06;

import com.doubeye.experiments.hadoop.c02.MaxTemperatureReducer;
import com.doubeye.experiments.hadoop.c06.v3.MaxTemperatureMapperWithDebug;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/5/31.
 * hadoop fs -mkdir /data
 * hdfs dfs -put d:\tmp /data
 * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c06.MaxTemperature /data /result
 *
 * 删除文件夹 hadoop fs -rm -r /result2
 */
public class MaxTemperature {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length != 2) {
            System.err.println("Usage : MaxTemperature <input path> <output path>");
            System.exit(-1);
        }
        Job job = Job.getInstance();
        //Job job = new Job(); this Constructor is deprecated;
        job.setJobName("Max temperature");
        job.setJarByClass(MaxTemperature.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(MaxTemperatureMapperWithDebug.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
