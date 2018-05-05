package com.doubeye.commons.utils.cloud.service.provider.aliyun.asr;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;
/**
 * Created by zhishen on 2017/7/10.
 */
public class Test {
    /*
     * 计算MD5+BASE64
     */
    public static String MD5Base64(String s) throws UnsupportedEncodingException {
        if (s == null) {
            return null;
        }
        String encodeStr = "";
        //string 编码必须为utf-8
        byte[] utfBytes = s.getBytes("UTF-8");
        MessageDigest mdTemp;
        try {
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            BASE64Encoder b64Encoder = new BASE64Encoder();
            encodeStr = b64Encoder.encode(md5Bytes);
        } catch (Exception e) {
            throw new Error("Failed to generate MD5 : " + e.getMessage());
        }
        return encodeStr;
    }
    /*
     * 计算 HMAC-SHA1
     */
    public static String HMACSha1(String data, String key) {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = (new BASE64Encoder()).encode(rawHmac);
        } catch (Exception e) {
            throw new Error("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }
    /*
     * 等同于javaScript中的 new Date().toUTCString();
     */
    public static String toGMTString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.UK);
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }
    /*
     * 发送POST请求
     */
    public static String sendPost(String url, String body, String ak_id, String ak_secret) {
        HttpURLConnection conn = null;
        Writer out = null;
        InputStream in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "POST";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            String bodyMd5 = MD5Base64(body);
            String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + content_type + "\n" + date ;
            System.out.println(stringToSign);
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            // 发送请求参数
            out.write(body);
            // flush输出流的缓冲
            out.flush();
            int rc = conn.getResponseCode();
            if (rc == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            result = changeInputStream(in, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    /*
     * 发送PUT请求
     */
    public static String sendPut(String url, String body, String ak_id, String ak_secret) {
        HttpURLConnection conn = null;
        PrintWriter out = null;
        InputStream in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "PUT";
            String accept = "application/json";
            String content_type = "application/json";
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            String bodyMd5 = MD5Base64(body);
            String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + content_type + "\n" + date ;
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            int rc = conn.getResponseCode();
            if (rc == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            result = changeInputStream(in, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    /*
     * 发送DELETE请求
     */
    public static String sendDelete(String url, String ak_id, String ak_secret) {
        HttpURLConnection conn = null;
        PrintWriter out = null;
        InputStream in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "DELETE";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            String stringToSign = method + "\n" + accept + "\n" + "" + "\n" + content_type + "\n" + date ;
            System.out.println(stringToSign);
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // 建立实际的连接
            conn.connect();
            int rc = conn.getResponseCode();
            if (rc == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            result = changeInputStream(in, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    /*
     * 发送DELETE请求
     */
    public static String sendGet(String url, String ak_id, String ak_secret) {
        HttpURLConnection conn = null;
        PrintWriter out = null;
        InputStream in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "GET";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            String stringToSign = method + "\n" + accept + "\n" + "" + "\n" + content_type + "\n" + date ;
            System.out.println(stringToSign);
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // 建立实际的连接
            conn.connect();
            int rc = conn.getResponseCode();
            if (rc == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            result = changeInputStream(in, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    /*
     * 发送POST请求
     */
    public static String sendNomalPost(String url, String body) {
        HttpURLConnection conn = null;
        PrintWriter out = null;
        InputStream in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            /*
             * http header 参数
             */
            String method = "POST";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("date", date);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            int rc = conn.getResponseCode();
            if (rc == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            result = changeInputStream(in, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    public static String changeInputStream(InputStream inputStream,
                                           String encode) {
        // ByteArrayOutputStream 一般叫做内存流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    /*
     * GET请求
     */
    public static String sendGet(String url, String task_id,String ak_id, String ak_secret) {
        HttpURLConnection conn = null;
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url+"/"+task_id);
            /*
             * http header 参数
             */
            String method = "GET";
            String accept = "application/json";
            String content_type = "application/json";
            String path = realUrl.getFile();
            String date = toGMTString(new Date());
            // 1.对body做MD5+BASE64加密
            //String bodyMd5 = MD5Base64("");
            String stringToSign = method + "\n" + accept + "\n" + "" + "\n" + content_type + "\n" + date;
            System.out.println(stringToSign);
            // 2.计算 HMAC-SHA1
            String signature = HMACSha1(stringToSign, ak_secret);
            // 3.得到 authorization header
            String authHeader = "Dataplus " + ak_id + ":" + signature;
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod(method);
            conn.setRequestProperty("accept", accept);
            conn.setRequestProperty("content-type", content_type);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("date", date);
            conn.setRequestProperty("Authorization", authHeader);
            // 建立实际的连接
            conn.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static void main(String[]args){
        String akId="LTAIknRDVvtaSLBM";
        String akSecret="6X9cZbHkjZtxFtPzwylIRajIMSdd6h";
        String url="https://nlsapi.aliyun.com/asr/custom/vocabs";
        /*
        String body="{\n"
                + "        \"global_weight\": 1,\n"
                + "        \"words\": [\n"
                + "            \"猕猴桃\",\n"
                + "            \"橘子\",\n"
                + "            \"石榴\"\n"
                + "        ],\n"
                + "        \"word_weights\": {\n"
                + "            \"橘子\": 2\n"
                + "        }\n"
                + "    }";
                */
        String body = "{\"global_weight\":3,\"words\":[\"香蕉\"],\"word_weights\":{\"苹果\":2}}";
        String body2="{\n"
                + "        \"global_weight\": 3,\n"
                + "        \"words\": [\n"
                + "            \"猕猴桃\",\n"
                + "            \"橘子\",\n"
                + "            \"葡萄\",\n"
                + "            \"石榴\"\n"
                + "        ],\n"
                + "        \"word_weights\": {\n"
                + "            \"橘子\": 2\n"
                + "        }\n"
                + "    }";
        //create result:{"request_id":"0363f31f197348f4b586a65d178a78ac","vocabulary_id":"1e5ad6bd96df4394b7933f28d6e07a89"}
        //create result:{"request_id":"457b47d310fe4008a364c869ff4e16a2","vocabulary_id":"12f61685a76e461d81c6812cd6acec9c"}
        //{"request_id":"8a29bed157ab41628e9a2ef2ed6ffce7","vocabulary_id":"2c680da52ced4474b0420a91ccfd779f"}
        //create result:{"request_id":"457b47d310fe4008a364c869ff4e16a2","vocabulary_id":"12f61685a76e461d81c6812cd6acec9c"}
        //create
        //String result= Test.sendPost(url,body,akId,akSecret);
        //System.out.println("create result:"+result);//create result:{"request_id":"***","vocabulary_id":"###"}
        //String vocabId=(String)JSONPath.read(result,"vocabulary_id");
        /*/
        //update
        result=Test.sendPut(url+"/"+vocabId,body2,akId,akSecret);
        System.out.println("update result:"+result);//update result:{"request_id":"***"}
        */
        //get
        //String result=Test.sendGet(url+"/", "07cb5f1f89094b3d8a05efbc79d6f711", akId,akSecret);
        //System.out.println("get result:"+result);//get result:{"request_id":"***","global_weight":3,"words":["猕猴桃","橘子","葡萄","石榴"],"word_weights":{"橘子":2}}

        //delete
        String result=Test.sendDelete(url+"/"+"07cb5f1f89094b3d8a05efbc79d6f711",akId,akSecret);
        System.out.println("delete result:"+result);//delete result:{"request_id":"***"}

    }
}