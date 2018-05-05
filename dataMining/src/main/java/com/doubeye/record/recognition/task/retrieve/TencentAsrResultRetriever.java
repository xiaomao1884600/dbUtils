package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;
import net.sf.json.JSONObject;


/**
 * @author doubeye
 * 获得腾讯ASR识别结果，从服务端获得ASR识别结果，并将结果保存在模板的results属性中
 */
@SuppressWarnings("unused | WeakerAccess")
public class TencentAsrResultRetriever extends AbstractOperation{
    /**
     * 接口的url
     */
    private String apiUrl;
    @Override
    public void run() {
        String content = UrlContentGetter.getHtmlCode(apiUrl, CommonConstant.CHARSETS.UTF_8.toString());
        JSONObject result = JSONObject.fromObject(content);
        getSharedResult().put(PropertyNameConstants.PROPERTY_NAME.RESULTS.toString(),
                result.getJSONArray(PropertyNameConstants.PROPERTY_NAME.DATA.toString()));
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public static Operation getInstance(String apiUrl) {
        TencentAsrResultRetriever instance = new TencentAsrResultRetriever();
        instance.setApiUrl(apiUrl);
        return instance;
    }

    public static void main(String[] args) throws Exception {
        Operation operation = TencentAsrResultRetriever.getInstance("http://39.107.204.253:8080/callback/result");
        operation.run();
        System.out.println(operation.getSharedResult());
    }

}
