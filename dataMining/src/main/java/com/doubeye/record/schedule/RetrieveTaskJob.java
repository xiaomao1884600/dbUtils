package com.doubeye.record.schedule;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.elasticsearch.DocumentHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.record.recognition.task.retrieve.TencentAsrResultRetrieverTemplate;
import org.elasticsearch.client.transport.TransportClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;

/**
 * @author doubeye
 * 腾讯云识别结果获取任务
 */
public class RetrieveTaskJob implements Job{
    private TencentAsrResultRetrieverTemplate template = new TencentAsrResultRetrieverTemplate();

    public RetrieveTaskJob() throws Exception {
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey);
        TransportClient client = TencentAsrResultRetrieverTemplate.getClient();
        DocumentHelper documentHelper = TencentAsrResultRetrieverTemplate.getDocumentHelper(client);

        template.setRecordAnalyzeConnection(recordAnalyzeConnection);
        template.setDocumentHelper(documentHelper);
        template.init();
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            template.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
