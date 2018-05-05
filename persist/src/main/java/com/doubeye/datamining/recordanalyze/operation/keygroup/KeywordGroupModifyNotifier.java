package com.doubeye.datamining.recordanalyze.operation.keygroup;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.core.opration.template.AbstractOperation;
import com.doubeye.core.opration.template.Operation;
import com.doubeye.datamining.recordanalyze.service.KeywordGroupService;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

/**
 * @author doubeye
 * @version 1.0.0
 * 关键词组改动后对后台进行消息通知
 * 需要将所有的关键词组及内容，以及版本号进行推送
 */
public class KeywordGroupModifyNotifier extends AbstractOperation{

    private HttpPost generatePostObject() {
        HttpPost post = new HttpPost(NOTIFY_URL);
        JSONObject contentObject = new JSONObject();
        contentObject.put(PROPERTY_NAME_GROUPS, getSharedResult().get(SelectAllKeywordGroupOperation.PROPERTY_NAME_KEYWORD_GROUP));
        contentObject.put(PROPERTY_NAME_VERSION, getSharedResult().get(PROPERTY_NAME_VERSION));
        post.addHeader(HEADER_PROPERTY_NAME_CONTENT_TYPE,HEADER_PROPERTY_VALUE_CONTENT_TYPE);
        post.setHeader(HEADER_PROPERTY_NAME_ACCEPT, HEADER_PROPERTY_VALUE_ACCEPT);
        post.setEntity(new StringEntity(contentObject.toString(), Charset.forName(CHARSET_UTF8)));
        return post;
    }



    @Override
    public void run() throws Exception {
        HttpPost post = generatePostObject();
        String result = UrlContentGetter.doPost(post);
        try {
            JSONObject resultObject = JSONObject.fromObject(result);
            if (!PROPERTY_VALUE_TRUE.equals(resultObject.getString(PROPERTY_NAME_SUCCESS))) {
                throw new RuntimeException(String.format(TEMPLATE_ERROR_MESSAGE, resultObject.getString(PROPERTY_NAME_ERROR_CODE), resultObject.getString(PROPERTY_NAME_ERROR_MESSAGE)));
            }
        } catch (JSONException e) {
            throw new RuntimeException("record_app返回内容格式与期望不符，请检查record_add服务端是否正常");
        }
    }

    private static final String NOTIFY_URL = "http://record.hxsd.local/api/record/save_record_label";
    private static final String PROPERTY_NAME_GROUPS = "groups";
    private static final String PROPERTY_NAME_VERSION = "version";
    private static final String HEADER_PROPERTY_NAME_CONTENT_TYPE = "Content-type";
    private static final String HEADER_PROPERTY_VALUE_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String HEADER_PROPERTY_NAME_ACCEPT = "Accept";
    private static final String HEADER_PROPERTY_VALUE_ACCEPT = "application/json";
    private static final String CHARSET_UTF8 = "UTF-8";


    private static final String PROPERTY_NAME_SUCCESS = "success";
    private static final String PROPERTY_NAME_ERROR_CODE = "errorCode";
    private static final String PROPERTY_NAME_ERROR_MESSAGE = "errorMessage";
    private static final String PROPERTY_VALUE_TRUE = "true";
    private static final String TEMPLATE_ERROR_MESSAGE = "record_app返回错误，代码：%s，错误信息：%s";

    public static Operation getInstance() {
        return new KeywordGroupModifyNotifier();
    }

    public static void main(String[] args) throws Exception {
        JSONObject.fromObject("fdsf");
        Operation operation = new KeywordGroupModifyNotifier();
        ApplicationContextInitiator.init();
        KeywordGroupService service = new KeywordGroupService();
        JSONObject result = service.getAllKeywordGroup(null);
        result.remove("SUCCESS");
        operation.setParameters(result);
        operation.run();
    }
}
