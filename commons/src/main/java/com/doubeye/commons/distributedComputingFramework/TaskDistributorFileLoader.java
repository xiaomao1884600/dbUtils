package com.doubeye.commons.distributedComputingFramework;

import com.doubeye.commons.utils.refactor.RefactorUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;
import java.util.Vector;

public class TaskDistributorFileLoader implements TaskLoader{
    private TaskDistributor taskDistributor;
    private Map<String, String> config;
    @Override
    public void load() {
        /*
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
         */
        taskDistributor.setName(config.get("name"));
        String taskCommitterClassName = config.get("taskCommitterClassName");
        taskDistributor.setTaskCommitter((TaskCommitter) RefactorUtils.getClassInstanceByName(taskCommitterClassName));

        JSONArray toBeExecutedTasks = JSONArray.fromObject(config.get("toBeExecuteTasks"));

        // taskDistributor.setToBeExecuting(CollectionUtils.getVectorFromJSONArray(toBeExecutedTasks));
    }

    @Override
    public void setTaskDistributor(TaskDistributor distributor) {
        this.taskDistributor = distributor;
    }

    @Override
    public void setLoadConfig(Map<String, String> config) {
        this.config = config;
    }

    /**
     * 将JSONArray转换为Vector<Task>
     * @param array JSONArray 对象
     * @return Vector对象
     */
    private static Vector<Task> getVectorFromJSONArray(JSONArray array) {
        Vector<Task> vector = new Vector<>();

        for (Object element : array) {
            JSONObject taskObject = JSONObject.fromObject(element);
            //Task task =
            //vector.add(element.toString());
            //TODO 需要定义具体的Task实体类  NOW
        }

        return vector;
    }
}
