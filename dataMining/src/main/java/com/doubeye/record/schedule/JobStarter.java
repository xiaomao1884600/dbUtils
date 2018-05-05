package com.doubeye.record.schedule;

import com.doubeye.test.HelloJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author doubeye
 * 腾讯云识别任务启动器
 */
public class JobStarter {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail postJob = newJob(PostTaskJob.class).withIdentity("postJob", "group1").build();
            Trigger postTrigger = newTrigger().withIdentity("postTrigger", "group1").startNow().withSchedule(simpleSchedule().withIntervalInHours(1).repeatForever()).build();
            scheduler.scheduleJob(postJob, postTrigger);
            Thread.sleep(2 * 60 * 1000);
            JobDetail retrieveJob = newJob(RetrieveTaskJob.class).withIdentity("retrieval", "group1").build();
            Trigger retrieveTrigger = newTrigger().withIdentity("retrievalTrigger", "group1").startNow().withSchedule(simpleSchedule().withIntervalInHours(1).repeatForever()).build();
            scheduler.scheduleJob(retrieveJob, retrieveTrigger);
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
