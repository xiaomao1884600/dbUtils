package com.doubeye.callback.service;

import com.doubeye.callback.helper.TencentCallbackRespondHelper;
import com.doubeye.commons.utils.file.FileUtils;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * @author doubeye
 * 接收回调请求，并回调内容记录为文件
 */
public class CallbackLoggerService extends HttpServlet {
    static String TMP_DIRECTORY = System.getProperty("java.io.tmpdir");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("doGet");
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            builder.append(line);
        }
        String content = builder.toString();
        String[] parameters = content.split("&");
        JSONObject result = new JSONObject();
        for (String parameter : parameters) {
            String[] keyValue = parameter.split("=");
            System.out.println(parameter);
            if (keyValue.length == 2) {
                result.put(keyValue[0], URLDecoder.decode(keyValue[1], "utf-8"));
            }
        }

        String fileName = String.format(FILE_NAME_TEMPLATE,
                TMP_DIRECTORY, result.getString(PROPERTY_NAME.REQUEST_ID.toString()));
        FileUtils.toFile(fileName, result);
        TencentCallbackRespondHelper.success(response);
    }

    enum PROPERTY_NAME {
        /**
         * 请求编号
         */
        REQUEST_ID("requestId"),

        ;
        private String propertyName;
        PROPERTY_NAME(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String toString() {
            return getPropertyName();
        }
    }

    static final String FILE_NAME_TEMPLATE = "%s/%s.json";
}
