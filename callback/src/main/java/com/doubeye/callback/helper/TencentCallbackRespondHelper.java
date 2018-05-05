package com.doubeye.callback.helper;

import com.doubeye.commons.utils.response.ResponseHelper;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * @author doubeye
 * 腾讯云回调助手
 */
public class TencentCallbackRespondHelper {
    private static final String SUCCESS;

    static {
        JSONObject successObject = new JSONObject();
        successObject.put("code", 0);
        successObject.put("message", "成功");
        SUCCESS = successObject.toString();
    }
    /**
     * 返回成功标记
     * @param response HttpServletResponse对象
     */
    public static void success(HttpServletResponse response) {

        ResponseHelper.send(response, SUCCESS);
    }
}
