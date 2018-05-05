package com.doubeye.experiments.hadoop.c06.v4;

import com.doubeye.experiments.hadoop.c06.NcdcRecordParser;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MaxTemperatureMapperV4 extends Mapper<LongWritable, Text, Text, IntWritable> {
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
        if (parser.isValidTemperature() && !parser.isAirTemperatureMalformed()) {
            int airTemperature = parser.getAirTemperature();
            context.write(new Text(parser.getYear()), new IntWritable(airTemperature));
        }
        if (parser.isAirTemperatureMalformed()) {
            System.err.println("Ignoring possibly corrupt input: " + value);
            context.getCounter(MaxTemperatureMapperV4.Temperature.OVER_100).increment(1);
            System.err.println("dfsdfadfafd " + context.getCounter(MaxTemperatureMapperV4.Temperature.OVER_100));
        }
    }
}