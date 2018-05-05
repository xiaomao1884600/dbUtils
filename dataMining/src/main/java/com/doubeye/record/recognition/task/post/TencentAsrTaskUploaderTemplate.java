package com.doubeye.record.recognition.task.post;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.cloud.service.provider.tencent.asr.AsrHelper;
import com.doubeye.commons.utils.cloud.service.provider.tencent.project.Project;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.core.opration.template.OperationTemplate;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

/**
 * @author doubeye
 * 腾讯ASR识别任务上传端
 */
@SuppressWarnings("unused | WeakerAccess")
public class TencentAsrTaskUploaderTemplate {
    private OperationTemplate template = new OperationTemplate();
    private Connection recordAnalyzeConnection;
    private AsrHelper asrHelper = new AsrHelper();

    public void init() {
        //TODO 这些信息需要传参
        Project project = new Project(1255612177, 1091843);
        Authorization authorization = new Authorization("AKIDeC0fDeGzxkOvJZNY7ZqvoVBj9ORNDqPp", "NabQ6qP4Xkv0xndHm7JKDwgcNxPxNbdU");
        asrHelper.setAuthorization(authorization);
        asrHelper.setProject(project);
        String startTime = DateTimeUtils.getDefaultFormattedDateTime(
                DateTimeUtils.timeDiff(new Date(), Calendar.HOUR_OF_DAY, -1));
        JSONObject generatorParameters = getGeneratorParameters(startTime);
        Operation taskGenerator = AsrTaskGenerator.getInstance(recordAnalyzeConnection, generatorParameters);
        template.addOperation(taskGenerator);
        Operation taskPoster = TencentAsrTaskPoster.getInstance(asrHelper, getTaskUploaderParameters());
        template.addOperation(taskPoster);
        Operation taskSaver = TencentAsrTaskSaver.getInstance(recordAnalyzeConnection);
        template.addOperation(taskSaver);
    }

    private static JSONObject getGeneratorParameters(String startTime) {
        JSONObject parameters = new JSONObject();
        parameters.put(PropertyNameConstants.PROPERTY_NAME.START_TIME.toString(), startTime);
        parameters.put(PropertyNameConstants.PROPERTY_NAME.MIN_CALL_LENGTH.toString(), 120);
        parameters.put(PropertyNameConstants.PROPERTY_NAME.MAX_CALL_LENGTH.toString(), 1200);
        parameters.put(PropertyNameConstants.PROPERTY_NAME.RECORD_COUNT.toString(), 30);
        return parameters;
    }

    private JSONObject getTaskUploaderParameters() {
        JSONObject parameters = new JSONObject();
        parameters.put(PropertyNameConstants.PROPERTY_NAME.OSS_PATH_PREFIX.toString(), OSS_PATH_PREFIX);
        return parameters;
    }

    public void run() throws Exception {
        template.run();
    }

    public Connection getRecordAnalyzeConnection() {
        return recordAnalyzeConnection;
    }

    public void setRecordAnalyzeConnection(Connection recordAnalyzeConnection) {
        this.recordAnalyzeConnection = recordAnalyzeConnection;
    }

    private static final String OSS_PATH_PREFIX = "http://hxsd-backup.oss-cn-beijing.aliyuncs.com/";

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection recordAnalyzeConnection = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            TencentAsrTaskUploaderTemplate uploader = new TencentAsrTaskUploaderTemplate();
            uploader.setRecordAnalyzeConnection(recordAnalyzeConnection);
            uploader.init();
            uploader.run();
        }
    }
}
