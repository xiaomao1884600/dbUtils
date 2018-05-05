package com.doubeye.datamining.recordanalyze.service;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;

import com.doubeye.datamining.recordanalyze.AuthorizationSaver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.Map;


/**
 * @author doubeye
 * @version 1.0.0
 * 用来接收王武军传过的E系统的权限数据，并保存到数据库中的服务
 */
@SuppressWarnings("unused")
    public class AuthorizationService {
    public JSONObject saveUser(Map<String, String[]> parameters) throws Exception {
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        JSONArray allUser = RequestHelper.getJSONArray(parameters, "users", "utf-8");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-DEV", encryptKey)) {
            AuthorizationSaver.saveUser(conn, allUser);
        }
        return ResponseHelper.getSuccessObject();
    }

    public JSONObject saveUserLeader(Map<String, String[]> parameters) throws Exception {
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        JSONArray allUser = RequestHelper.getJSONArray(parameters, "userLeader", "utf-8");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-DEV", encryptKey)) {
            AuthorizationSaver.saveUserLeader(conn, allUser);
        }
        return ResponseHelper.getSuccessObject();
    }
}
