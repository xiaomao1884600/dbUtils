package com.hxsd.monitor.productLine.me;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/9.
 */

public class MeDatabaseConnectionScheduler {
    /*
    public static void main(String[] args) {
        SchedulerFactory schemaFactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = schemaFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(MeDatabaseConnectionMonitor.class).withIdentity("MeDatabaseConnectionMonitor", "group1").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger", "triggerGroup1")
                    .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(1)).startNow().build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    */

    public static Scheduler startMeDatabaseConnectionScheduler() {
        SchedulerFactory schemaFactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = schemaFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(MeDatabaseConnectionMonitor.class).withIdentity("MeDatabaseConnectionMonitor", "group1").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger", "triggerGroup1")
                    .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(1)).startNow().build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduler;
    }
}
