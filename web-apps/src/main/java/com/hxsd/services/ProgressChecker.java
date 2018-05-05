package com.hxsd.services;

import com.doubeye.commons.application.ApplicationCache;
import com.doubeye.commons.utils.ProgressedRunnable;
import com.doubeye.commons.utils.request.RequestHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/8/18.
 */
public class ProgressChecker {
    public JSONArray getProgress(Map<String, String[]> params) {
        String uuid = RequestHelper.getString(params, "uuid");
        JSONObject obj = new JSONObject();
        ProgressedRunnable runnable = ApplicationCache.getInstance().getTask(uuid);
        obj.put("progress", runnable.getProgress());
        obj.put("description", runnable.getProgressDescription());
        obj.put("totalRunCost", runnable.getTotalRunCost());
        JSONArray result = new JSONArray();
        result.add(obj);
        return result;
    }

    public JSONObject removeProgress(Map<String, String[]> params) {
        String uuid = RequestHelper.getString(params, "uuid");
        ProgressedRunnable runnable = ApplicationCache.getInstance().getTask(uuid);
        ApplicationCache.getInstance().removeTask(uuid);
        JSONObject obj = new JSONObject();
        obj.put("SUCCESS", true);
        return obj;
    }

    public JSONArray getProgressList(Map<String, String[]> params) {
        Set<String> allTaskKeys = ApplicationCache.getInstance().getAllTaskKeys();
        JSONObject obj = new JSONObject();
        obj.put("keys", allTaskKeys.toString());
        JSONArray result = new JSONArray();
        result.add(obj);
        return result;
    }
}
