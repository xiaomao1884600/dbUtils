package com.doubeye.callback.service;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;

import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @author doubeye
 * 移除回调服务中的指定结果
 */
public class ResultRemoveService extends HttpServlet{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameters = RequestHelper.processFromData(request);
        String requestIds = RequestHelper.getString(parameters, "savedRequestIds");
        List<String> ids = CollectionUtils.split(requestIds, CommonConstant.SEPARATOR.COMMA.toString());
        ids.forEach(id -> {
            String fileName = String.format(
                    CallbackLoggerService.FILE_NAME_TEMPLATE, CallbackLoggerService.TMP_DIRECTORY, id);
            System.out.println(fileName);
            FileUtils.deleteQuietly(new File(fileName));
        });
        JSONObject result = ResponseHelper.getSuccessObject();
        result.put("id", requestIds);
        ResponseHelper.result(response, result.toString());
    }
}
