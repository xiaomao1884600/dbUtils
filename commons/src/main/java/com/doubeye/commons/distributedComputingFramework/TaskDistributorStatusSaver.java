package com.doubeye.commons.distributedComputingFramework;

import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0.0
 * 保存TaskDistributor的运行状态
 */
public interface TaskDistributorStatusSaver {
    /**
     * 保存运行状态
     */
    void saveDistributorStatus(TaskDistributor distributor) throws IOException;

    /**
     * 设置参数
     * @param configuration 参数对象，具体参数由实体类来定义
     */
    void setConfiguration(JSONObject configuration);
}
