package com.doubeye.record.recognition.task.file;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.cloud.service.provider.aliyun.oss.OssHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * 将容联云通讯的录音文件上传到阿里云中
 * 流程为
 * 1. 从record_tong_task_upload表中查找未上传的记录（upload_status = 0）
 * 2. 将源文件上传到阿里云的指定位置（monitor_path -> oss_path）
 * 3. 如果上传成功，
 *  a.上传成功(PutObjectResult.getClientCRC().equals(PutObjectResult.getServerCRC()))，将记录的上传状态改为成功（SET upload_status = 1）
 *  b.上传失败（PutObjectResult.getClientCRC().equals(PutObjectResult.getServerCRC())）将记录的上传状态改为失败（SET upload_status = 2）
 */
public class RlToOSS {
    private Connection conn;
    private OSSClient ossClient;
    private String bucket;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public OSSClient getOssClient() {
        return ossClient;
    }

    public void setOssClient(OSSClient ossClient) {
        this.ossClient = ossClient;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void doUpload() throws SQLException {
        int successCount = 0, errorCount = 0;
        long start = System.currentTimeMillis();
        JSONArray toBeUpload = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_FILES);
        for (int i = 0; i < toBeUpload.size(); i ++) {
            JSONObject entry = toBeUpload.getJSONObject(i);
            int id = entry.getInt("id");
            String rlUri = entry.getString("monitorpath");
            String ossKey = entry.getString("oss_path");
            try {
                PutObjectResult result = OssHelper.putUri(ossClient, bucket, ossKey, rlUri);
                int status;
                String message = "";
                if (result.getClientCRC().equals(result.getServerCRC())) {
                    status = UPLOAD_RESULT.SUCCESS.getStatus();
                } else {
                    status = UPLOAD_RESULT.FAIL.getStatus();
                    message = ERROR_MESSAGE_SAVE_STATUS;
                    errorCount ++;
                }
                SQLExecutor.execute(conn, String.format(SQL_UPLOAD_STATUS, status, message, id));
                successCount ++;
            } catch (Exception e) {
                SQLExecutor.execute(conn, String.format(SQL_UPLOAD_STATUS, UPLOAD_RESULT.FAIL.getStatus(), e.getMessage(), id));
                errorCount ++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format(MESSAGE_REPORT, toBeUpload.size(), successCount, errorCount, ((end - start) / 1000 / 60)));
    }

    private static final String SQL_SELECT_FILES = "SELECT id,monitorpath,oss_path,upload_status FROM record_tong_task_upload WHERE upload_status = 0 LIMIT 100";
    private static final String SQL_UPLOAD_STATUS = "UPDATE record_tong_task_upload SET upload_status = %d, message_option = '%s' WHERE id = %d";
    private static final String MESSAGE_REPORT = "本次共获得上传任务%d条，%d个文件成功，%d个文件失败，总共花费%s分钟";
    private static final String ERROR_MESSAGE_SAVE_STATUS = "上传阿里云过程出错，校验失败";

    /**
     * 上传状态
     */
    enum UPLOAD_RESULT {
        /**
         * 成功
         */
        SUCCESS(1),
        /**
         * 失败
         */
        FAIL(2),;
        private int status;

        UPLOAD_RESULT(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    public static void main(String[] args) throws Exception {
        //OSSClient client = OssHelper.getOSSClient("oss-cn-beijing.aliyuncs.com", "uGS25hDijJnmwb5s", "K4xQySOab6weamr5fk8mMcqJCx5wwd")
        OSSClient client = OssHelper.getOSSClient("oss-cn-beijing.aliyuncs.com", "LTAIknRDVvtaSLBM", "6X9cZbHkjZtxFtPzwylIRajIMSdd6h");
        RlToOSS rlToOSS = new RlToOSS();
        rlToOSS.setBucket("doubeye-test-conversation-analyze");
        rlToOSS.setOssClient(client);
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-DEV_WWJ", encryptKey);) {
            rlToOSS.setConn(recordAnalyzeConnection);
            rlToOSS.doUpload();
        } finally {
            OssHelper.shutDownOSSClient(client);
        }
    }
}
