package com.doubeye.spider.content.analyzer.lagou.dictionary;

import com.doubeye.commons.utils.automation.classGenerator.ConstantClassHelper;
import com.doubeye.commons.utils.automation.classGenerator.ConstantFieldHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 拉勾网字典常量助手
 */
public class LaGouDictionaryConstantHelper {
    private static final String CLASS_NAME = "LaGouDictionary";
    private static final String PACKAGE_NAME = "com.doubeye.spider.contentAnalyzer.lagou.dictionary";
    private static final String FILED_NAME_PREFIX = "LAGOU_";
    private static final String CLASS_TYPE_JSON_ARRAY = "JSONArray";
    private static final String CORE_PROJECT_SOURCE_ROOT = "D:/workcode/dbUtils/spider/src/main/java";


    /**
     * 职位类型
     */
    private static final String JOB_TYPES = LaGouDirectoryJobTypeDictionaryHelper.getAllJobTypes().toString();;

    private static final String WRAPPER_FUNCTION = "JSONArray.fromObject";

    public static void main(String[] args) throws SQLException, IOException {
        ApplicationContextInitiator.init();
        ConstantClassHelper constantClassHelper = new ConstantClassHelper();
        constantClassHelper.setPackageName(PACKAGE_NAME);
        constantClassHelper.setAuthor("doubeye");
        constantClassHelper.setVersion("1.0.0");
        constantClassHelper.setClassComment("定义拉勾网中所有字典的常量");
        constantClassHelper.setClassName(CLASS_NAME);
        constantClassHelper.setProjectSourceRoot(CORE_PROJECT_SOURCE_ROOT);

        constantClassHelper.addImport("net.sf.json.JSONArray");

        //职位类型
        constantClassHelper.addField(ConstantFieldHelper.getField("职位类型", FILED_NAME_PREFIX + "JOB_TYPE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(JOB_TYPES).toString()));
        constantClassHelper.generateClassContent();
    }
}
