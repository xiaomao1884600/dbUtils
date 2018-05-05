package com.doubeye.experiments.hadoop.c09;

import com.doubeye.experiments.hadoop.c08.JobBuilder;
import com.doubeye.experiments.hadoop.commons.NcdcRecordParser;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;


import org.apache.hadoop.io.*;


import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class LookupRecordsByTemperature extends Configured implements Tool{
    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            JobBuilder.printUsage(this, "<path> <key>");
            return -1;
        }

        Path path = new Path(args[0]);
        LongWritable key = new LongWritable(Long.parseLong(args[1]));
        //IntWritable key = new IntWritable(Integer.parseInt(args[1]));

        MapFile.Reader[] readers = MapFileOutputFormat.getReaders(path, getConf());
        Partitioner<LongWritable, Text> partitioner = new HashPartitioner<>();
        // Partitioner<IntWritable, Text> partitioner = new HashPartitioner<>();
        Text val = new Text();

        MapFile.Reader reader = readers[partitioner.getPartition(key, val, readers.length)];
        Writable entry = reader.get(key, val);

        if (entry == null) {
            System.err.println("key not found:" + key);
            return -1;
        }
        NcdcRecordParser parser = new NcdcRecordParser();
        LongWritable nextKey = new LongWritable();
        do {
            parser.parse(val.toString());
            System.out.printf("%s\t%s\n", parser.getStationId(), parser.getYear());
        } while (reader.next(nextKey, val) && key.equals(nextKey));

        return 0;
    }
    /*
    hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c09.LookupRecordTemperature /c09_data1 100
    本例依赖SortByTemperatureToMapFile的输出，且为了能够正确执行，在SortByTemperatureToMapFile不要使用压缩
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new LookupRecordsByTemperature(), args);
        System.exit(exitCode);
    }
}
