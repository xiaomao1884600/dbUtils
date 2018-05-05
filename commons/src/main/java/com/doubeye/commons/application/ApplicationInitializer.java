package com.doubeye.commons.application;


import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;

/**
 * @author doubeye
 * @version 1.0.0
 * 应用初始化
 */
public class ApplicationInitializer extends HttpServlet{
    /**
     * 初始化应用服务
     * @throws ServletException Servlet异常
     */
    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        String fileName = context.getRealPath(CONFIG_FILE_NAME);
        File file = new File(fileName);
        JSONObject initConfig = JSONUtils.getJsonObjectFromFile(file);
        GlobalApplicationContext.init(initConfig);
    }

    /**
     * 初始化文件
     */
    private static final String CONFIG_FILE_NAME = "/config/init.json";
}
