package com.doubeye.experiments.hadoop.c05;

import com.doubeye.experiments.hadoop.c02.MaxTemperature;
import com.doubeye.experiments.hadoop.c02.MaxTemperatureMapper;
import com.doubeye.experiments.hadoop.c02.MaxTemperatureReducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.jboss.netty.util.internal.jzlib.JZlib;

import java.io.IOException;

public class MaxTemperatureWithCompression {
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c05.MaxTemperatureWithCompression /data/data.gz /result
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length != 2) {
            System.err.println("Usage : MaxTemperatureWithCompression <input path> <output path>");
            System.exit(-1);
        }
        Job job = Job.getInstance();
        //Job job = new Job(); this Constructor is deprecated;
        job.setJobName("Max temperature");
        job.setJarByClass(MaxTemperature.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileOutputFormat.setCompressOutput(job, true);
        FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);


        job.setMapperClass(MaxTemperatureMapper.class);
        job.setCombinerClass(MaxTemperatureReducer.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
