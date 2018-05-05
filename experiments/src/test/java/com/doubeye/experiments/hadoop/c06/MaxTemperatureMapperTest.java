package com.doubeye.experiments.hadoop.c06;

import com.doubeye.experiments.hadoop.c02.MaxTemperatureMapper;

import com.doubeye.experiments.hadoop.c06.v3.MaxTemperatureMapperWithDebug;
import com.doubeye.experiments.hadoop.c06.v4.MaxTemperatureMapperV4;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapred.OutputLogFilter;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
;
import static org.hamcrest.CoreMatchers.is;


public class MaxTemperatureMapperTest {
    @Test
    public void processesValidRecord() throws IOException {
        Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" +
                "99999V0203201N00261220001CN9999999N9-00111+99999999999");
        new MapDriver<LongWritable, Text, Text, IntWritable>().withMapper(new MaxTemperatureMapper()).withInput(new LongWritable(0), value).withOutput(new Text("1950"), new IntWritable(-11)).runTest();
    }

    @Test
    public void ignoresMissingTemperatureRecord() throws IOException {
        Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" +
                                                   // Year ^^^^
                "99999V0203201N00261220001CN9999999N9+99991+99999999999");
                                             // Temperature ^^^^^
        // new MapDriver<LongWritable, Text, Text, IntWritable>().withMapper(new com.doubeye.experiments.hadoop.c06.v1.MaxTemperatureMapperWithDebug()).withInput(new LongWritable(0), value).runTest();
        new MapDriver<LongWritable, Text, Text, IntWritable>().withMapper(new com.doubeye.experiments.hadoop.c06.v2.MaxTemperatureMapper()).withInput(new LongWritable(0), value).runTest();

    }
    @Test
    public void returnsMaximumIntegerInValues() throws IOException {
        new ReduceDriver<Text, IntWritable, Text, IntWritable>().withReducer(new MaxTemperatureReducer()).withInput(new Text("1950"), Arrays.asList(new IntWritable(10), new IntWritable(5))).withOutput(new Text("1950"), new IntWritable(10)).runTest();
    }
    @Test
    public void test() throws Exception {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "file:///");
        configuration.set("mapreduce.framework.name", "local");
        configuration.setInt("mapreduce.task.io.sort.mb", 1);

        Path input = new Path("D:\\download\\hadoop-book-master\\input\\ncdc\\micro\\");
        Path output = new Path("d:/result");

        FileSystem fs = FileSystem.getLocal(configuration);
        fs.delete(output, true);
        MaxTemperatureDriver driver = new MaxTemperatureDriver();
        driver.setConf(configuration);

        int exitCode = driver.run(new String[] {
           input.toString(), output.toString(),
        });
        assertThat(exitCode, is(0));

        checkOutput(configuration, output);
    }

    private void checkOutput(Configuration configuration, Path output) throws IOException {
        FileSystem fs = FileSystem.getLocal(configuration);
        Path[] outputFiles = FileUtil.stat2Paths(fs.listStatus(output, new OutputLogFilter()));
        assertThat(outputFiles.length, is(2));
        BufferedReader actual = asBufferedReader(fs.open(outputFiles[0]));
        BufferedReader expected = asBufferedReader(getClass().getResourceAsStream("/expected.txt"));

        String expectedLine;
        while ((expectedLine = expected.readLine()) != null) {
            assertThat(actual.readLine(), is(expectedLine));
        }
        assertThat(actual.readLine(), nullValue());
        actual.close();
        expected.close();
    }
    private BufferedReader asBufferedReader(InputStream in) {
        return new BufferedReader(new InputStreamReader(in));
    }

    @Test
    public void parsesMalformedTemperature() throws IOException,
            InterruptedException {
        Text value = new Text("0335999999433181957042302005+37950+139117SAO +0004" +
// Year ^^^^
                "RJSN V02011359003150070356999999433201957010100005+353");
        Counters counters = new Counters();
        new MapDriver<LongWritable, Text, Text, IntWritable>()
                .withMapper(new MaxTemperatureMapperV4())
                .withInput(new LongWritable(0), value)
                .withCounters(counters)
                .runTest();
        Counter c = counters.findCounter(MaxTemperatureMapperV4.Temperature.OVER_100);
        System.out.println(c.getValue());
        //assertThat(c.getValue(), is(1L));
    }
}
