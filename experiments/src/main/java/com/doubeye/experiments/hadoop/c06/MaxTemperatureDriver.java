package com.doubeye.experiments.hadoop.c06;

import com.doubeye.experiments.hadoop.c06.v2.MaxTemperatureMapper;
import com.doubeye.experiments.hadoop.c06.v3.MaxTemperatureMapperWithDebug;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class MaxTemperatureDriver extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.printf("Usage : %s [generic options] <input> <output>\n",
                    getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        System.out.println(getConf().get("mapreduce.framework.name"));

        Job job = new Job(getConf(), "Max temperature");
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(MaxTemperatureMapperWithDebug.class);
        job.setCombinerClass(MaxTemperatureReducer.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        return job.waitForCompletion(true) ? 0 : 1;

    }
    /**
     hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c06.MaxTemperatureDriver -conf /hadoop-local.xml d:\data\ d:\result
     hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c06.MaxTemperatureDriver -fs file:/// -jt local d:\data\ d:\result
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MaxTemperatureDriver(), args);
        System.out.println(exitCode);
    }
}
