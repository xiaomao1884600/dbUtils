package com.doubeye.commons.utils.net;

import com.doubeye.commons.utils.collection.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlContentGetter {
    public static String getHtmlCode(String url, String encoding) {
        StringBuilder sBuffer = new StringBuilder();
        try {
            // 建立网络连接
            URL uri = new URL(url);
            // 打开连接
            URLConnection urlConnection = uri.openConnection();

            try (InputStream inputStream =urlConnection.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
                 BufferedReader bReader = new BufferedReader(inputStreamReader)){
                String temp;
                while ((temp = bReader.readLine()) != null) {
                    sBuffer.append(temp).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO log-it
        }
        return sBuffer.toString();
    }

    public static String getHtml(String url, Map<String, String> httpHeader) {
        try {
            HttpGet httpGet = new HttpGet(url);
            setBrowserHeader(httpGet, httpHeader);

            //try (CloseableHttpClient httpClient = HttpClients.createDefault();
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            // Waiting for a connection from connection manager
                            .setConnectionRequestTimeout(10000)
                            // Waiting for connection to establish
                            .setConnectTimeout(5000)
                            .setExpectContinueEnabled(false)
                            // Waiting for data
                            .setSocketTimeout(5000)
                            .setCookieSpec("easy")
                            .build())
                    .setMaxConnPerRoute(20)
                    .setMaxConnTotal(100)
                    .build();
                 CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getHtml(String url, Map<String, String> httpHeader, String proxyHost, int port) throws IOException {

        HttpGet httpGet = new HttpGet(url);
        setBrowserHeader(httpGet, httpHeader);
        HttpHost proxy = new HttpHost(proxyHost, port);
        HttpClientBuilder hcBuilder = HttpClients.custom();
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        hcBuilder.setRoutePlanner(routePlanner);
        try (CloseableHttpClient httpClient = hcBuilder.build()) {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 打印响应状态
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }

        return null;
    }

    public static byte[] fromUrl(String url) throws IOException {
        // 建立网络连接
        URL uri = new URL(url);
        // 打开连接
        URLConnection urlConnection = uri.openConnection();
        try (InputStream inputStream =urlConnection.getInputStream();
             ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
    }

    public static String doPost(HttpPost post) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 打印响应状态
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }
        return "";
    }

    public static String doPost(String url, Map<String, String> header) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        setBrowserHeader(httpPost, header);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 打印响应状态
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }
        return "";
    }

    private static final int BUFFER_SIZE = 4096;

    private static void setBrowserHeader(HttpRequestBase httpRequestBase, Map<String, String> header) {
        for (String key : header.keySet()) {
            httpRequestBase.addHeader(key, header.get(key));
        }
    }

    public static final Map<String, String> FIREFOX_HEADER = new HashMap<>();
    static {
        FIREFOX_HEADER.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        FIREFOX_HEADER.put("Accept-Language", "zh-CN,zh;q=0.8");
        FIREFOX_HEADER.put("Cache-Control", "no-cache");
        FIREFOX_HEADER.put("Connection","keep-alive");
        FIREFOX_HEADER.put("Pragma", "no-cache");
        FIREFOX_HEADER.put("Upgrade-Insecure-Requests", "1");
        FIREFOX_HEADER.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
    }

    /*
    private static final Map<String, String> TIANYANCHA_HEADER = new HashMap<>();
    static {
        TIANYANCHA_HEADER.put("Accept", "application/json, text/plain");
        TIANYANCHA_HEADER.put("Accept-Encoding", "gzip, deflate, br");
        TIANYANCHA_HEADER.put("Accept-Language", "zh-CN,zh;q=0.8");
        TIANYANCHA_HEADER.put("Cache-Control", "no-cache");
        TIANYANCHA_HEADER.put("Connection", "keep-alive");
        TIANYANCHA_HEADER.put("Host", "www.tianyancha.com");
        TIANYANCHA_HEADER.put("Pragma", "no-cache");
        TIANYANCHA_HEADER.put("Referer", "https://www.tianyancha.com/search?key=%E6%B5%8B%E8%AF%95&checkFrom=searchBox&rnd=");
        TIANYANCHA_HEADER.put("Upgrade-Insecure-Requests", "1");
        TIANYANCHA_HEADER.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        //auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNDg2OTY5MSwiZXhwIjoxNTIwNDIxNjkxfQ.30ikFZCjCSsbcLOAy14V4DuhUu2NUhTDzy8w0kneOV69e1sFkMytor0LlqeSsjFxbTfNA_XAQGLV5iCSIpcPQg
        //TIANYANCHA_HEADER.put("Cookie", "aliyungf_tc=AQAAABNM2y9KHAAALOz3ckcMlLfjnbUj; csrfToken=NY-bGj-ApG8BrXzjXo27a5aP; TYCID=39cd51008ef811e7a257f9305d665c22; uccid=50b4cf4ccc79d508d43a70acd0a633c8; ssuid=3992077791; jsid=SEM-BAIDU-JP-SY-000035; bannerFlag=true; token=597a93261e5b44cb80eacc4bcadf911a; _utm=c8e32498c0164ddbaa79d8262bb6f708; tyc-user-info=%257B%2522token%2522%253A%2522eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNDg2ODg1NywiZXhwIjoxNTIwNDIwODU3fQ.GZpema6jFWYxa4G-61inpSKWVUA2vjfTIWG3ksfOn6Ql6Nud--HGk_5BrTy-Ra4EpzFlekefKb7k64LBJBHnQA%2522%252C%2522integrity%2522%253A%25220%2525%2522%252C%2522state%2522%253A%25220%2522%252C%2522vnum%2522%253A%25220%2522%252C%2522onum%2522%253A%25220%2522%252C%2522mobile%2522%253A%252213581572543%2522%257D; auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNDg2ODg1NywiZXhwIjoxNTIwNDIwODU3fQ.GZpema6jFWYxa4G-61inpSKWVUA2vjfTIWG3ksfOn6Ql6Nud--HGk_5BrTy-Ra4EpzFlekefKb7k64LBJBHnQA; Hm_lvt_e92c8d65d92d534b0fc290df538b4758=1504258287,1504606052,1504675396; Hm_lpvt_e92c8d65d92d534b0fc290df538b4758=1504869367; _csrf=ciGi2aSNOIYEooP5SpDvvA==; OA=LeaQu/qj8crONtTHBYNcloV97ScD3FkEYZzAMwq7adw5Yl+aQjvBnetRF1NCPvzOtn0bftjvZMu2PVpAFN9Z/A==; _csrf_bk=affc0e41f2b8800bb091209c9deda19c");
        //TIANYANCHA_HEADER.put("Cookie", "auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTA5MTkwMCwiZXhwIjoxNTIwNjQzOTAwfQ.X8mhxSl_XDM2KzUiHHePrWr62hTwC-BaZDlLSrNlpzOX9I8G6U0VyP5PIKMreRtMoisKdIwwRwF9Sc6gGjSaWg");
        TIANYANCHA_HEADER.put("Cookie", "auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTA5NjEwMSwiZXhwIjoxNTIwNjQ4MTAxfQ.bTrLvBN-sQtLKVMpifs1ooonZGd0NHAmzpNGiYfJVhpD-Z0sKr5LOq8NXPHu9I4ePgW1APEGJxyIxev24MOFQg");
        TIANYANCHA_HEADER.put("Tyc-From", "normal");
        TIANYANCHA_HEADER.put("Check-Error", "check");
    }
    */

    public static final List<String> TIANYANCHA_USED_COOKIE_NAMES = new ArrayList<>();
    static {
        /*
        TIANYANCHA_USED_COOKIE_NAMES.add("aliyungf_tc");
        TIANYANCHA_USED_COOKIE_NAMES.add("csrfToken");
        TIANYANCHA_USED_COOKIE_NAMES.add("TYCID");
        TIANYANCHA_USED_COOKIE_NAMES.add("uccid");
        TIANYANCHA_USED_COOKIE_NAMES.add("ssuid");
        TIANYANCHA_USED_COOKIE_NAMES.add("jsid");
        TIANYANCHA_USED_COOKIE_NAMES.add("bannerFlag");
        TIANYANCHA_USED_COOKIE_NAMES.add("tyc-user-info");
        */
        TIANYANCHA_USED_COOKIE_NAMES.add("auth_token");
        /*
        TIANYANCHA_USED_COOKIE_NAMES.add("_csrf");
        TIANYANCHA_USED_COOKIE_NAMES.add("OA");
        TIANYANCHA_USED_COOKIE_NAMES.add("_csrf_bk");
        TIANYANCHA_USED_COOKIE_NAMES.add("Hm_lvt_?");
        TIANYANCHA_USED_COOKIE_NAMES.add("Hm_lpvt_?");
        TIANYANCHA_USED_COOKIE_NAMES.add("token");
        TIANYANCHA_USED_COOKIE_NAMES.add("_utm");
        */
    }

    public static String getHtmlByJsoup(String url, Map<String, String> header) throws IOException {
        Connection conn = Jsoup.connect(url);
        //请求头设置，特别是cookie设置

        for (String key : header.keySet()) {
            conn.header(key, header.get(key));
        }

        //con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        //con.header("Accept-Encoding", "gzip, deflate, br");
        //con.header("Accept-Language", "zh-CN,zh;q=0.8");
        //con.header("Cache-Control", "no-cache");
        //con.header("Connection", "keep-alive");
        //con.header("Host", "www.tianyancha.com");
        //con.header("Pragma", "no-cache");
        //con.header("Referer", "https://www.tianyancha.com/search?key=%E6%B5%8B%E8%AF%95&checkFrom=searchBox&rnd=");
        //con.header("Upgrade-Insecure-Requests", "1");
        //con.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        //con.header("Cookie", "aliyungf_tc=AQAAABNM2y9KHAAALOz3ckcMlLfjnbUj; csrfToken=NY-bGj-ApG8BrXzjXo27a5aP; TYCID=39cd51008ef811e7a257f9305d665c22; uccid=50b4cf4ccc79d508d43a70acd0a633c8; ssuid=3992077791; jsid=SEM-BAIDU-JP-SY-000035; bannerFlag=true; token=597a93261e5b44cb80eacc4bcadf911a; _utm=c8e32498c0164ddbaa79d8262bb6f708; tyc-user-info=%257B%2522token%2522%253A%2522eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEwNzI5MSwiZXhwIjoxNTIwNjU5MjkxfQ.WfDtsKup7eARftjF_Je5vd08gYSIoDQpGaFkE9smpmRMjU9OiKZI8_2hEbUY6qxQjNbRsjcEx1oHOroVepiQfw%2522%252C%2522integrity%2522%253A%25220%2525%2522%252C%2522state%2522%253A%25220%2522%252C%2522vnum%2522%253A%25220%2522%252C%2522onum%2522%253A%25220%2522%252C%2522mobile%2522%253A%252213581572543%2522%257D; auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEwNzI5MSwiZXhwIjoxNTIwNjU5MjkxfQ.WfDtsKup7eARftjF_Je5vd08gYSIoDQpGaFkE9smpmRMjU9OiKZI8_2hEbUY6qxQjNbRsjcEx1oHOroVepiQfw; _csrf=pMDB9QBvhL+sIuzvlJjZFA==; OA=LeaQu/qj8crONtTHBYNclnSDXkF2sr4qzYpRPTrE0L1aYttwCding6yhN/XqwJFHsnKBOBC0Piaik/tPTk7XaEv14JtrouhumL0FKgMr+gCcb18+aRSqBMZphNkDuIk5; _csrf_bk=4c4656ecc902eaaf2fd914aea2b75220; Hm_lvt_e92c8d65d92d534b0fc290df538b4758=1504258287,1504606052,1504675396; Hm_lpvt_e92c8d65d92d534b0fc290df538b4758=1505117078");

        //解析请求结果
        Document doc=conn.get();
        //获取标题
        return doc.html();
    }




    public static Map<String, String> getTianYanChaHeader(String cookie) {
        Map<String, String> header = new HashMap<>();
        
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "zh-CN,zh;q=0.8");
        header.put("Cache-Control", "no-cache");
        header.put("Connection", "keep-alive");
        header.put("Host", "www.tianyancha.com");
        header.put("Pragma", "no-cache");
        header.put("Referer", "https://www.tianyancha.com/search?key=%E6%B5%8B%E8%AF%95&checkFrom=searchBox&rnd=");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        header.put("Cookie", cookie);
        //header.put("Tyc-From", "normal");
        //header.put("Check-Error", "check");
        return header;
    }

    public static void setTianYanChaCookie(String cookie) {
        //TIANYANCHA_HEADER.put("Cookie", cookie);
    }



    public static void main(String[] args) throws IOException {
        System.out.println(UrlContentGetter.getHtmlCode("http://www.chinahr.com/sou/?orderField=relate&keyword=摄影师&city=34&companyType=0&degree=-1&refreshTime=1&workAge=-1&page=1", "utf-8"));
        //System.out.println(UrlContentGetter.getHtml("https://www.zhipin.com/c101010100/h_101010100/?query=JAVA&page=30", FIREFOX_HEADER));
        //System.out.println(UrlContentGetter.getHtmlCode("https://www.zhipin.com/c101010100/h_101010100/?query=JAVA&page=30", "utf-8"));
        // 测试代理
        //System.out.println(UrlContentGetter.getHtml("https://www.zhipin.com/c101010100/h_101010100/?query=JAVA&page=29", FIREFOX_HEADER, "75.151.213.85", 8080));

    }
}
