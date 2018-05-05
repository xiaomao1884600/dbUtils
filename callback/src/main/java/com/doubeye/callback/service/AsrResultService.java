package com.doubeye.callback.service;

import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author doubeye
 * 腾讯语音识别结果服务
 */
public class AsrResultService extends HttpServlet{
    private static String errorFile = CallbackLoggerService.TMP_DIRECTORY + "/failLog";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<File> resultFiles = getResultFileNames();
        JSONArray result = new JSONArray();
        resultFiles.forEach(file -> {
            try {
                JSONObject content = JSONUtils.getJsonObjectFromFile(file);
                result.add(content);
            } catch (Exception e) {
                StringBuilder builder = new StringBuilder();
                builder.append(file.getName()).append("\r\n");
                try {
                    FileUtils.toFile(errorFile, builder, true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        ResponseHelper.result(response, result.toString());
    }

    private static List<File> getResultFileNames() throws IOException {
        return FileUtils.getAllFileInDirectory(CallbackLoggerService.TMP_DIRECTORY,  ".json", MAX_RESULT_COUNT);
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

    private static final int MAX_RESULT_COUNT = 20;
}
