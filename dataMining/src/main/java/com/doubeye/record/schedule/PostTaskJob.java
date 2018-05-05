package com.doubeye.record.schedule;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.record.recognition.task.post.TencentAsrTaskUploaderTemplate;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;

/**
 * @author doubeye
 * 提交到腾讯云识别的任务
 */
public class PostTaskJob implements Job {

    private TencentAsrTaskUploaderTemplate template = new TencentAsrTaskUploaderTemplate();

    public  PostTaskJob() throws Exception {
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey);
        template.setRecordAnalyzeConnection(recordAnalyzeConnection);
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
