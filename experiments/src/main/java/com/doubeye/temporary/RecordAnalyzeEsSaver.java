package com.doubeye.temporary;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SingleNumberPagedExecutor;
import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.DocumentHelper;
import com.doubeye.commons.utils.elasticsearch.IndexHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.elasticsearch.client.transport.TransportClient;


import java.io.IOException;

import java.sql.Connection;

/**
 * @author doubeye
 * @version 1.0.0
 * 将录音文字分析的内容添加到ES中
 */
public class RecordAnalyzeEsSaver {

    private static final Logger logger = LogManager.getLogger(RecordAnalyzeEsSaver.class);

    //谢文豪
    private static final String CLUSTER_NAME = "hxsd-es";
    private static final String CLUSTER_ADDRESS = "10.2.20.59:9300";
    private static final String INDEX_NAME = "record_analyze";
    private static final String TYPE_NAME = "record";
    public static void main(String[] args) throws Exception {
        logger.setLevel(Level.ERROR);
        ApplicationContextInitiator.init();
        String key = GlobalApplicationContext.getInstance().getStringParameter("secretDatasourceKey");
        ClientHelper helper = new ClientHelper();
        helper.setClusterName(CLUSTER_NAME);
        helper.addNode(CLUSTER_ADDRESS);
        helper.setClientTransportSniff(false);
        try (TransportClient client = helper.getClient();
             Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", key)) {
            IndexHelper indexHelper = new IndexHelper();
            indexHelper.setClient(client);
            SingleNumberPagedExecutor sqlExecutor = new SingleNumberPagedExecutor() {
                @Override
                public void afterRetrieveData(JSONArray result) throws Exception {
                    save(result, indexHelper);
                }
            };
            sqlExecutor.setConn(conn);
            sqlExecutor.setAutoIncrementColumnName("id");
            sqlExecutor.setRecordCountPerPage(1000);
            sqlExecutor.setSqlTemplate(SQL_SELECT_ALL_RECORD_INFO);
            sqlExecutor.run();
        }
    }




    private static void save(JSONArray recordInfo, IndexHelper helper) throws IOException {
        for (int i = 0; i < recordInfo.size(); i ++) {
            JSONObject info = recordInfo.getJSONObject(i);
            if (info.containsKey("oss_path")) {
                info.put("oss_path", "http://hxsd-backup.oss-cn-beijing.aliyuncs.com/" + info.getString("oss_path"));
            }
        }
        if (recordInfo.size() > 0 ) {
            System.out.println(recordInfo.getJSONObject(0).getString("id") + "  -  " +
                    recordInfo.getJSONObject(recordInfo.size() -1).getString("id"));
        }



        DocumentHelper documentHelper = new DocumentHelper();
        documentHelper.setIndexHelper(helper);
        documentHelper.setIndexName(INDEX_NAME);
        documentHelper.setTypeName(TYPE_NAME);
        documentHelper.addDocuments(recordInfo);

    }


    private static final String SQL_SELECT_ALL_RECORD_INFO = "SELECT\n" +
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
            "WHERE record_info.id > :start AND record_info.id <= :end ORDER BY record_info.id";
}
