package com.doubeye.commons.utils.cloud.service.provider.tencent.restful;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author doubeye
 * 腾讯云restful接口工具
 */
public class RestfulApiHelper {
    public static String getSignature(String url, String appId, Authorization authorization, CommonConstant.HTTP_METHOD method,  Map<String, String> parameters) throws UnsupportedEncodingException {
        String toBeSigned = method + url;//getFullUrl(url, appId, parameters);
        // System.out.println("toBeSigned          "  + toBeSigned);
        return
                com.doubeye.commons.utils.string.StringUtils.toHmacSha1(toBeSigned, authorization.getAccessKeySecret());
    }

    /**
     * 获得提交的url
     * @param url 功能的url
     * @param appId 腾讯appId
     * @param parameters 提交参数
     * @return 完整的提交url
     */
    public static String getFullUrl(String url, String appId, Map<String , String> parameters) {
        String result = url + appId + "?";
        TreeMap<String, String> orderedMap;
        if (parameters instanceof TreeMap) {
            orderedMap = (TreeMap<String, String>) parameters;
        } else {
            orderedMap = new TreeMap<>(parameters);
        }
        StringBuilder builder = new StringBuilder();
        orderedMap.forEach((key, value) -> {
            builder.append(key).append("=").append(value).append("&");
        });
        result += StringUtils.substringBeforeLast(builder.toString(), "&");
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        long timestamp = System.currentTimeMillis();
        Map<String, String> parameters = new HashMap<>(20);
        Authorization authorization = new Authorization();
        //authorization.setAccessKeyId("AKIDeC0fDeGzxkOvJZNY7ZqvoVBj9ORNDqPp");
        authorization.setAccessKeyId("AKIDUfLUEUigQiXqm7CVSspKJnuaiIKtxqAv");
        //authorization.setAccessKeySecret("NabQ6qP4Xkv0xndHm7JKDwgcNxPxNbdU");
        authorization.setAccessKeySecret("bLcPnl88WU30VY57ipRhSePfPdOfSruK");
        //String appId = "1255612177";
        String appId = "2000001";
        String url = "aai.qcloud.com/asr/v1/";
        //parameters.put("projectid", "1091843");
        parameters.put("projectid", "0");
        parameters.put("sub_service_type", "0");
        parameters.put("engine_model_type", "1");
        parameters.put("url", "http://test.qq.com/voice_url");
        parameters.put("res_text_format", "0");
        parameters.put("res_type", "1");
        parameters.put("callback_url", "http://test.qq.com/rec_callback");
        parameters.put("source_type", "0");
        parameters.put("secretid", authorization.getAccessKeyId());
        parameters.put("timestamp", "1473752207");
        parameters.put("expired", "1473752807");
        parameters.put("nonce", "44925");
        //parameters.put("channel_num", "1");


        String signed = getSignature(url, appId, authorization, CommonConstant.HTTP_METHOD.POST, parameters);
        System.out.println(signed);


        String fromTencent = "POSTaai.qcloud.com/asr/v1/2000001?callback_url=http://test.qq.com/rec_callback&engine_model_type=1&expired=1473752807&nonce=44925&projectid=0" +
                "&res_text_format=0&res_type=1&secretid=AKIDUfLUEUigQiXqm7CVSspKJnuaiIKtxqAv&source_type=0&sub_service_type=0&timestamp=1473752207&url=http://test.qq.com/voice_url";
        System.out.println(fromTencent);
        System.out.println(com.doubeye.commons.utils.string.StringUtils.toHmacSha1(fromTencent, authorization.getAccessKeySecret()));

    }
}
