package com.doubeye.record.schedule.rl;

import com.doubeye.record.recognition.task.file.RlToOSS;
import com.doubeye.record.schedule.PostTaskJob;
import com.doubeye.record.schedule.RetrieveTaskJob;
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
 * 容联上传阿里云OSS服务启动器
 */
public class RlRecordFilePosterJobStarter {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail postJob = newJob(FilePosterJob.class).withIdentity("postJob", "group1").build();
            Trigger postTrigger = newTrigger().withIdentity("postTrigger", "group1").startNow().withSchedule(simpleSchedule().withIntervalInMinutes(2).repeatForever()).build();
            scheduler.scheduleJob(postJob, postTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
