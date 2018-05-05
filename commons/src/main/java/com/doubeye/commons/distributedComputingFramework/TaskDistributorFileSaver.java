package com.doubeye.commons.distributedComputingFramework;


import com.doubeye.commons.utils.file.FileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class TaskDistributorFileSaver implements TaskDistributorStatusSaver {
    private String filename;

    @Override
    public void saveDistributorStatus(TaskDistributor distributor) throws IOException {
        JSONObject status = new JSONObject();
        status.put("name", distributor.getName());
        status.put("taskCommitterClassName", distributor.getTaskCommitter().getClass().getName());

        JSONArray toBeExecutedTasks = JSONArray.fromObject(distributor.getToBeExecuting());
        toBeExecutedTasks.addAll(JSONArray.fromObject(distributor.getExecuting()));
        status.put("toBeExecutedTasks", toBeExecutedTasks);

        status.put("success", JSONArray.fromObject(distributor.getSuccess()));
        status.put("failed", JSONArray.fromObject(distributor.getFailed()));

        status.put("runningCost", distributor.getRunningCost());

        FileUtils.toFile(filename, status);
    }

    /**
     * 设置参数
     * @param configuration 参数对象，参数中需包含如下内容
     *                      filename 保存的文件名
     */
    @Override
    public void setConfiguration(JSONObject configuration) {
        if (configuration.containsKey("filename") && StringUtils.isNotEmpty(configuration.getString("filename"))) {
            filename = configuration.getString("filename");
        } else {
            throw new RuntimeException("设置参数失败，必须指定filename属性");
        }
    }
}
