package com.doubeye.experiments.hadoop.c06.v3;

import com.doubeye.experiments.hadoop.c06.NcdcRecordParser;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MaxTemperatureMapperWithDebug extends Mapper<LongWritable, Text, Text, IntWritable> {
    public enum Temperature {
        OVER_100
    }

    private com.doubeye.experiments.hadoop.c06.v4.NcdcRecordParser parser = new com.doubeye.experiments.hadoop.c06.v4.NcdcRecordParser();
    private int index = 0;

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        parser.parse(value);
        index ++;
        if (index <= 20) {
            System.out.println(parser.getAirTemperature() + " " + parser.isValidTemperature());
        }
        if (parser.isValidTemperature()) {
            int airTemperature = parser.getAirTemperature();
            if (airTemperature > 1000) {
                System.err.println("Temperature over 100 degrees for input: " + value);
                context.setStatus("Detected possibly corrupt record: see logs.");
                context.getCounter(Temperature.OVER_100).increment(1);
            }
            context.write(new Text(parser.getYear()), new IntWritable(airTemperature));
        }
    }
}