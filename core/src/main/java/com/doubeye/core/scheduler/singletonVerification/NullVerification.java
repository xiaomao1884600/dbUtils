package com.doubeye.core.scheduler.singletonVerification;

import com.doubeye.core.scheduler.bean.ScheduleBean;

import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 无验证单例模式，处于此模式下的计划任务不受任何限制，同时此类可作为其他验证类的助手类
 */
@SuppressWarnings("unused")
public class NullVerification implements SingletonVerification{
    protected String identifier;
    protected String name;
    protected List<ScheduleBean> runningSchedules;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setRunningSchedule(List<ScheduleBean> runningSchedules) {
        this.runningSchedules = runningSchedules;
    }


}
