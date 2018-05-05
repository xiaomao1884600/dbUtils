package com.doubeye.commons.utils.test;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * @author doubeye
 * @version 1.0.0
 * 为了在命令行下初始化环境参数，在生成环境中干慎用
 */
public class ApplicationContextInitiator {
    /**
     * 初始化
     */
    public static void init() {
        JSONObject config = JSONUtils.getJsonObjectFromFile(new File(CONFIGURATION_FILE_NAME));
        GlobalApplicationContext.init(config);
    }

    /**
     * 配置文件路径及文件名
     */
    private static final String CONFIGURATION_FILE_NAME =  "d:/workCode/dbUtils/web-apps/src/main/config/init.json";

}
