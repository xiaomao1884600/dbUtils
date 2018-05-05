package com.doubeye.commons.distributedComputingFramework;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTask implements Task{
    private int id;
    private List<TaskFailInformation> failure = new ArrayList<>();
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getFailCount() {
        return failure.size();
    }

    @Override
    public List<TaskFailInformation> getFailures() {
        return failure;
    }

    @Override
    public void addFailure(TaskFailInformation information) {
        failure.add(information);
    }
}
