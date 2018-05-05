package com.doubeye.commons.distributedComputingFramework;

/**
 * @author doubeye
 * @version 1.0.0
 * 任务运行成功后的提交器
 */
public interface TaskCommitter {
    /**
     * 提交任务
     */
    public void doCommit(Task task);
}
