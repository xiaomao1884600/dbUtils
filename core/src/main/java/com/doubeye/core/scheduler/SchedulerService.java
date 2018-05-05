package com.doubeye.core.scheduler;

import com.doubeye.commons.application.ApplicationCache;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.ProgressedRunnable;
import com.doubeye.commons.utils.json.mapper.JSONMapperHelper;
import com.doubeye.commons.utils.json.mapper.NameMapConfig;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.core.scheduler.bean.ScheduleBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.xml.soap.SOAPHeaderElement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
/**
 * @author 计划任务服务
 */
@SuppressWarnings("unused")
public class SchedulerService {

    public JSONArray getRunningTask(Map<String, String[]> args) {
        Map<String, ProgressedRunnable> runningTasks = ApplicationCache.getInstance().getRunningTasks();
        JSONArray result = new JSONArray();
        runningTasks.forEach((key, value) -> {
            JSONObject task = new JSONObject();
            task.put("identifier", value.getClass().getName());
            task.put("name", key);
            task.put("process", value.getProgress());
            task.put("errorCount", value.getErrorCount());
            task.put("costs", value.getTotalRunCost());
            task.put("information", value.getProgressDescription());
            result.add(task);
        });
        return result;
    }

    public JSONArray getAllSchedules(Map<String, String[]> args) throws SQLException {
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            NameMapConfig config = JSONMapperHelper.getFromClass(ScheduleBean.class);
            return JSONMapperHelper.doNameMapper(ResultSetJSONWrapper.getJSONArrayFromSQL(coreConnection, SQL_SELECT_ALL_SCHEDULES), config);
        }
    }

    public JSONObject saveSchedule(Map<String, String[]> params) throws SQLException {
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            JSONObject schedule = RequestHelper.getJSONObject(params, "schedule");
            //ScheduleBean bean = new ScheduleBean();
            //RefactorUtils.fillByJSON(bean, schedule);
            int scheduleId;
            if (schedule.containsKey("id") && !StringUtils.isEmpty(schedule.getString("id"))) {
                //TODO UPDATE
                scheduleId = schedule.getInt("id");
            } else {
                //TODO insert
                String sql = com.doubeye.commons.utils.string.StringUtils.format(SQL_INSERT_SCHEDULE, schedule);
                System.out.println(sql);
                coreConnection.setAutoCommit(false);
                try {
                    SQLExecutor.execute(coreConnection, sql);
                    scheduleId = SQLExecutor.getLastInsertId(coreConnection);
                    coreConnection.commit();
                } catch (Exception e) {
                    coreConnection.rollback();
                    throw e;
                }
            }
            JSONObject result = ResponseHelper.getSuccessObject();
            result.put("id", scheduleId);
            return result;
        }
    }

    private static final String SQL_SELECT_ALL_SCHEDULES = "select * from core_schedule";
    private static final String SQL_INSERT_SCHEDULE = "INSERT INTO core_schedule (name, identifier, schedule_type, config, run_config, singleton_type) VALUES" +
            "('([{name}])' , '([{identifier}])' , '([{scheduleType}])' , '([{config}])' , '([{runConfig}])' , '([{singletonType}])' )";

    public static void main(String[] args) {
        JSONObject schedule = JSONObject.fromObject("{\"initConfig\":{},\"name\":\"l;\",\"identifier\":\"k\",\"scheduleType\":\"ji\"}");
        System.out.println(com.doubeye.commons.utils.string.StringUtils.format(SQL_INSERT_SCHEDULE, schedule));
    }
}
