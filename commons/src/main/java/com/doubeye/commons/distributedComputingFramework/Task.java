package com.doubeye.commons.distributedComputingFramework;

import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 任务接口
 */
public interface Task {

    /**
     * 获得任务id
     * @return 任务的唯一id
     */
    int getId();

    /**
     * 设置任务的id
     * @param id 任务id
     */
    void setId(int id);

    /**
     * 获得任务内容，格式为String， 如果需要可将其转化为JSONObject
     * @return 任务的内容
     */
    String getTaskContent();

    /**
     * 设置任务内容
     * @param taskContent 任务内容
     */
    void setTaskContent(String taskContent);

    /**
     * 获得失败的次数
     * @return 失败的次数
     */
    int getFailCount();

    /**
     * 获得任务的失败信息
     * @return 失败信息列表
     */
    List<TaskFailInformation> getFailures();

    /**
     * 添加一次运行失败
     * @param information 运行的失败信息
     */
    void addFailure(TaskFailInformation information);
}
