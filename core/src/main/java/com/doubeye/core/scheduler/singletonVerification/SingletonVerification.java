package com.doubeye.core.scheduler.singletonVerification;

import com.doubeye.commons.utils.ProgressedRunnable;
import com.doubeye.core.scheduler.bean.ScheduleBean;

import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 用来验证任务是否满足单例条件
 */
interface SingletonVerification {
    /**
     * 设置任务标示符
     * @param identifier 任务标示符
     */
    void setIdentifier(String identifier);

    /**
     * 设置任务名称
     * @param name 任务名称
     */
    void setName(String name);

    /**
     * 获得任务标示符
     * @return 任务标示符
     */
    String getIdentifier();

    /**
     * 获得任务名称
     * @return 任务名称
     */
    String getName();

    /**
     * 判断任务是否可被创建
     * @return 如果任务满足单例模式，可以被创建，返回true，否则返回false
     */
    boolean isValid();

    void setRunningSchedule(List<ScheduleBean> runningSchedules);
}
