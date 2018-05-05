package com.doubeye.experiments.hadoop.c08;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;

public class JobBuilder {
    public static Job parseInputAndOutput(Tool tool, Configuration configuration, String[] args) throws IOException {
        if (args.length != 2) {
            printUsage(tool, "<input> <output>");
            return null;
        }
        Job job = new Job(configuration);
        job.setJarByClass(tool.getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        return job;
    }

    /*
    hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c09.parseInputAndOutput -D mapreduce.job.reduces=2
     */
    public static void printUsage(Tool tool, String extraArgsUsage) {
        System.err.printf("Usage : %s [genericOptions] %s\n\n", tool.getClass().getSimpleName(), extraArgsUsage);
    }
}
