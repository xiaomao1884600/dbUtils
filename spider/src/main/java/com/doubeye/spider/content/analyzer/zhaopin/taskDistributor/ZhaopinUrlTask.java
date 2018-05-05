package com.doubeye.spider.content.analyzer.zhaopin.taskDistributor;

import com.doubeye.commons.distributedComputingFramework.AbstractTask;
import com.doubeye.commons.distributedComputingFramework.Task;
import com.doubeye.commons.distributedComputingFramework.TaskFailInformation;

import java.util.ArrayList;
import java.util.List;

public class ZhaopinUrlTask extends AbstractTask {

    private String taskContent;

    @Override
    public String getTaskContent() {
        return taskContent;
    }

    @Override
    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }
}
