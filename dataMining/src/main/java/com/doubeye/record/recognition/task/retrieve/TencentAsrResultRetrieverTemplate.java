package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;

import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.DocumentHelper;
import com.doubeye.commons.utils.elasticsearch.IndexHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.core.opration.template.OperationTemplate;
import net.sf.json.JSONObject;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;

import java.net.UnknownHostException;
import java.sql.Connection;

/**
 * @author doubeye
 * 腾讯云ASR识别结果获取模板
 */
public class TencentAsrResultRetrieverTemplate {
    private OperationTemplate template = new OperationTemplate();
    private Connection recordAnalyzeConnection;
    private DocumentHelper documentHelper;

    public void init() {
        Operation resultRetriever = TencentAsrResultRetriever.getInstance(URL_RESULT_RETRIEVE);
        template.addOperation(resultRetriever);
        Operation recordMatcher = TencentAsrRequestRecordMatcher.getInstance(recordAnalyzeConnection);
        template.addOperation(recordMatcher);
        Operation formatter = TencentAsrResultFormatter.getInstance();
        template.addOperation(formatter);
        Operation saver = TencentAsrResultSaver.getInstance(recordAnalyzeConnection);
        template.addOperation(saver);
        Operation esSave = TencentAsrResultEsSaver.getInstance(documentHelper);
        template.addOperation(esSave);
        Operation callbackRemover = TencentAsrCallbackRemover.getInstance(URL_RESULT_REMOVE);
        template.addOperation(callbackRemover);
    }




    public void run() throws Exception {
        template.run();
    }

    public Connection getRecordAnalyzeConnection() {
        return recordAnalyzeConnection;
    }

    public DocumentHelper getDocumentHelper() {
        return documentHelper;
    }

    public void setDocumentHelper(DocumentHelper documentHelper) {
        this.documentHelper = documentHelper;
    }

    public void setRecordAnalyzeConnection(Connection recordAnalyzeConnection) {
        this.recordAnalyzeConnection = recordAnalyzeConnection;
    }


    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey);
            TransportClient client = getClient()) {
            DocumentHelper documentHelper = getDocumentHelper(client);
            TencentAsrResultRetrieverTemplate template = new TencentAsrResultRetrieverTemplate();
            template.setRecordAnalyzeConnection(recordAnalyzeConnection);
            template.setDocumentHelper(documentHelper);
            template.init();
            template.run();
        }
    }

    public static DocumentHelper getDocumentHelper(TransportClient client) throws UnknownHostException {
        IndexHelper indexHelper = new IndexHelper();
        indexHelper.setClient(client);
        DocumentHelper documentHelper = new DocumentHelper();
        documentHelper.setTypeName("analyze");
        documentHelper.setIndexHelper(indexHelper);
        return documentHelper;
    }

    public static TransportClient getClient() throws UnknownHostException {
        ClientHelper clientHelper = new ClientHelper();
        clientHelper.setClusterName("hxsd-bd");
        clientHelper.addNode("10.2.24.57:9300");
        return clientHelper.getClient();
    }

    private static final String URL_RESULT_RETRIEVE = "http://39.107.204.253:8080/callback/result";
    private static final String URL_RESULT_REMOVE = "http://39.107.204.253:8080/callback/remove";
}
