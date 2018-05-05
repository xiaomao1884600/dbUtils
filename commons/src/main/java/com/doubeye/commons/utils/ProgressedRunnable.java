package com.doubeye.commons.utils;

/**
 * @author doubeye
 * @version 1.0.0
 * 带运行进度的可运行接口
 */
public interface ProgressedRunnable extends Runnable {
    /**
     * 获得进度
     * @return 返回运行的进度
     */
    float getProgress();

    /**
     * 获得进度描述
     * @return 运行进度描述
     */
    String getProgressDescription();

    /**
     * 获得运行总时间
     * @return 运行总时间
     */
    long getTotalRunCost();

    /**
     * 获得运行中的错误个数
     * @return 运行错误个数
     */
    int getErrorCount();
}
