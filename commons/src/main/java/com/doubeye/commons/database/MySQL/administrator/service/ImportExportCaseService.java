package com.doubeye.commons.database.MySQL.administrator.service;

import com.doubeye.commons.application.ApplicationCache;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.MySQL.administrator.importExportCase.ImportExportCaseEntity;
import com.doubeye.commons.database.MySQL.administrator.importExportCase.ImportExportCaseThreadRunner;
import com.doubeye.commons.database.MySQL.administrator.importExportCase.ImportExportRunner;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/4/27.
 * 导入导出案例服务
 */
@SuppressWarnings("unused")
public class ImportExportCaseService {
    /**
     * 获得所有方案
     * @param parameters 参数
     * @return 所有方案
     * @throws SQLException SQL异常
     */
    @SuppressWarnings("unused")
    public JSONArray getAllCases(Map<String, String[]> parameters) throws SQLException {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()){
            ImportExportCaseEntity caseEntity = new ImportExportCaseEntity();
            caseEntity.setConn(conn);
            return caseEntity.getAll();
        }
    }
    /**
     * 保存方案
     * @param parameters 参数，包含方案的JSON格式
     * @return 执行结果，并给定保存方案的id
     * @throws SQLException SQL异常
     */
    @SuppressWarnings("unused")
    public JSONObject saveCase(Map<String, String[]> parameters) throws Exception {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()){
            JSONObject caseObject = JSONObject.fromObject(RequestHelper.getString(parameters, "case"));
            ImportExportCaseEntity caseEntity = new ImportExportCaseEntity();
            RefactorUtils.fillByJSON(caseEntity, caseObject);
            caseEntity.setConn(conn);
            int caseId = caseEntity.getId();
            if (caseId == 0) {
                caseId = caseEntity.save();
            } else {
                caseEntity.update();
            }
            JSONObject result = ResponseHelper.getSuccessObject();
            result.put("id", caseId);
            return result;
        }
    }

    /**
     * 执行方案
     * @param parameters 参数包含以下内容
     *                   caseId 方案编号
     *                   onlyExport 仅做导出备份，默认为false
     * @return 返回重定向地址
     * @throws SQLException SQL异常
     * @throws IOException IO异常
     * @throws InterruptedException 中断异常
     */
    @SuppressWarnings("unused")
    public JSONArray runCase(Map<String, String[]> parameters) throws Exception {
        int caseId = RequestHelper.getInt(parameters, "caseId");
        boolean onlyExport = RequestHelper.getBoolean(parameters, "onlyExport", false);
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()){
            String tmpDirectory = System.getProperty("java.io.tmpdir");
            ImportExportRunner runner = new ImportExportRunner();
            runner.setConn(conn);
            runner.init(caseId, tmpDirectory);
            runner.setOnlyExport(onlyExport);
            ImportExportCaseThreadRunner threadRunner = new ImportExportCaseThreadRunner(runner);
            String uuid = UUID.randomUUID().toString();
            ApplicationCache.getInstance().addTask(uuid, threadRunner);
            //TODO what to do
            new Thread(threadRunner).start();
            JSONArray result = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("REDIRECT", uuid);
            result.add(obj);
            return result;

        }
    }

    /**
     * 删除案例
     * TODO unimplemented
     * @param parameters 包含以下参数
     *                   caseId 方案编号
     * @return 执行结果对象
     */
    @SuppressWarnings("unused")
    public JSONObject removeCase(Map<String, String[]> parameters) {
        return null;
    }
}
