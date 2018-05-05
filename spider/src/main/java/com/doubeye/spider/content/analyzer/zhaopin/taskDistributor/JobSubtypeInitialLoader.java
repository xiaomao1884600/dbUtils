package com.doubeye.spider.content.analyzer.zhaopin.taskDistributor;

import com.doubeye.commons.distributedComputingFramework.Task;
import com.doubeye.commons.distributedComputingFramework.TaskDistributor;
import com.doubeye.commons.distributedComputingFramework.TaskLoader;
import com.doubeye.spider.content.analyzer.zhaopin.ZhaopinUrlGenerator;


import java.util.List;
import java.util.Map;
import java.util.Vector;

public class JobSubtypeInitialLoader implements TaskLoader{
    private TaskDistributor distributor;
    @Override
    public void load() {
        List<String> urls = ZhaopinUrlGenerator.getInterestedJobSubtypeCityUrls(true);
        Vector<Task> toBeExecuted = new Vector<>();
        for (int i = 0; i < urls.size(); i ++) {
            Task task = new ZhaopinUrlTask();
            task.setId(i);
            task.setTaskContent(urls.get(i));
        }
        distributor.setToBeExecuting(toBeExecuted);
    }

    @Override
    public void setTaskDistributor(TaskDistributor distributor) {
        this.distributor = distributor;
    }

    @Override
    public void setLoadConfig(Map<String, String> config) {

    }
}
