package com.doubeye.commons.utils.cloud.service.provider.tencent.asr;

import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.cloud.service.provider.tencent.constant.Constant;
import com.doubeye.commons.utils.cloud.service.provider.tencent.project.Project;
import com.doubeye.commons.utils.cloud.service.provider.tencent.restful.RestfulApiHelper;
import com.doubeye.commons.utils.cloud.service.provider.tencent.restful.TimeParameters;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.net.UrlContentGetter;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 录音离线识别助手
 *
 * @author doubeye
 */
public class AsrHelper {
    private static final String ASR_HOST = "aai.qcloud.com";
    private static final String POST_URL = "aai.qcloud.com/asr/v1/";
    //private static final String CALL_BACK_URL = "http://39.107.204.253/api/call_back";
    private static final String CALL_BACK_TOMCAT_URL = "http://39.107.204.253:8080/callback/callback";
    /**
     * 身份认证对象
     */
    private Authorization authorization;
    /**
     * 语音在公网的路径
     */
    private String path;
    /**
     * 项目对象
     */
    private Project project;

    private static Map<String, String> getPostParameters(Project project, Authorization authorization, String path, String callBackUrl, TimeParameters timeParameters) {
        Map<String, String> parameters = new TreeMap<>();
        parameters.put(Constant.PROPERTY_NAME.PROJECT_ID.toString(), project.getProjectId() + "");
        parameters.put(Constant.PROPERTY_NAME.SUB_SERVICE_TYPE.toString(), Constant.SUB_SERVICE_TYPE.OFFLINE.toString());
        parameters.put(Constant.PROPERTY_NAME.ENGINE_MODEL_TYPE.toString(), Constant.ENGINE_MODEL_TYPE.TYPE_8K_6.toString());
        parameters.put(Constant.PROPERTY_NAME.URL.toString(), path);
        parameters.put(Constant.PROPERTY_NAME.RESULT_TEXT_FORMAT.toString(), Constant.RESULT_TEXT_FORMAT.UTF_8.toString());
        parameters.put(Constant.PROPERTY_NAME.RESULT_TYPE.toString(), Constant.RESULT_TYPE.ASYNCHRONOUS.toString());
        parameters.put(Constant.PROPERTY_NAME.CALLBACK_URL.toString(), callBackUrl);
        parameters.put(Constant.PROPERTY_NAME.SOURCE_TYPE.toString(), Constant.SOURCE_TYPE.THROUGH_URL.toString());
        parameters.put(Constant.PROPERTY_NAME.SECRET_ID.toString(), authorization.getAccessKeyId());
        parameters.put(Constant.PROPERTY_NAME.TIMESTAMP.toString(), (timeParameters.getTimeStamp() + ""));
        parameters.put(Constant.PROPERTY_NAME.EXPIRED.toString(), (timeParameters.getExpired() + ""));
        parameters.put(Constant.PROPERTY_NAME.NONCE.toString(), (timeParameters.getNonce() + ""));
        parameters.put(Constant.PROPERTY_NAME.CHANNEL_NEMBER.toString(), "1");

        return parameters;
    }

    public static void main(String[] args) throws IOException {
        //腾讯云
        //String filePath = "http://asr-bucket-1255612177.cossh.myqcloud.com/20171008090459-Outbound-E1Trunk1-3215-018088630272.wav?sign=bnt9/I8FxrPjP7v3yBiLmEOezZFhPTEyNTU2MTIxNzcmaz1BS0lEOEZCMzFLQWQ2T1VRS0ttdlNzQUdFREI0b09telJ2Zk0mZT0xNTE3MTAxNDgxJnQ9MTUxNDUwOTQ4MSZyPTkzODIwODEyNiZmPS8yMDE3MTAwODA5MDQ1OS1PdXRib3VuZC1FMVRydW5rMS0zMjE1LTAxODA4ODYzMDI3Mi53YXYmYj1hc3ItYnVja2V0";
        //阿里云
        String filePath = "http://hxsd-backup.oss-cn-beijing.aliyuncs.com/recordAnalyze/2018/04/09/bei_jing/20180409182924-Outbound-E1Trunk1-3276-015038655347.wav";
        // System.out.println(URLDecoder.decode(filePath, "utf-8"));
        AsrHelper helper = new AsrHelper();
        Project project = new Project(1255612177, 1091843);
        Authorization authorization = new Authorization("AKIDeC0fDeGzxkOvJZNY7ZqvoVBj9ORNDqPp", "NabQ6qP4Xkv0xndHm7JKDwgcNxPxNbdU");
        helper.setAuthorization(authorization);
        helper.setProject(project);
        helper.setPath(URLDecoder.decode(filePath, "utf-8"));
        System.out.println(helper.postFile().toString());
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public JSONObject postFile() throws IOException {
        Map<String, String> parameters = getPostParameters(project, authorization, path, CALL_BACK_TOMCAT_URL, new TimeParameters());
        String url = RestfulApiHelper.getFullUrl(POST_URL, (project.getAppId() + ""), parameters);
        String signature = RestfulApiHelper.getSignature(url, (project.getAppId() + ""), authorization, CommonConstant.HTTP_METHOD.POST, parameters);
        // System.out.println("signature           "+ signature);
        Map<String, String> header = new HashMap<>(20);
        header.put(CommonConstant.HEADER_PROPERTY_NAME.CONTENT_TYPE.toString(), CommonConstant.CONTENT_TYPE.APPLICATION_OCTET_STREAM.toString());
        header.put(CommonConstant.HEADER_PROPERTY_NAME.AUTHORIZATION.toString(), signature);
        header.put(CommonConstant.HEADER_PROPERTY_NAME.HOST.toString(), ASR_HOST);
        // System.out.println(url);
        return JSONObject.fromObject(UrlContentGetter.doPost("http://" + url, header));
    }
}
