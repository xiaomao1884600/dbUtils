package com.doubeye.commons.utils.runtime;

import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 运行时返回结果
 */
public class RuntimeRunningResult {
    /**
     * 运行结果
     */
    private boolean runningResult;
    /**
     * 错误信息
     */
    private List<String> errorMessage;
    /**
     * 返回的其他信息
     */
    private List<String> message;

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public boolean getRunningResult() {
        return runningResult;
    }
    @SuppressWarnings("WeakerAccess")
    public void setRunningResult(boolean runningResult) {
        this.runningResult = runningResult;
    }

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }
}
