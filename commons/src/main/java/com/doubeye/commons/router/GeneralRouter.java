package com.doubeye.commons.router;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.token.TokenUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.4.1
 * + 加入返回下载文件的支持
 */
public class GeneralRouter extends HttpServlet{

    private static Logger logger = LogManager.getLogger(GeneralRouter.class);
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> formDataParameter = RequestHelper.processFromData(request);
        Map<String, String[]> params = request.getParameterMap();
        //合并url中的参数和post的参数
        formDataParameter.putAll(params);
        //将远程地址放入到参数中
        logger.trace(RequestHelper.getRequestParameters(formDataParameter));
        formDataParameter.put(REQUEST_PARAMETER_REMOTE_HOST, new String[] {request.getRemoteHost()});
        String objectName = RequestHelper.getString(formDataParameter, "objectName");
        String action = RequestHelper.getString(formDataParameter, "action");
        //todo 做到列表中 DELETE AFTER test
        //if (!"login".equals(action) && !"downloadHosts".equals(action) && !"saveUser".equalsIgnoreCase(action) && !"saveUserLeader".equalsIgnoreCase(action) && !"getDetailFile".equalsIgnoreCase(action) && !"getResignedEnrollment".equalsIgnoreCase(action)) {
        if (!uncheckedActions.contains(action)) {
            boolean checked = checkLogin(formDataParameter);
            if (!checked) {
                //todo test getNotLogin
                JSONObject result = ResponseHelper.getSuccessObject();
                result.put("NOT_LOGIN", true);
                ResponseHelper.success(response, result);
                return;
            }
        }
        if (StringUtils.isEmpty(objectName)) {
            ResponseHelper.error(response, "没有指定调用的对象名。");
            return;
        }
        if (StringUtils.isEmpty(action)) {
            ResponseHelper.error(response, "没有指定调用的对象方法。");
            return;
        }
        try {
            Class<?> c = Class.forName(objectName);
            Constructor<?> constructor = c.getConstructor();
            Method method = c.getMethod(action, Map.class);
            Object o = constructor.newInstance();
            Object result = method.invoke(o, formDataParameter);
            // TODO 这个地方需要弄的更优美
            if (result instanceof JSONArray) {
                if (((JSONArray) result).size() > 0) {
                    Object obj = ((JSONArray) result).get(0);
                    if (obj instanceof JSONObject) {
                        // 重定向
                        if (((JSONObject) obj).has("REDIRECT")) {
                            ResponseHelper.redirect(response, ResponseHelper.PROGRESS_REDIRECT, ((JSONObject) obj).get("REDIRECT").toString());
                        } else {
                            ResponseHelper.result(response, result.toString());
                        }
                    } else {
                        ResponseHelper.result(response, result.toString());
                    }
                    // 如果返回的JSONArray为空数组，则表明无结果集
                } else if (((JSONArray) result).size() == 0){
                    ResponseHelper.noResult(response);
                }
            } else if (result instanceof JSONObject) {
                JSONObject obj = (JSONObject) result;
                // 成功返回
                if (obj.has("SUCCESS") && (obj.getBoolean("SUCCESS"))) {
                     if (obj.has(ResponseHelper.DOWNLOAD_STRING_LIST_CONTENT )) {
                        @SuppressWarnings("unchecked")
                        List<String> content = (List<String>) obj.get("content");
                        String defaultFileName = obj.getString("defaultFileName");
                        if (obj.has("CHARSET")) {
                            ResponseHelper.sendFile(request, response, content, defaultFileName, obj.getString("CHARSET"));
                        } else {
                            ResponseHelper.sendFile(request, response, content, defaultFileName);
                        }
                        return;
                    }
                    ResponseHelper.success(response, obj);
                }
            } else {
                ResponseHelper.result(response, result.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.error(response, e.getCause().getMessage());
        }
    }

    /**
     * ip参数名
     */
    public static final String REQUEST_PARAMETER_REMOTE_HOST = "_remoteHost";
    private boolean checkLogin(Map<String, String[]> parameters) {
        GlobalApplicationContext context = GlobalApplicationContext.getInstance();
        if (!parameters.containsKey("_expiredDate") || !parameters.containsKey("_userId") || !parameters.containsKey("_token")) {
            return false;
        }
        String expiredDateString = RequestHelper.getString(parameters, "_expiredDate");
        Date expiredDate = null;
        try {
            expiredDate = DateTimeUtils.getDate(expiredDateString, context.getStringParameter("DEFAULT_TIME_FORMAT"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String userId = RequestHelper.getString(parameters, "_userId");
        String token = RequestHelper.getString(parameters, "_token");
        //无过期时间
        if (StringUtils.isEmpty(expiredDateString)) {
            return false;
        } else if (expiredDate ==null || new Date().compareTo(expiredDate) > 0) {
            //已经过期
            return false;
        } else {
            String secretKey = context.getStringParameter("TOKEN_SECRET_KEY");
            try {
                String generatedToken = TokenUtils.generateToken(userId, expiredDateString, secretKey);
                return generatedToken.equalsIgnoreCase(token);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                return false;
            }
        }
    }

    private static final List<String> uncheckedActions = new ArrayList<>();
    static {
        uncheckedActions.add("login");
        uncheckedActions.add("downloadHosts");
        uncheckedActions.add("saveUser");
        uncheckedActions.add("saveUserLeader");
        uncheckedActions.add("getDetailFile");
        uncheckedActions.add("getResignedEnrollment");
        uncheckedActions.add("getAllMenus");
    }
}
