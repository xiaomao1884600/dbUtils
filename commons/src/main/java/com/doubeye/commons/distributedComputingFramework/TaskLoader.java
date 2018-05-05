package com.doubeye.commons.distributedComputingFramework;

import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 任务载入器接口
 */
public interface TaskLoader {
    /**
     * 载入任务
     */
    void load();

    /**
     * 设置任务分发器
     * @param distributor 任务分发器
     */
    void setTaskDistributor(TaskDistributor distributor);

    /**
     * 设置载入配置
     * @param config 载入配置，具体配置的参数，由实现类定义
     */
    void setLoadConfig(Map<String, String> config);
}
