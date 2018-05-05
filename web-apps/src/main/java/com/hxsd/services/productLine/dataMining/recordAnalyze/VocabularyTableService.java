package com.hxsd.services.productLine.dataMining.recordAnalyze;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.cloud.service.provider.aliyun.asr.VocabularyTable;
import com.doubeye.commons.utils.cloud.service.provider.aliyun.asr.VocabularyTableManager;


import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;


/**
 * @author doubeye
 * @version 1.0.0
 * 阿里云ASR热词表管理服务
 */
@SuppressWarnings("unused")
public class VocabularyTableService {
    /**
     * 获得所有的词汇表列表
     * @param parameters 参数
     * @return 词汇表列表数组
     * @throws Exception 异常
     */
    public static JSONArray getAllVocabularyTable(Map<String, String[]> parameters) throws Exception {
        String secretKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", secretKey)) {
            VocabularyTableManager manager = new VocabularyTableManager();
            manager.setConn(conn);
            return manager.getAllVocabularies();
        }
    }


    public static JSONArray getVocabularyTableById(Map<String, String[]> parameters) throws Exception {
        String vocabularyId = RequestHelper.getString(parameters, "vocabularyTableId");
        String secretKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", secretKey)) {
            Authorization authorization = GlobalApplicationContext.getInstance().getAliyunAuthorizationByIdentifier("doubeye");
            VocabularyTableManager manager = new VocabularyTableManager();
            manager.setConn(conn);
            manager.setAuthorization(authorization);
            try {
                JSONArray result = new JSONArray();
                JSONObject vocabularyTableObject = manager.getVocabularyJSONFormatById(vocabularyId);
                result.add(vocabularyTableObject);
                return result;
            } catch (IOException e) {
                if (e.getMessage().contains(VOCABULARY_NOT_EXISTS_SIGNATURE)) {
                    throw new RuntimeException("查找的热词表不存在:" + vocabularyId);
                } else {
                    throw e;
                }
            }
        }
    }

    public static JSONObject deleteVocabularyTableById(Map<String, String[]> parameters) throws Exception {
        String vocabularyId = RequestHelper.getString(parameters, "vocabularyTableId");
        String secretKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", secretKey)) {
            Authorization authorization = GlobalApplicationContext.getInstance().getAliyunAuthorizationByIdentifier("doubeye");
            VocabularyTableManager manager = new VocabularyTableManager();
            manager.setConn(conn);
            manager.setAuthorization(authorization);
            try {
                manager.deleteVocabularyTableById(vocabularyId);

                return ResponseHelper.getSuccessObject();
            } catch (IOException e) {
                if (e.getMessage().contains(VOCABULARY_NOT_EXISTS_SIGNATURE)) {
                    throw new RuntimeException("查找的热词表不存在:" + vocabularyId);
                } else {
                    throw e;
                }
            }
        }
    }


    public static JSONObject saveVocabularyTable(Map<String, String[]> parameters) throws Exception {
        JSONObject vocabularyTableObject = RequestHelper.getJSONObject(parameters, "vocabularyTable");
        String secretKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", secretKey)) {
            Authorization authorization = GlobalApplicationContext.getInstance().getAliyunAuthorizationByIdentifier("doubeye");


            VocabularyTableManager manager = new VocabularyTableManager();
            manager.setConn(conn);
            manager.setAuthorization(authorization);
            try {
                if (vocabularyTableObject.containsKey(VocabularyTable.PROPERTY_WORD_WEIGHTS)) {
                    vocabularyTableObject.put(VocabularyTable.PROPERTY_WORD_WEIGHTS,
                            JSONUtils.arrayToObject(vocabularyTableObject.getJSONArray(VocabularyTable.PROPERTY_WORD_WEIGHTS),
                                    "id", "weight"));
                }
                VocabularyTable vocabularyTable = new VocabularyTable();
                vocabularyTable.fromString(vocabularyTableObject.toString());
                if (org.apache.commons.lang3.StringUtils.isEmpty(vocabularyTable.getId())) {
                    manager.addVocabulary(vocabularyTable);
                } else {
                    manager.updateVocabularyTable(vocabularyTable.getId(), vocabularyTable);
                }
                JSONObject result = ResponseHelper.getSuccessObject();
                result.put("id", vocabularyTable.getId());
                return result;
            } catch (IOException e) {
                if (e.getMessage().contains(VOCABULARY_NOT_EXISTS_SIGNATURE)) {
                    throw new RuntimeException("更新的热词表不存在:" + vocabularyTableObject.get("id"));
                } else {
                    throw e;
                }
            }
        }
    }

    private static final String VOCABULARY_NOT_EXISTS_SIGNATURE = "code=404";
}
