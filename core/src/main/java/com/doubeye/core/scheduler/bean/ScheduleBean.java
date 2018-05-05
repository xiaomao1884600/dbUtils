package com.doubeye.core.scheduler.bean;

import net.sf.json.JSONObject;

/**
 * @author doubeye
 * @version 1.0.0
 * Schedule的对象
 */
public class ScheduleBean {
    /**
     * 唯一编号
     */
    private int id;
    /**
     * 名称
     */
    private String name;
    /**
     * 标示符
     */
    private String identifier;
    /**
     * 任务类型，具体字典参照dict_schedule_type
     */
    private int scheduleType;
    /**
     * 配置，具体解释需参考实际的任务类
     */
    private JSONObject initConfig;
    /**
     * 运行配置，具体解释需参考实际的任务类
     */
    private JSONObject runConfig;
    /**
     * 单例类型，具体类型需参考dict_singleton_type
     */
    private int singletonType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public JSONObject getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(JSONObject initConfig) {
        this.initConfig = initConfig;
    }

    public JSONObject getRunConfig() {
        return runConfig;
    }

    public void setRunConfig(JSONObject runConfig) {
        this.runConfig = runConfig;
    }

    public int getSingletonType() {
        return singletonType;
    }

    public void setSingletonType(int singletonType) {
        this.singletonType = singletonType;
    }
}
