package com.doubeye.core.scheduler.singletonVerification;

import com.doubeye.core.scheduler.bean.ScheduleBean;

/**
 * @author doubeye
 * @version 1.0.0
 * 标示符单例，即相同标示符的计划任务，在统一时间只能有一个被激活的任务
 */
public class IdentifierSingleton extends NullVerification{
    @Override
    public boolean isValid() {
        for (ScheduleBean runningSchedule : runningSchedules) {
            if (runningSchedule.getIdentifier().equals(identifier)) {
                return false;
            }
        }
        return true;
    }
}
