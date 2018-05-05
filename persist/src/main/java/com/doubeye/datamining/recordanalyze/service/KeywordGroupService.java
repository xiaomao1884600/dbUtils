package com.doubeye.datamining.recordanalyze.service;


import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.core.opration.template.OperationTemplate;
import com.doubeye.core.systemProperties.VersionPropertyIncrementOperation;
import com.doubeye.core.systemProperties.VersionPropertySelectOperation;
import com.doubeye.datamining.recordanalyze.operation.keygroup.*;
import com.doubeye.datamining.recordanalyze.persist.KeywordGroupPersist;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author doubeye
 * @version 1.0.0
 * 关键词组服务
 */
@SuppressWarnings("unused")
public class KeywordGroupService {

    /**
     * 保存关键词，根据关键词是否包括id属性来确定插入还是更新
     * @param parameters 参数
     * @return 执行结果
     * @throws Exception 异常
     */
    public synchronized JSONObject saveKeywordGroup(Map<String, String[]> parameters) throws Exception {
        OperationTemplate template = new OperationTemplate();
        JSONObject keywordGroup = RequestHelper.getJSONObject(parameters, "keywordGroup");
        removeDuplication(keywordGroup);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");

        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
            Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            if (keywordGroup.containsKey(DeleteKeywordGroupOperation.PROPERTY_NAME_ID) && StringUtils.isNotEmpty(keywordGroup.getString(DeleteKeywordGroupOperation.PROPERTY_NAME_ID))){
                template.addOperation(UpdateKeywordGroupOperation.getInstance(recordAnalyzeConnection, keywordGroup));
            } else {
                template.addOperation(AddKeywordGroupOperation.getInstance(recordAnalyzeConnection, keywordGroup));
            }
            template.addOperation(VersionPropertyIncrementOperation.getInstance(coreConnection, VERSION_NAME_KEYWORD_GROUP));
            template.addOperation(VersionPropertySelectOperation.getInstance(coreConnection, VERSION_NAME_KEYWORD_GROUP));
            template.addOperation(SelectAllKeywordGroupOperation.getInstance(recordAnalyzeConnection));
            template.addOperation(KeywordGroupModifyNotifier.getInstance());
            template.run();
            return ResponseHelper.getSuccessObject();
        }
    }

    /**
     * 删除指定的关键词组
     * @param parameters 参数，需要包含id，用来指定关键词的id
     * @return 执行结果
     * @throws Exception 异常
     */
    public synchronized JSONObject removeKeywordGroup(Map<String, String[]> parameters) throws Exception {
        OperationTemplate template = new OperationTemplate();
        String keywordGroupId = RequestHelper.getString(parameters, DeleteKeywordGroupOperation.PROPERTY_NAME_ID);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
             Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            template.addOperation(DeleteKeywordGroupOperation.getInstance(recordAnalyzeConnection, keywordGroupId));
            template.addOperation(VersionPropertyIncrementOperation.getInstance(coreConnection, VERSION_NAME_KEYWORD_GROUP));
            template.addOperation(VersionPropertySelectOperation.getInstance(coreConnection, VERSION_NAME_KEYWORD_GROUP));
            template.addOperation(SelectAllKeywordGroupOperation.getInstance(recordAnalyzeConnection));
            template.addOperation(KeywordGroupModifyNotifier.getInstance());
            template.run();

            return ResponseHelper.getSuccessObject();
        }
    }

    /**
     * 获得所有的关键词组
     * @param parameters 参数
     * @return 所有的关键词组
     * @throws Exception 异常
     */
    public JSONObject getAllKeywordGroup(Map<String, String[]> parameters) throws Exception {

        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection();
             Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            JSONObject operationResult = getAllKeywordGroups(coreConnection, recordAnalyzeConnection);
            JSONObject versionObject = new JSONObject();
            versionObject.put(VersionPropertySelectOperation.PROPERTY_NAME_VERSION, operationResult.get(VersionPropertySelectOperation.PROPERTY_NAME_VERSION));
            return ResponseHelper.getSuccessObject(operationResult.getJSONArray(SelectAllKeywordGroupOperation.PROPERTY_NAME_KEYWORD_GROUP), versionObject);
        }
    }

    private JSONObject getAllKeywordGroups(Connection coreConnection, Connection recordAnalyzeConnection) throws Exception {
        OperationTemplate template = new OperationTemplate();
        template.setNeedTransaction(false);
        template.addOperation(SelectAllKeywordGroupOperation.getInstance(recordAnalyzeConnection));
        template.addOperation(VersionPropertySelectOperation.getInstance(coreConnection, VERSION_NAME_KEYWORD_GROUP));
        template.run();
        return template.getMergedResult();
    }

    public JSONArray getAllKeywordGroupNames(Map<String, String[]> parameters) throws Exception {

        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            KeywordGroupPersist persist = new KeywordGroupPersist();
            persist.setConn(recordAnalyzeConnection);
            return persist.getAllKeywordGroupNames();
        }
    }

    /**
     * 关键词组版本号标识符
     */
    private static final String VERSION_NAME_KEYWORD_GROUP = "VERSION_KEYWORD_GROUP";
    private static void removeDuplication(JSONObject keywordGroup) {
        if (keywordGroup.containsKey("keywords")) {
            Set<String> set = new TreeSet<>();
            JSONArray keywords = keywordGroup.getJSONArray("keywords");
            for (int i = 0; i < keywords.size() ; i ++) {
                set.add(keywords.getString(i));
            }
            keywordGroup.put("keywords", set);
        }
    }
}
