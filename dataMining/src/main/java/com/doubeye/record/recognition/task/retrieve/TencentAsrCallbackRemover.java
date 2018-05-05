package com.doubeye.record.recognition.task.retrieve;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.record.recognition.task.constant.PropertyNameConstants;

import net.sf.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author doubeye
 * 将回调服务器中的识别结果删除，本类将读取模板共享存储中的savedRequestIds属性
 */
@SuppressWarnings("unused | WeakerAccess")
public class TencentAsrCallbackRemover extends AbstractOperation{
    /**
     * 接口地址
     */
    private String apiUrl;
    @Override
    public void run() throws Exception {
        JSONObject sharedResult = getSharedResult();
        List<String> savedIds = CollectionUtils.getListFromJSONArray(
                sharedResult.getJSONArray(PropertyNameConstants.PROPERTY_NAME.SAVED_REQUEST_IDS.toString()));
        String savedIdsString = CollectionUtils.split(savedIds,
                CommonConstant.SEPARATOR.COMMA.toString());
        System.out.println(UrlContentGetter.doPost(generatePostObject(savedIdsString)));
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    private HttpPost generatePostObject(String savedIds) {
        HttpPost post = new HttpPost(apiUrl);
        JSONObject contentObject = new JSONObject();
        contentObject.put(PropertyNameConstants.PROPERTY_NAME.SAVED_REQUEST_IDS, savedIds);
        post.setHeader(CommonConstant.HEADER_PROPERTY_NAME.CONTENT_TYPE.toString(),
                CommonConstant.HEADER_PROPERTY_VALUE.CONTENT_TYPE.toString());
        post.setEntity(new StringEntity(contentObject.toString(), Charset.forName(CommonConstant.CHARSETS.UTF_8.toString())));
        return post;
    }

    public static Operation getInstance(String apiUrl) {
        TencentAsrCallbackRemover instance = new TencentAsrCallbackRemover();
        instance.setApiUrl(apiUrl);
        return instance;
    }

    public static void main(String[] args) throws Exception {
        Operation remover = getInstance("http://39.107.204.253:8080/callback/remove");
        JSONObject sharedResult = remover.getSharedResult();
        sharedResult.put(PropertyNameConstants.PROPERTY_NAME.SAVED_REQUEST_IDS.toString(), "[98768393, 98768392]");
        remover.run();
    }
}
