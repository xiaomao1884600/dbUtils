package com.doubeye.commons.utils.cloud.service.provider.tencent.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by Satroler on 2017/9/11.
 */
public class TestJava {

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException {

        //post请求参数组装
        String appid = "1255612177";
        String secretId = "AKIDeC0fDeGzxkOvJZNY7ZqvoVBj9ORNDqPp";
        String secretKey = "NabQ6qP4Xkv0xndHm7JKDwgcNxPxNbdU";
        String _url = "http://aai.qcloud.com/asr/v1/";
        String domain = "aai.qcloud.com";
        String path = "/asr/v1/";
        String projectid = "1091843";
        // String projectid="0";
        String template_name = "asr_1_datawin01";

/*        //测试数据
        FileInputStream fileInpustream = new FileInputStream(new File("E:\\mydoc\\ts\\240495889\\FileRecv\\Z3.wav"));
        fileInpustream.skip(44);
        int avaiable = fileInpustream.available();
        byte[] b = new byte[640];
        fileInpustream.read(b);
        //转为speex格式
        byte[] dataPacket = JSpeexService.wav2Speex(b);*/


        Map<String, String> urlMap = TencentHttpUtil.setUrlJson(appid, secretId, projectid, template_name,
                //ShengdongConstants.TENCENT_URL_PARAM.SUB_SERVICE_TYPE_STREAM_VOICE,
                //"1",
                "0",
                //ShengdongConstants.TENCENT_URL_PARAM.ENGINE_MODEL_TYPE_COMMON,
                //"1",
                "0",
                //ShengdongConstants.TENCENT_URL_PARAM.RES_TYPE_SYNCHRONIZED,
                "1",
                //ShengdongConstants.TENCENT_URL_PARAM.RES_TEXT_FORMAT_UTF8,
                "0",
                "1234567890abcdef",
                "3",
                //ShengdongConstants.TENCENT_URL_PARAM.SOURCE_TYPE_WEB
                "0"
        );
        //1、serverUrl
        String serverUrl = TencentHttpUtil.generateUrl(_url, urlMap);
        System.out.println("Server URL: " + serverUrl);
        //2、设置header
        String authinfo = TencentHttpUtil.createSign(serverUrl, secretKey);
        System.out.println("签名: " + authinfo);

        Map<String, String> header = new HashMap<>();
        // header.put("Host", "aai.qcloud.com");

        header.put("Authorization", authinfo);
                //header.put("Content-Type", "application/octet-stream");
                //header.put("Content-Length", dataPacket.length + "");

                /*
                HttpResponse<String> asrupload = Unirest.post(serverUrl)
                        .headers(header)
                        .body(dataPacket)
                        .asString();
                System.out.print(asrupload.getBody());
                */

    }
}
