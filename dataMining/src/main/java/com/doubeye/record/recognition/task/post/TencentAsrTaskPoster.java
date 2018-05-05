package com.doubeye.record.recognition.task.post;

import com.doubeye.commons.utils.cloud.service.provider.tencent.asr.AsrHelper;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * @author doubeye
 * 任务提交器
 * 该操作将所有成功上传的任务保存在模板中的uploaded属性中，格式为JSONArray，内容包括requestId和recordId
 */
@SuppressWarnings("unused | WeakerAccess")
public class TencentAsrTaskPoster extends AbstractOperation{
    /**
     * 语音识别提交助手
     */
    private AsrHelper asrHelper;
    /**
     * 阿里云oss路径前缀
     */
    private String ossPathPrefix;

    public AsrHelper getAsrHelper() {
        return asrHelper;
    }

    public void setAsrHelper(AsrHelper asrHelper) {
        this.asrHelper = asrHelper;
    }

    public String getOssPathPrefix() {
        return ossPathPrefix;
    }

    public void setOssPathPrefix(String ossPathPrefix) {
        this.ossPathPrefix = ossPathPrefix;
    }

    @Override
    public void run() throws Exception {
        JSONArray records = getSharedResult().getJSONArray(PropertyNameConstants.PROPERTY_NAME.RECORD.toString());
        JSONArray successRequest = new JSONArray();
        for (int i = 0; i < records.size(); i ++) {
            JSONObject record = records.getJSONObject(i);
            String path = ossPathPrefix +
                    URLDecoder.decode(record.getString(PropertyNameConstants.PROPERTY_NAME.OSS_PATH.toString()),
                            CommonConstant.CHARSETS.UTF_8.toString());
            asrHelper.setPath(path);
            try {

                JSONObject result = asrHelper.postFile();
                System.out.println(result.toString());
                if (result.getInt(PropertyNameConstants.PROPERTY_NAME.CODE.toString()) == RESULT_CODE_SUCCESS) {
                    JSONObject successRecord = new JSONObject();
                    successRecord.put(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString(),
                            record.get(PropertyNameConstants.PROPERTY_NAME.RECORD_ID.toString()));
                    successRecord.put(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID,
                            result.getString(PropertyNameConstants.PROPERTY_NAME.REQUEST_ID.toString()));
                    successRequest.add(successRecord);
                } else {
                    //todo log tencent asr request error
                    System.out.println(result.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                //todo log Exception and retry
            }
        }
        getSharedResult().put(PropertyNameConstants.PROPERTY_NAME.UPLOADED, successRequest);
    }

    /**
     * 获得腾讯语音识别提交器的实例
     * @param asrHelper 语音识别助手
     * @param parameters 参数，包括一下属性
     *   ossPathPrefix {String} 阿里云oss路径前缀
     * @return TencentAsrTaskPoster 实例
     */
    public static Operation getInstance(AsrHelper asrHelper, JSONObject parameters) {
        TencentAsrTaskPoster instance = new TencentAsrTaskPoster();
        instance.setAsrHelper(asrHelper);
        RefactorUtils.fillByJSON(instance, parameters);
        return instance;
    }


    private static final int RESULT_CODE_SUCCESS = 0;
}
