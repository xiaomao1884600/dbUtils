package com.doubeye.experiments.hadoop.c08;


import com.doubeye.experiments.hadoop.c06.v4.NcdcRecordParser;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;


public class PartitionByStationUsingMultipleOutputs extends Configured implements Tool{
    static class StationMapper extends Mapper<LongWritable, Text, Text, Text> {
        private NcdcRecordParser parser = new NcdcRecordParser();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            parser.parse(value);
            if (!parser.isAirTemperatureMalformed()) {
                context.write(new Text(parser.getStationId()), value);
            }
        }
    }

    static class MultipleOutputsReducer extends Reducer<Text, Text, NullWritable, Text> {
        private MultipleOutputs<NullWritable, Text> multipleOutputs;
        private NcdcRecordParser parser = new NcdcRecordParser();

        @Override
        protected void setup(Context context) {
            multipleOutputs = new MultipleOutputs<>(context);
        }
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value : values) {
                parser.parse(value);
                String basePath = String.format("%s/%s/part", parser.getStationId(), parser.getYear());
                multipleOutputs.write(NullWritable.get(), value, key.toString());
            }
        }
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            multipleOutputs.close();
        }
    }



    @Override
    public int run(String[] args) throws Exception {
        Job job = JobBuilder.parseInputAndOutput(this, getConf(), args);
        if (job == null) {
            return -1;
        }

        job.setMapperClass(StationMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setReducerClass(MultipleOutputsReducer.class);
        job.setOutputKeyClass(NullWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }
    /*
        hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c08.PartitionByStationUsingMultipleOutputs /data /multiple
     */

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new PartitionByStationUsingMultipleOutputs(), args);
        System.exit(exitCode);
    }
}
