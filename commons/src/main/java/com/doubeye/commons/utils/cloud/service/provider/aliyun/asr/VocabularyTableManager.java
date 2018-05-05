package com.doubeye.commons.utils.cloud.service.provider.aliyun.asr;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.cloud.service.provider.aliyun.restful.RestfulApiHelper;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 阿里云ASR热词管理
 */
public class VocabularyTableManager {
    /**
     * 数据库连接
     */
    private Connection conn;
    /**
     * 阿里云身份认证
     */
    private Authorization authorization;

    /**
     * 新增一个热词表
     * @param vocabularyTable 热词表
     * @throws IOException IO异常
     * @throws SQLException SQL异常
     */
    public void addVocabulary(VocabularyTable vocabularyTable) throws IOException, SQLException {
        String result = RestfulApiHelper.doPost(VOCABULARY_URL, vocabularyTable.toContentString(), authorization);
        JSONObject requestResult = JSONObject.fromObject(result);
        String vocabularyId = requestResult.getString(PROPERTY_VOCABULARY_ID);
        vocabularyTable.setId(vocabularyId);
        SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_VOCABULARY, JSONObject.fromObject(vocabularyTable)));
    }

    public JSONArray getAllVocabulariesFromAliyun() throws IOException {
        String result = RestfulApiHelper.doGet(VOCABULARY_URL, authorization);
        JSONObject resultObject = JSONObject.fromObject(result);
        return resultObject.getJSONArray("vocabs");
    }


    /**
     * 获得所有的热词表
     * @return 热词表，仅包含id和名称
     * @throws SQLException SQL异常
     */
    public JSONArray getAllVocabularies() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL_VOCABULARY);
    }

    /**
     * 根据热词表id获得热词表内容
     * @param vocabularyId 热词表id
     * @return 热词表的内容
     */
    public VocabularyTable getVocabularyTableById(String vocabularyId) throws IOException {
        VocabularyTable vocabularyTable = new VocabularyTable();
        String requestResult = RestfulApiHelper.doGet(String.format(VOCABULARY_URL_BY_ID_TEMPLATE, vocabularyId), authorization);
        vocabularyTable.fromString(requestResult);
        return vocabularyTable;
    }

    public JSONObject getVocabularyJSONFormatById(String vocabularyId) throws IOException {
        String requestResult = RestfulApiHelper.doGet(String.format(VOCABULARY_URL_BY_ID_TEMPLATE, vocabularyId), authorization);
        JSONObject vocabularyTableObject = JSONObject.fromObject(requestResult);
        if (vocabularyTableObject.containsKey(VocabularyTable.PROPERTY_WORD_WEIGHTS)) {
            vocabularyTableObject.put(VocabularyTable.PROPERTY_WORD_WEIGHTS, JSONUtils.objectToArray(vocabularyTableObject.getJSONObject(VocabularyTable.PROPERTY_WORD_WEIGHTS), "id", "weight"));
        }
        return vocabularyTableObject;
    }

    public void deleteVocabularyTableById(String vocabularyId) throws IOException, SQLException {
        String requestResult = RestfulApiHelper.doDelete(String.format(VOCABULARY_URL_BY_ID_TEMPLATE, vocabularyId), authorization);
        System.out.println(requestResult);
        SQLExecutor.execute(conn, String.format(SQL_DELETE_VOCABULARY_BY_ID, vocabularyId));
    }

    public void updateVocabularyTable(String vocabularyId, VocabularyTable vocabularyTable) throws IOException, SQLException {
        RestfulApiHelper.doPut(String.format(VOCABULARY_URL_BY_ID_TEMPLATE, vocabularyId), vocabularyTable.toContentString(), authorization);
        SQLExecutor.execute(conn, String.format(SQL_UPDATE_VOCABULARY_BY_ID, vocabularyTable.getName(), vocabularyId));
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    private static final String VOCABULARY_URL = "https://nlsapi.aliyun.com/asr/custom/vocabs";
    private static final String VOCABULARY_URL_BY_ID_TEMPLATE = "https://nlsapi.aliyun.com/asr/custom/vocabs/%s";
    private static final String PROPERTY_VOCABULARY_ID = "vocabulary_id";
    private static final String SQL_INSERT_VOCABULARY = "INSERT INTO asr_hot_vocabulary(id, name) VALUES ('([{id}])', '([{name}])')";
    private static final String SQL_DELETE_VOCABULARY_BY_ID = "DELETE FROM asr_hot_vocabulary WHERE id = '%s'";
    private static final String SQL_SELECT_ALL_VOCABULARY = "SELECT id, name FROM asr_hot_vocabulary";
    private static final String SQL_UPDATE_VOCABULARY_BY_ID = "UPDATE asr_hot_vocabulary set name = '%s' WHERE id = '%s'";

    public static void addVocabularyTableInFile(Authorization authorization, Connection conn, String fileName, String vocabularyTableName) throws IOException, SQLException {
        List<String> words = CollectionUtils.loadFromFile(fileName);
        VocabularyTable vocabularyTable = new VocabularyTable();
        vocabularyTable.setGlobalWeight(2);
        vocabularyTable.setName(vocabularyTableName);
        for (String word : words) {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setWord(word);
            vocabularyTable.addVocabulary(vocabulary);
        }

        VocabularyTableManager manager = new VocabularyTableManager();
        manager.setConn(conn);
        manager.setAuthorization(authorization);

        manager.addVocabulary(vocabularyTable);
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection connection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {

            VocabularyTable table = new VocabularyTable();
            table.setName("测试");
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setWord("新增2");
            vocabulary.setWeight(1);
            table.addVocabulary(vocabulary);
            vocabulary = new Vocabulary();
            vocabulary.setWord("新增1");
            table.addVocabulary(vocabulary);
            table.setGlobalWeight(3);



            Authorization authorization = new Authorization();
            authorization.setAccessKeyId("LTAIknRDVvtaSLBM");
            authorization.setAccessKeySecret("6X9cZbHkjZtxFtPzwylIRajIMSdd6h");

            //addVocabularyTableInFile(authorization, connection, "d:/hotWord.txt", "录音矫正");

            /*

            VocabularyTableManager manager = new VocabularyTableManager();
            manager.setAuthorization(authorization);
            manager.setConn(connection);

            manager.getAllVocabulariesFromAliyun();

            manager.addVocabulary(table);
            //System.out.println(manager.getVocabularyTableById("04062950d5f44d95852ecca83bb7b9a0"));

            vocabulary = new Vocabulary();
            vocabulary.setWord("橘子");
            table.addVocabulary(vocabulary);

            //manager.updateVocabularyTable("04062950d5f44d95852ecca83bb7b9a0", table);
            System.out.println(manager.getVocabularyTableById("04062950d5f44d95852ecca83bb7b9a0"));

            manager.deleteVocabularyTableById("c51179deb96b4f1299c9f8a2d3f3079a");

            */
            VocabularyTableManager manager = new VocabularyTableManager();
            manager.setAuthorization(authorization);
            manager.setConn(connection);
            System.out.println(manager.getAllVocabulariesFromAliyun());
        }
    }
}
