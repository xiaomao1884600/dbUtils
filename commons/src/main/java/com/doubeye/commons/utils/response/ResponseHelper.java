package com.doubeye.commons.utils.response;

import com.doubeye.commons.utils.constant.CommonConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;



/**
 * @author doubeye
 * @version 1.0.1
 * 用来帮助简化服务端向客户端返回的助手类
 * history
 *  1.0.1
 *   + getSuccessObject 增加新重载方法，添加对分页的支持
 */
@SuppressWarnings("unused")
public class ResponseHelper {
    /**
     * 日志类
     */
    private static Logger logger = LogManager.getLogger(ResponseHelper.class);
    /**
     * 默认的成功返回内容
     */
    public static final String SUCCESS = "{\"SUCCESS\" : true}";
    /**
     * 无返回结果的内容模板
     */
    private static final String NO_RESULT = "{\"SUCCESS\" : true, \"NO_RESULT\" : true, \"DATA\" : []}";
    /**
     * 错误返回模板
     */
    private static final String ERROR = "{\"SUCCESS\" : false, \"errorMessage\" : \"%s\"}";
    /**
     * 有结果集的返回模板
     */
    private static final String DATA_RESULT = "{\"SUCCESS\" : true, \"DATA\" : %s}";
    /**
     * 重定向的返回模板
     */
    private static final String REDIRECT = "{\"REDIRECT\" : true, \"url\" : \"%s\", \"uuid\" : \"%s\", \"SUCCESS\" : true}";
    /**
     * 下载返回模板
     */
    public static final String DOWNLOAD_STRING_LIST_CONTENT = "DOWNLOAD-STRING-LIST-CONTENT";
    /**
     * 处理重定向返回模板
     */
    public static final String PROGRESS_REDIRECT = "generalRouter?objectName=com.hxsd.services.ProgressChecker&action=getProgress";
    /**
     * 未登录的属性名
     */
    private static final String NOT_LOGIN_FLAG = "NOT_LOGIN";
    /**
     * 总记录数，用来进行分页
     */
    private static final String TOTAL_RECORDS = "TOTAL_RECORDS";

    private static final String DATA = "DATA";



    /**
     * 返回错误内容
     * @param response HttpServletResponse对象
     * @param errorMessage 错误信息
     */
    public static void error(HttpServletResponse response, String errorMessage) {
        logger.error(errorMessage);
        send(response, String.format(ERROR, errorMessage.replace("\"", "\\\\\"").replace("\n", " ")));
    }

    /**
     * 返回成功标记
     * @param response HttpServletResponse对象
     */
    public static void success(HttpServletResponse response) {
        send(response, SUCCESS);
    }

    public static void success(HttpServletResponse response, JSONObject result) {
        send(response, result.toString());
    }

    /**
     * 返回数据
     * @param response HttpServletResponse对象
     * @param result 数据的JSON格式
     */
    public static void result(HttpServletResponse response, String result) {
        send(response, String.format(DATA_RESULT, result));
    }

    public static JSONObject getSuccessObject(JSONArray dataSet, long totalRecords) {
        JSONObject result = getSuccessObject();
        result.put(DATA, dataSet);
        result.put(TOTAL_RECORDS, totalRecords);
        return result;
    }

    /**
     * 获得成功返回对象
     * @param dataSet 数据集
     * @param toBeMerge 要融合到结果集的对象
     * @return 成功对象
     */
    public static JSONObject getSuccessObject(JSONArray dataSet, JSONObject toBeMerge) {
        JSONObject result = getSuccessObject();
        result.put(DATA, dataSet);
        result.putAll(toBeMerge);
        return result;
    }



    /**
     * 返回重定向
     * @param response HttpServletResponse 对象
     * @param url 重定向地址
     * @param uuid 重定向后的uuid
     */
    public static void redirect(HttpServletResponse response, String url, String uuid) {
        send(response, String.format(REDIRECT, url, uuid));
    }
    /**
     * 返回无结果集
     * @param response HttpServletResponse对象
     */
    public static void noResult(HttpServletResponse response) {
        send(response, NO_RESULT);
    }

