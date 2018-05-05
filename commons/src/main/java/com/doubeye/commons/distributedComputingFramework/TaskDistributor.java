package com.doubeye.commons.distributedComputingFramework;

import com.doubeye.commons.utils.ProgressedRunnable;

import java.util.List;
import java.util.Vector;

/**
 * @author doubeye
 * @version 1.0,0
 * 任务分发器 TODO 对获得下一个任务，提交，和任务失败方法，需要验证是否需要锁机制
 * //TODO Vector 替换为java.util.concurrent
 */
public class TaskDistributor implements ProgressedRunnable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 分发器名称，当存在多个分发器时，可以根据名称来进行区别
     */
    private String name;
    /**
     * 任务提交器
     */
    private TaskCommitter taskCommitter;
    /**
     * 要被执行的任务
     */
    private Vector<Task> toBeExecuting;
    /**
     * 正在执行中的任务
     */
    private Vector<Task> executing;
    /**
     * 已失败的任务
     */
    private Vector<Task> failed;
    /**
     * 执行成功的任务
     */
    private Vector<Task> success;

    public synchronized Task getNext() {
        if (toBeExecuting.size() >0 ) {
            Task task = toBeExecuting.elementAt(0);
            toBeExecuting.remove(0);
            executing.add(task);
            return task;
        }
        return null;
    }

    public void setToBeExecuting(Vector<Task> toBeExecuting) {
        this.toBeExecuting = toBeExecuting;
    }

    public void setSuccess(Vector<Task> success) {
        this.success = success;
    }


    public void commit(int id, String content) {
        Task task = findTask(executing, id);
        try {
            taskCommitter.doCommit(task);
            executing.remove(task);
            success.add(task);
        } catch (Exception e) {
            TaskFailInformation taskFailInformation = new TaskFailInformation();
            taskFailInformation.setCause(TaskFailInformation.TASK_FAIL_CAUSE_RUNTIME_ERROR);
            taskFailInformation.setFailPhase(TaskFailInformation.TASK_FAIL_PHASE_COMMIT);
            taskFailInformation.setInformation(e.getMessage());
            fail(task, taskFailInformation);
        }
    }

    public void fail(Task task, TaskFailInformation information) {
        executing.remove(task);
        List<TaskFailInformation> failures = task.getFailures();
        task.addFailure(information);
        if (failures.size() >= 3) {
            //记录失败日志
            //将任务放到失败列表
            failed.add(task);
        } else {
            toBeExecuting.add(task);
        }
    }

    /**
     * 所有任务的数量
     */
    private int allTaskCount;
    private long runningCost = 0;
    private long startAt = System.currentTimeMillis();
    @Override
    public float getProgress() {
        return ((float) (allTaskCount - success.size() - failed.size())) / allTaskCount;
    }

    @Override
    public String getProgressDescription() {
        return String.format("共有%d个任务，已经成功运行%d，正在运行%d，失败个数%d, 剩余%d，总进度%f，已花费%d毫秒", allTaskCount, success.size(), executing.size(), failed.size(), (allTaskCount - success.size() - failed.size()),getProgress(), getTotalRunCost());
    }

    @Override
    public long getTotalRunCost() {
        return runningCost + System.currentTimeMillis() - startAt;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public void run() {

    }

    private static Task findTask(List<Task> tasks, int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public void shutdown() {
        toBeExecuting.clear();
        executing.clear();
        failed.clear();
        success.clear();
    }

    public TaskCommitter getTaskCommitter() {
        return taskCommitter;
    }

    public void setTaskCommitter(TaskCommitter taskCommitter) {
        this.taskCommitter = taskCommitter;
    }

    public Vector<Task> getToBeExecuting() {
        return toBeExecuting;
    }

    public Vector<Task> getExecuting() {
        return executing;
    }

    public Vector<Task> getFailed() {
        return failed;
    }

    public Vector<Task> getSuccess() {
        return success;
    }

    public long getRunningCost() {
        return runningCost;
    }
}
