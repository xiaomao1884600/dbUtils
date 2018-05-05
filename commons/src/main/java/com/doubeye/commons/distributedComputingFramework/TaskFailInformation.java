package com.doubeye.commons.distributedComputingFramework;

public class TaskFailInformation {
    public static final String TASK_FAIL_PHASE_RUNNING = "RUNNING";
    public static final String TASK_FAIL_PHASE_COMMIT = "COMMIT";

    public static final String TASK_FAIL_CAUSE_TIMEOUT = "TIMEOUT";
    public static final String TASK_FAIL_CAUSE_RUNTIME_ERROR = "RUNTIME_ERROR";
    public static final String TASK_FAIL_CAUSE_RUNTIME_NEED_CAPTCHA_VERIFICATION = "RUNTIME_NEED_CAPTCHA_VERIFICATION";

    private String failPhase;
    private String cause;
    private String information;

    public void setFailPhase(String failPhase) {
        this.failPhase = failPhase;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
