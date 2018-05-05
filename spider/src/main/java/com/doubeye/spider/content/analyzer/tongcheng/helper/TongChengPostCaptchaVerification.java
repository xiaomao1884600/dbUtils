package com.doubeye.spider.content.analyzer.tongcheng.helper;

import com.doubeye.commons.utils.net.UrlContentGetter;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TongChengPostCaptchaVerification {
    public static String doPost(HttpPost post) throws IOException {
        return UrlContentGetter.doPost(post);
    }

    public static HttpPost generatePostObject(Document document, String verifyCode) throws UnsupportedEncodingException {
        String ip = document.getElementById(ID_IP).attr("value");
        String postUrl = String.format(URL_VERIFY_TEMPLATE, ip);
        String nameSpace = document.getElementById(ID_NAME_SPACE).attr("value");
        String uuid = document.getElementById(ID_UUID).attr("value");
        String url = document.getElementById(ID_URL).attr("value");
        HttpPost post = new HttpPost(postUrl);
        // 创建参数队列
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair(ID_NAME_SPACE, nameSpace));
        formParams.add(new BasicNameValuePair(ID_UUID, uuid));
        formParams.add(new BasicNameValuePair(ID_URL, url));
        formParams.add(new BasicNameValuePair(ID_VERIFY_CODE, verifyCode));
        //参数转码
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
        post.setEntity(uefEntity);
        return post;
    }


    private static void setPostHeader(HttpPost post) throws UnsupportedEncodingException {
        StringEntity se = new StringEntity("");
        se.setContentEncoding("UTF-8");
        se.setContentType("application/json");//发送json需要设置contentType
        post.setEntity(se);
    }

    private static final String ID_IP = "ip";
    private static final String ID_NAME_SPACE = "namespace";
    private static final String ID_UUID = "uuid";
    private static final String ID_URL = "url";
    private static final String ID_VERIFY_CODE = "verify_code";
    private static final String URL_VERIFY_TEMPLATE = "http://callback.58.com/firewall/valid/%s.do";

    public static void main(String[] args) throws IOException {
    }
}
