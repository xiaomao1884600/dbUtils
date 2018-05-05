package com.hxsd.services.productLine.monitor;


import com.doubeye.commons.application.ApplicationCache;
import com.hxsd.monitor.productLine.me.MeDatabaseConnectionScheduler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.Map;

import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/10.
 */
public class MonitorService {
    public JSONArray getAllJobs(Map<String, String[]> parameters) throws SchedulerException {
        ApplicationCache applicationCache = ApplicationCache.getInstance();
        Scheduler scheduler = applicationCache.getActivatedScheduler();
        if (scheduler == null) {
            scheduler = MeDatabaseConnectionScheduler.startMeDatabaseConnectionScheduler();
            applicationCache.setActivatedScheduler(scheduler);
        }

        JSONArray result = new JSONArray();
        for(String group: scheduler.getJobGroupNames()) {
            for(JobKey jobKey : scheduler.getJobKeys(groupEquals(group))) {
                JSONObject jobObject = new JSONObject();
                jobObject.put("groupName", jobKey.getGroup());
                jobObject.put("jobName", jobKey.getName());
                String jobFullName = jobKey.getGroup() + "." + jobKey.getName();
                jobObject.put("fullName", jobFullName);
                if (scheduler.getTriggersOfJob(jobKey).size() > 0) {
                    jobObject.put("running", true);
                }
                result.add(jobObject);
            }
        }
        return result;
    }
}
