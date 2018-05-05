package com.doubeye.spider.content.analyzer.tongcheng.dictionary;

import com.doubeye.commons.utils.automation.classGenerator.ConstantClassHelper;
import com.doubeye.commons.utils.automation.classGenerator.ConstantFieldHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;


import java.io.IOException;
import java.sql.SQLException;

public class TongChengDictionaryConstantHelper {


    private static final String CLASS_NAME = "TongChengDictionary";
    private static final String PACKAGE_NAME = "com.doubeye.experiments.spider.contentAnalyzer.tongcheng.dictionary";
    private static final String FILED_NAME_PREFIX = "TONG_CHENG_";
    private static final String CLASS_TYPE_JSON_ARRAY = "JSONArray";
    private static final String CORE_PROJECT_SOURCE_ROOT = "D:/workcode/dbUtils/experiments/src/main/java";

    /**
     * 省和直辖市一级的城市
     */
    private static final JSONArray CITY = CityDictionaryHelper.getTongchengProvinces();
    private static final String JOB_TYPE = AllJobTypeDictionaryPageAnalyzer.getTongChengAllJobTypes().toString();

    private static final String WRAPPER_FUNCTION = "JSONArray.fromObject";

    public static void main(String[] args) throws SQLException, IOException {
        ApplicationContextInitiator.init();
        ConstantClassHelper constantClassHelper = new ConstantClassHelper();
        constantClassHelper.setPackageName(PACKAGE_NAME);
        constantClassHelper.setAuthor("doubeye");
        constantClassHelper.setVersion("1.0.0");
        constantClassHelper.setClassComment("定义58同城中所有字典的常量");
        constantClassHelper.setClassName(CLASS_NAME);
        constantClassHelper.setProjectSourceRoot(CORE_PROJECT_SOURCE_ROOT);

        constantClassHelper.addImport("net.sf.json.JSONArray");

        //城市
        constantClassHelper.addField(ConstantFieldHelper.getField("城市",
                FILED_NAME_PREFIX + "CITY", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, CITY.toString()));
        constantClassHelper.addField(ConstantFieldHelper.getField("职位类型", FILED_NAME_PREFIX + "JOB_TYPE",
                CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JOB_TYPE));

        constantClassHelper.generateClassContent();
    }



}