    /**
     * 设置默认的response格式
     * @param response HttpServletResponse对象
     */
    private static void setDefaultResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
    }

    /**
     * 返回内容
     * @param response HttpServletResponse对象
     * @param content 返回的内容
     */
    public static void send(HttpServletResponse response, String content) {
        setDefaultResponse(response);
        addCorsSupport(response);

        try (PrintWriter out = response.getWriter()) {
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入跨域支持
     * @param response HttpServletResponse 对象
     */
    private static void addCorsSupport(HttpServletResponse response) {
        addCorsSupport(response, "*");
    }

    /**
     * 发送文件
     * @param request HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     * @param content 发送的文件内容
     * @param defaultFileName 默认文件名
     * @param charset 字符集
     * @throws IOException IO异常
     */
    public static void sendFile(HttpServletRequest request, HttpServletResponse response, List<String> content, String defaultFileName, String charset) throws IOException {
        response.setCharacterEncoding(charset);
        //设置文件MIME类型
        response.setContentType(request.getServletContext().getMimeType(defaultFileName));
        if (StringUtils.isEmpty(response.getContentType())) {
            response.setContentType("application/octet-stream");
        }
        //设置Content-Disposition
        //response.setHeader("Content-Disposition", "attachment;filename=" + defaultFileName);
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(defaultFileName.getBytes("utf-8"), "ISO_8859_1"));
        //读取目标文件，通过response将目标文件写到客户端


        try (PrintWriter out = response.getWriter()){
            for (String line : content) {
                out.write(line + "\n");
            }
        }
    }


    /**
     * 发送文件
     * @param request HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     * @param content 发送的文件内容
     * @param defaultFileName 默认文件名
     * @throws IOException IO异常
     */
    public static void sendFile(HttpServletRequest request, HttpServletResponse response, List<String> content, String defaultFileName) throws IOException {
        sendFile(request, response, content, defaultFileName, CommonConstant.CHARSETS.UTF_8.toString());
    }

    /**
     * @param response HttpServletResponse 对象
     * @param domain 允许访问的域名，如果有多个域名，用逗号分隔
     */
    private static void addCorsSupport(HttpServletResponse response, String domain) {
        response.setHeader("Access-Control-Allow-Origin", domain);
        response.setHeader("Access-Control-Allow-Methods", "POST,GET");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // response.setHeader("Access-Control-Allow-Headers", "allowHeaders");
        // response.setHeader("Access-Control-Expose-Headers", "exposeHeaders");
    }

    /*
     * TODO 在Core中建立全局的属性对象，并建立对全局属性改变的消息的订阅
     * @param response HttpServletResponse 对象
     */
    /*
    private static void addDefaultCorsSupport(HttpServletResponse response) throws SQLException {
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection()){
            String domains = SystemProperties.getString(conn,"allowedOrigins");
            addCorsSupport(response, domains);
        }
    }
    */

    /**
     * 获得成功返回对象
     * @return 成功的返回对象
     */
    public static JSONObject getSuccessObject() {
        return JSONObject.fromObject(SUCCESS);
    }


    public static JSONObject getNotLoginObject() {
        JSONObject result = JSONObject.fromObject(SUCCESS);
        result.put(NOT_LOGIN_FLAG, true);
        return result;
    }

    /**
     * 获得文件返回的对象
     * @param content 返回的文件内容
     * @param defaultFileName 默认文件名
     * @return 返回下载文件的对象
     */
    public static JSONObject getDownloadStringListContentObject(List<String> content, String defaultFileName) {
        return getDownloadStringListContentObject(content, defaultFileName, CommonConstant.CHARSETS.UTF_8.toString());
    }

    /**
     * 获得文件返回的对象
     * @param content 返回的文件内容
     * @param defaultFileName 默认文件名
     * @return 返回下载文件的对象
     */
    public static JSONObject getDownloadStringListContentObject(List<String> content, String defaultFileName, String charset) {
        JSONObject result = getSuccessObject();
        result.put(DOWNLOAD_STRING_LIST_CONTENT, true);
        result.put("content", content);
        result.put("defaultFileName", defaultFileName);
        result.put("CHARSET", charset);
        return result;
    }

    /**
     * 获得文件返回的对象
     * @param builder 返回的文件内容
     * @param defaultFileName 默认文件名
     * @return 返回下载文件的对象
     */
    public static JSONObject getDownloadStringListContentObject(StringBuilder builder, String defaultFileName) {
        JSONObject result = getSuccessObject();
        result.put(DOWNLOAD_STRING_LIST_CONTENT, true);
        result.put("content", builder.toString());
        result.put("defaultFileName", defaultFileName);
        return result;
    }
}
