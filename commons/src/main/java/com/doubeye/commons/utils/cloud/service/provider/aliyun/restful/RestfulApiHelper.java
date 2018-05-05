package com.doubeye.commons.utils.cloud.service.provider.aliyun.restful;

import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.cloud.service.common.authorization.Authorization;
import com.doubeye.commons.utils.constant.CommonConstant;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author doubeye
 * @version 1.0.0
 * 阿里云
 */
@SuppressWarnings("WeakerAccess")
public class RestfulApiHelper {
    /**
     * 对字符串进行md5哈希，并用Base64编码
     * @param plainText 要转换的字符串
     * @return 转换结果
     */
    public static String toMD5Base64(String plainText) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(plainText)) {
            return "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance(CommonConstant.CIPHERS.MD5.toString());
            md5.update(plainText.getBytes(CommonConstant.CHARSETS.UTF_8.toString()));
            byte[] resultBytes = md5.digest();
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(resultBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 要签名的内容，第一个参数为METHOD,第二个参数为body，第三个参数为GMT格式的date
     */
    private static final String SIGNING_CONTENT_TEMPLATE = "%s\n" + CommonConstant.HEADER_PROPERTY_VALUE.ACCEPT + "\n%s\n" +
            CommonConstant.HEADER_PROPERTY_VALUE.CONTENT_TYPE + "\n%s";
    /**
     * Authorization头的模板，第一个参数为accessId，第二个参数为计算出的签名
     */
    private static final String AUTHORIZATION_TEMPLATE = "Dataplus %s:%s";

    private static final int RESPONSE_CODE_SUCCESS = 200;
    private static final String ERROR_MESSAGE_TEMPLATE = "HTTP返回错误，code=%d，错误消息：%s";

    /**
     * 获得URLConnection 对象
     * @param url url
     * @param gmtDateString GMT时间日期
     * @param body 请求体
     * @param authorization 阿里云授权信息
     * @param method HTTP METHOD
     * @return URLConnection对象
     * @throws IOException IO异常
     */
    private static URLConnection getConnection(URL url, String gmtDateString, String body, Authorization authorization, CommonConstant.HTTP_METHOD method) throws IOException {

        String md5edBody = toMD5Base64(body);

        String contentToSign = String.format(SIGNING_CONTENT_TEMPLATE, method, md5edBody, gmtDateString);

        String signature = com.doubeye.commons.utils.string.StringUtils.toHmacSha1(contentToSign, authorization.getAccessKeySecret());

        String authorizationContent = String.format(AUTHORIZATION_TEMPLATE, authorization.getAccessKeyId(), signature);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method.toString());

        connection.setRequestProperty(CommonConstant.HEADER_PROPERTY_NAME.ACCEPT.toString(), CommonConstant.HEADER_PROPERTY_VALUE.ACCEPT.toString());
        connection.setRequestProperty(CommonConstant.HEADER_PROPERTY_NAME.CONTENT_TYPE.toString(), CommonConstant.HEADER_PROPERTY_VALUE.CONTENT_TYPE.toString());
        connection.setRequestProperty(CommonConstant.HEADER_PROPERTY_NAME.DATE.toString(), gmtDateString);
        connection.setRequestProperty(CommonConstant.HEADER_PROPERTY_NAME.AUTHORIZATION.toString(), authorizationContent);
        connection.setRequestProperty(CommonConstant.HEADER_PROPERTY_NAME.ACCEPT_CHARSET.toString(), CommonConstant.CHARSETS.UTF_8.toString());
        connection.setRequestProperty(CommonConstant.HEADER_PROPERTY_NAME.CONTENTYYE.toString(), CommonConstant.CHARSETS.UTF_8.toString());
        return connection;
    }

    /**
     * 获得POST URLConnection对象
     * @param url url
     * @param body 请求体
     * @param authorization 阿里云授权信息
     * @return POST URLConnection对象
     * @throws IOException IO异常
     */
    private static URLConnection getPostConnection(String url, String body, Authorization authorization) throws IOException {
        return getPostLikeConnection(url, body, authorization, CommonConstant.HTTP_METHOD.POST);
    }

    private static URLConnection getPutConnection(String url, String body, Authorization authorization) throws IOException {

        return getPostLikeConnection(url, body, authorization, CommonConstant.HTTP_METHOD.PUT);
    }

    private static URLConnection getPostLikeConnection(String url, String body, Authorization authorization, CommonConstant.HTTP_METHOD method) throws IOException {
        URL urlObject = new URL(url);
        String gmtDateString = DateTimeUtils.toGMTString(new Date());
        URLConnection connection = getConnection(urlObject, gmtDateString, body, authorization, method);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        return connection;
    }


    /**
     * 获得通用 URLConnection对象
     * @param url url
     * @param authorization 阿里云授权信息
     * @param method HTTP 方法
     * @return URLConnection对象
     * @throws IOException IO异常
     */
    private static URLConnection getGeneralConnection(String url, Authorization authorization, CommonConstant.HTTP_METHOD method) throws IOException {
        URL urlObject = new URL(url);
        String gmtDateString = DateTimeUtils.toGMTString(new Date());
        return getConnection(urlObject, gmtDateString, "", authorization, method);
    }

    /**
     * 获得GET URLConnection对象
     * @param url url
     * @param authorization 阿里云授权信息
     * @return GET URLConnection对象
     * @throws IOException IO异常
     */
    private static URLConnection getGetConnection(String url, Authorization authorization) throws IOException {
        URL urlObject = new URL(url);
        String gmtDateString = DateTimeUtils.toGMTString(new Date());
        return getConnection(urlObject, gmtDateString, "", authorization, CommonConstant.HTTP_METHOD.GET);
    }

    /**
     * 连接
     * @param connection URLConnection对象
     * @param body 请求体
     * @return 请求结果
     * @throws IOException IO异常
     */
    private static String doConnection(URLConnection connection, String body) throws IOException {
        if (!StringUtils.isEmpty(body)) {

            try (OutputStream writer = connection.getOutputStream()) {
                writer.write(body.getBytes(CommonConstant.CHARSETS.UTF_8.toString()));
                writer.flush();
            }

        }
        int statusCode = ((HttpURLConnection)connection).getResponseCode();
        try (BufferedReader reader = (statusCode == RESPONSE_CODE_SUCCESS) ?
                new BufferedReader(new InputStreamReader((connection).getInputStream(), CommonConstant.CHARSETS.UTF_8.toString())) :
                new BufferedReader(new InputStreamReader(((HttpURLConnection)connection).getErrorStream()))) {
            String response = processResult(reader);
            if (statusCode != RESPONSE_CODE_SUCCESS) {
                throw new IOException(String.format(ERROR_MESSAGE_TEMPLATE, statusCode, response));
            } else {
                return response;
            }
        }

    }

    /**
     * 执行Post
     * @param url url
     * @param body 请求体
     * @param authorization 阿里云授权信息
     * @return 请求结果
     * @throws IOException IO异常
     */
    public static String doPost(String url, String body, Authorization authorization) throws IOException {
        URLConnection connection = getPostConnection(url, body, authorization);
        try {
            return doConnection(connection, body);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 执行GET
     * @param url url
     * @param authorization 阿里云授权信息
     * @return 请求结果
     * @throws IOException IO异常
     */
    public static String doGet(String url, Authorization authorization) throws IOException {
        URLConnection connection = getGetConnection(url, authorization);
        return doConnection(connection, "");
    }

    public static String doDelete(String url, Authorization authorization) throws IOException {
        URLConnection connection = getGeneralConnection(url, authorization, CommonConstant.HTTP_METHOD.DELETE);
        return doConnection(connection, "");
    }

    public static String doPut(String url, String body, Authorization authorization) throws IOException {
        URLConnection connection = getPutConnection(url, body, authorization);
        return doConnection(connection, body);
    }

    /**
     * 处理请求结果
     * @param reader BufferedReader对象
     * @return 处理结果
     * @throws IOException IO异常
     */
    public static String processResult(BufferedReader reader) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
