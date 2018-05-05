package com.doubeye.record.schedule.rl;

import com.aliyun.oss.OSSClient;
import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.cloud.service.provider.aliyun.oss.OssHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.record.recognition.task.file.RlToOSS;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;

/**
 * @author doubeye
 * 容联录音文件上传到阿里OSS任务
 */
@DisallowConcurrentExecution
public class FilePosterJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            RlToOSS rlToOSS = new RlToOSS();
            //OSSClient client = OssHelper.getOSSClient("oss-cn-beijing.aliyuncs.com", "LTAIknRDVvtaSLBM", "6X9cZbHkjZtxFtPzwylIRajIMSdd6h");
            OSSClient client = OssHelper.getOSSClient("oss-cn-beijing.aliyuncs.com", "uGS25hDijJnmwb5s", "K4xQySOab6weamr5fk8mMcqJCx5wwd");
            rlToOSS.setBucket("hxsd-backup");
            rlToOSS.setOssClient(client);
            ApplicationContextInitiator.init();
            String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
            try (Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey);) {
                rlToOSS.setConn(recordAnalyzeConnection);
                rlToOSS.doUpload();
            } finally {
                OssHelper.shutDownOSSClient(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
