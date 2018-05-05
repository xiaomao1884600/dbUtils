package com.doubeye.experiments.hadoop.c09;

import com.doubeye.experiments.hadoop.c06.MaxTemperatureReducer;
import com.doubeye.experiments.hadoop.c08.JobBuilder;
import com.doubeye.experiments.hadoop.commons.NcdcRecordParser;
import com.doubeye.experiments.hadoop.commons.NcdcStationMetadata;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;
import java.io.IOException;

public class MaxTemperatureByStationNameUsingDistributedCacheFile extends Configured implements Tool {


    static class StationTemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private static NcdcRecordParser parser = new NcdcRecordParser();
        @Override
        protected void map(LongWritable key ,Text value, Context context) throws IOException, InterruptedException {
            parser.parse(value);
            if (parser.isValidTemperature()) {
                context.write(new Text(parser.getStationId()), new IntWritable(parser.getAirTemperature()));
            }
        }
    }

    static class MaxTemperatureReducerWithStationLookup extends Reducer<Text, IntWritable, Text, IntWritable> {
        private NcdcStationMetadata metadata;

        @Override
        protected void setup(Context context) throws IOException {
            metadata = new NcdcStationMetadata();
            metadata.initialize(new File("stations-fixed-width.txt"));
        }

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            String stationName = metadata.getStationName(key.toString());

            int maxValue = Integer.MIN_VALUE;
            for(IntWritable value : values) {
                maxValue = Math.max(maxValue, value.get());
            }

            context.write(new Text(stationName), new IntWritable(maxValue));
        }
    }


    @Override
    public int run(String[] args) throws Exception {
        Configuration config = getConf();
        config.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        Job job = JobBuilder.parseInputAndOutput(this, config, args);
        if (job == null) {
            return -1;
        }
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(StationTemperatureMapper.class);
        job.setCombinerClass(MaxTemperatureReducer.class);
        job.setReducerClass(MaxTemperatureReducerWithStationLookup.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    /*
    将示例文件放到d：根目录下的metadata文件夹，调用一下命令
    hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c09.MaxTemperatureByStationNameUsingDistributedCacheFile -files /metadata/stations-fixed-width.txt /c09_sample /c09_cacheFile
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MaxTemperatureByStationNameUsingDistributedCacheFile(), args);
        System.exit(exitCode);
    }
}