package com.doubeye.commons.utils.elasticsearch;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SingleNumberPagedExecutor;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.client.transport.TransportClient;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 将数据库中的内容抽取到ES中
 */

@SuppressWarnings("unused | WeakerAccess")
public class JdbcPumper {
    private ClientHelper clientHelper;


    private List<RecordProcessor> processors = new ArrayList<>();


    private DocumentHelper documentHelper;
    /**
     * 用来执行分页导入的执行器
     */
    private SingleNumberPagedExecutor executor = new SingleNumberPagedExecutor() {
        @Override
        public void afterRetrieveData(JSONArray result) throws Exception {
            save(result);
        }
    };

    /**
     * 将结果集保存到ES
     * @param records 要保存的结果集
     * @throws IOException IO异常
     */
    private void save(JSONArray records) throws IOException {
        processors.forEach(processor -> processor.doProcess(records));
        documentHelper.addDocuments(records);
    }

    public ClientHelper getClientHelper() {
        return clientHelper;
    }

    public void setClientHelper(ClientHelper clientHelper) {
        this.clientHelper = clientHelper;
    }


    public void setConn(Connection conn) {
        executor.setConn(conn);
    }


    public void setSqlTemplate(String sqlTemplate) {
        executor.setSqlTemplate(sqlTemplate);
    }

    public void setDocumentHelper(DocumentHelper documentHelper) {
        this.documentHelper = documentHelper;
    }

    /**
     * 设置用来分页的idcolumnName
     * @param columnName 字段名
     */
    public void setIdColumnName(String columnName) {
        executor.setAutoIncrementColumnName(columnName);
    }

    /**
     * 设置数据库读取数据时每次的记录数
     * @param bunchSize 每次读取的记录数（注意，此数字为概数）
     */
    public void setBunchSize(int bunchSize) {
        executor.setRecordCountPerPage(bunchSize);
    }

    /**
     * 设置数据表中id的最大值
     * @param maxId id最大值
     */
    public void setMaxId(int maxId) {
        executor.setStartMustAfter(maxId);
    }

    /**
     * 添加结果处理器
     * @param recordProcessor 结果处理器
     */
    public void addRecordProcessor(RecordProcessor recordProcessor) {
        processors.add(recordProcessor);
    }

    /**
     * 执行抽取
     */
    public void pump() throws Exception {
        executor.run();
    }

    public static void main(String[] args) throws Exception {
        String sqlTemplate = "SELECT\n" +
                "\t`record_info`.`id`,\n" +
                "\t`record_info`.`userid`,\n" +
                "\t`record_info`.`username`,\n" +
                "\t`record_info`.`user_mobile`,\n" +
                "\t`record_info`.`studentid`,\n" +
                "\t`record_info`.`studentname`,\n" +
                "\t`record_info`.`student_mobile`,\n" +
                "\t`record_info`.`record_id`,\n" +
                "\t`record_info`.`campus_id`,\n" +
                "\t`record_info`.`calltype`,\n" +
                "\t`record_info`.`datetime`,\n" +
                "\t`record_info`.`billable` AS `duration`,\n" +
                "\t\"\" AS remark,\n" +
                "\t\"\" AS label,\n" +
                "\toss_path,\n" +
                "\t`analyze`.`user_channel_id`,\n" +
                "\t`analyze`.`asr_rate`,\n" +
                "\t`analyze`.`channel_rate`,\n" +
                "\t`analyze`.`analyze_info` \n" +
                "FROM\n" +
                "\t`record_info`\n" +
                "\tLEFT JOIN `record_analyze` AS `analyze` ON `record_info`.`record_id` = `analyze`.`record_id`\n" +
                "WHERE record_info.id > :start AND record_info.id <= :end AND record_info.id <= 100 ORDER BY record_info.id";
        JdbcPumper pumper = new JdbcPumper();
        ApplicationContextInitiator.init();
        String key = GlobalApplicationContext.getInstance().getStringParameter("secretDatasourceKey");
        ClientHelper clientHelper = new ClientHelper();
        clientHelper.setClusterName("hxsd-es");
        clientHelper.addNode("10.2.20.59:9300");
        try (TransportClient client = clientHelper.getClient();
             Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", key)) {
            IndexHelper indexHelper = new IndexHelper();
            indexHelper.setClient(client);
            DocumentHelper documentHelper = new DocumentHelper();
            documentHelper.setIndexHelper(indexHelper);
            documentHelper.setIndexName("record_test");
            documentHelper.setTypeName("analyze");
            pumper.setClientHelper(clientHelper);
            pumper.setDocumentHelper(documentHelper);
            pumper.setConn(conn);
            pumper.setBunchSize(1000);
            pumper.setIdColumnName("id");
            pumper.setSqlTemplate(sqlTemplate);
            pumper.addRecordProcessor(records -> {
                for (int i = 0; i < records.size(); i ++) {
                    JSONObject info = records.getJSONObject(i);
                    if (info.containsKey("oss_path")) {
                        info.put("oss_path", "http://hxsd-backup.oss-cn-beijing.aliyuncs.com/" + info.getString("oss_path"));
                    }


                    info.put("datetime", info.getString("datetime").replace(".0", ""));


                    JSONArray tagging = new JSONArray();
                    for (int j = 1; j <= 2; j ++) {
                        JSONObject tag = new JSONObject();
                        tag.put("remark", "备注" + i);
                        tag.put("label", i);
                        tag.put("userid", -1);
                        tag.put("username", "系统添加");
                        tagging.add(tag);
                    }
                    info.put("tagging", tagging);
                }
            });
            pumper.pump();
        }
    }
}
