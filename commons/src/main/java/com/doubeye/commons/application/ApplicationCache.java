package com.doubeye.commons.application;

import com.doubeye.commons.utils.ProgressedRunnable;
import org.quartz.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 应用级别缓存，用来在单个应用节点中做缓存
 */
public class ApplicationCache {
    /**
     * 单例模式使用
     */
    private static volatile ApplicationCache applicationCache;

    /**
     * 构造函数
     */
    private ApplicationCache() {
         runningTasks = new HashMap<>();
    }

    /**
     * 获得实例
     * @return 缓存对象实例
     */
    public static ApplicationCache getInstance() {
        if (applicationCache == null) {
            synchronized (ApplicationCache.class) {
                if (applicationCache == null) {
                    applicationCache = new ApplicationCache();
                }
            }
        }
        return applicationCache;
    }

    /**
     * 加入一个任务
     * @param key 任务的键
     * @param task 任务对象
     */
    public void addTask(String key, ProgressedRunnable task) {
        ApplicationCache.getInstance().runningTasks.put(key, task);
    }

    /**
     * 根据键获得任务对象
     * @param key 任务的键
     * @return 相应的对象
     */
    public ProgressedRunnable getTask(String key) {
        return ApplicationCache.getInstance().runningTasks.get(key);
    }

    /**
     * 移除任务
     * @param key 要移除的对象的键
     */
    public void removeTask(String key) {
        ApplicationCache.getInstance().runningTasks.remove(key);
    }

    /**
     * 保存所有任务的Map
     */
    private Map<String, ProgressedRunnable> runningTasks
            ;
    /**
     * 任务调度器
     */
    private Scheduler activatedScheduler;

    /**
     * 获得所有缓存的key信息
     * @return 所有缓存的key信息
     */
    public Set<String> getAllTaskKeys() {
        ApplicationCache applicationCache = ApplicationCache.getInstance();
        return applicationCache.runningTasks.keySet();
    }

    public Map<String, ProgressedRunnable> getRunningTasks() {
        return ApplicationCache.getInstance().runningTasks;
    }


    public Scheduler getActivatedScheduler() {
        return activatedScheduler;
    }

    public void setActivatedScheduler(Scheduler activatedScheduler) {
        this.activatedScheduler = activatedScheduler;
    }
}
