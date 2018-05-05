package com.doubeye.experiments.hadoop.c09;

import com.doubeye.experiments.hadoop.c08.JobBuilder;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class MissingTemperatureFields extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 1) {
            JobBuilder.printUsage(this, "<job ID>");
            return -1;
        }
        String jobID = args[0];
        Cluster cluster = new Cluster(getConf());

        Job job = cluster.getJob(JobID.forName(jobID));
        if (job == null) {
            System.err.printf("No job with ID %s found.\n", jobID);
            return -1;
        }
        /*
        if (!job.isComplete()) {
            System.err.printf("Job %s is not complete.\n", jobID);
            return -1;
        }
        */

        Counters counters = job.getCounters();
        long missing = counters.findCounter(MaxTemperatureWithCounters.Temperature.MISSING).getValue();
        long total = counters.findCounter(TaskCounter.MAP_INPUT_RECORDS).getValue();

        System.out.printf("Records with missing temperature fields : %.2f%%\n", 100.0 * missing / total);
        return 0;
    }

    /*
    hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c09.MissingTemperatureFields job_1503312170897_0002
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MissingTemperatureFields(), args);
        System.exit(exitCode);
    }
    /*
    17/08/21 18:51:00 INFO mapred.ClientServiceDelegate: Application state is completed. FinalApplicationStatus=SUCCEEDED. Redirecting to job history server
    Caused by: java.net.ConnectException: Call From DESKTOP-zhanglu1782/10.2.20.63 to 0.0.0.0:10020 failed on connection exception: java.net.ConnectException: Connection refused: no further information; For more details see:  http://wiki.apache.org/hadoop/ConnectionRefused
     */
}
