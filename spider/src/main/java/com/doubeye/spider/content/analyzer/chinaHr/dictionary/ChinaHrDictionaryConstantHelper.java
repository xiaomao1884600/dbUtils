package com.doubeye.spider.content.analyzer.chinaHr.dictionary;

import com.doubeye.commons.utils.automation.classGenerator.ConstantClassHelper;
import com.doubeye.commons.utils.automation.classGenerator.ConstantField;
import com.doubeye.commons.utils.automation.classGenerator.ConstantFieldHelper;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

public class ChinaHrDictionaryConstantHelper {

    private static final String CLASS_NAME = "ChinaHrDictionary";
    private static final String PACKAGE_NAME = "com.doubeye.experiments.spider.contentAnalyzer.chinaHr.dictionary";
    private static final String FILED_NAME_PREFIX = "CHINA_HR_";
    private static final String CLASS_TYPE_JSON_ARRAY = "JSONArray";
    private static final String CORE_PROJECT_SOURCE_ROOT = "D:/workcode/dbUtils/experiments/src/main/java";

    /**
     * 学历
     */
    private static final String DEGREE = "{\n" +
            "\t\t0: \"不限\",\n" +
            "\t\t1: \"博士\",\n" +
            "\t\t2: \"EMBA\",\n" +
            "\t\t3: \"MBA\",\n" +
            "\t\t4: \"硕士\",\n" +
            "\t\t5: \"本科\",\n" +
            "\t\t6: \"大专\",\n" +
            "\t\t7: \"高职\",\n" +
            "\t\t8: \"高中\",\n" +
            "\t\t9: \"职高\",\n" +
            "\t\t10: \"中专\",\n" +
            "\t\t11: \"中职\",\n" +
            "\t\t12: \"中技\",\n" +
            "\t\t13: \"初中\",\n" +
            "\t\t14: \"其他\"\n" +
            "\t}";
    /**
     * 公司类型
     */
    private static final String COMPANY_TYPE = "{\n" +
            "\t\t0:\"全部\",\n" +
            "\t\t1: \"外商独资\",\n" +
            "\t\t2: \"合资\",\n" +
            "\t\t3: \"上市公司\",\n" +
            "\t\t4: \"国企\",\n" +
            "\t\t5: \"国家机关\",\n" +
            "\t\t6: \"事业单位\",\n" +
            "\t\t7: \"民企/私企\",\n" +
            "\t\t8: \"代表处\",\n" +
            "\t\t9:\"非赢利组织\",\n" +
            "\t\t10: \"股份制\",\n" +
            "\t\t11: \"其他\"\n" +
            "\t}";
    /**
     * 公司规模
     */
    private static final String COMPANY_SIZE = "{\n" +
            "\t\t0:\"全部\",\n" +
            "\t\t1: \"20人以下\",\n" +
            "\t\t2: \"21-50人\",\n" +
            "\t\t3: \"51-100人\",\n" +
            "\t\t4: \"101-300人\",\n" +
            "\t\t5: \"301-500人\",\n" +
            "\t\t6: \"500人以上\"\n" +
            "\t}";
    /**
     * 经验
     */
    private static final String EXPERIENCE = "{\n" +
            "\t\t0: \"不限\",\n" +
            "\t\t1: \"应届毕业生\",\n" +
            "\t\t2: \"1年以下\",\n" +
            "\t\t3: \"1-3年\",\n" +
            "\t\t4: \"3-5年\",\n" +
            "\t\t5: \"5-7年\",\n" +
            "\t\t6: \"7-10年\",\n" +
            "\t\t7: \"10年以上\"\n" +
            "\t}";
    private static final String JOB_PROPERTY = "{\n" +
            "\t\t0: \"全部\",\n" +
            "\t\t1: \"全职\",\n" +
            "\t\t2: \"兼职\",\n" +
            "\t\t3: \"实习\"\n" +
            "\t}";
    /**
     * 省和直辖市一级的城市
     */
    private static final String CITY = getProvinceDictionary().toString();
    private static final String WRAPPER_FUNCTION = "JSONArray.fromObject";

    public static void main(String[] args) throws SQLException, IOException {
        ApplicationContextInitiator.init();
        ConstantClassHelper constantClassHelper = new ConstantClassHelper();
        constantClassHelper.setPackageName(PACKAGE_NAME);
        constantClassHelper.setAuthor("doubeye");
        constantClassHelper.setVersion("1.0.0");
        constantClassHelper.setClassComment("定义中华英才网中所有字典的常量");
        constantClassHelper.setClassName(CLASS_NAME);
        constantClassHelper.setProjectSourceRoot(CORE_PROJECT_SOURCE_ROOT);

        constantClassHelper.addImport("net.sf.json.JSONArray");

        //城市
        constantClassHelper.addField(ConstantFieldHelper.getField("城市",
                FILED_NAME_PREFIX + "CITY", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, CITY));
        //学历
        constantClassHelper.addField(ConstantFieldHelper.getField("学历", FILED_NAME_PREFIX + "DEGREE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONUtils.objectToArray(JSONObject.fromObject(DEGREE), "id", "name").toString()));
        //公司类型
        constantClassHelper.addField(ConstantFieldHelper.getField("公司类型", FILED_NAME_PREFIX + "COMPANY_TYPE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONUtils.objectToArray(JSONObject.fromObject(COMPANY_TYPE), "id", "name").toString()));
        //公司规模
        constantClassHelper.addField(ConstantFieldHelper.getField("公司规模", FILED_NAME_PREFIX + "COMPANY_SIZE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONUtils.objectToArray(JSONObject.fromObject(COMPANY_SIZE), "id", "name").toString()));
        //经验
        constantClassHelper.addField(ConstantFieldHelper.getField("经验", FILED_NAME_PREFIX + "EXPERIENCE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONUtils.objectToArray(JSONObject.fromObject(EXPERIENCE), "id", "name").toString()));
        //工作性质
        constantClassHelper.addField(ConstantFieldHelper.getField("经验", FILED_NAME_PREFIX + "JOB_PROPERTY", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONUtils.objectToArray(JSONObject.fromObject(JOB_PROPERTY), "id", "name").toString()));

        constantClassHelper.generateClassContent();
    }

    private static JSONArray getProvinceDictionary() {
        JSONArray result = new JSONArray();
            /* 文件需要根据实际的情况来处理，由于是字典类，该文件没有放到资源中
            ps 此文件内容由http://st01.chrstatic.com/themes/pcchinahr/js/citys.js获得，为exports.base的值
             */
        try {
            JSONArray allCities = JSONUtils.getJsonArrayFromFile(new java.io.File("d:/chinaHrCityJSON.txt"), "utf-8");
            for (int i = 0; i < allCities.size(); i++) {
                JSONObject city = allCities.getJSONObject(i);
                if (city.has("id")) {
                    JSONObject province = new JSONObject();
                    province.put("id", city.getString("id"));
                    province.put("name", city.getString("name"));
                    result.add(province);
                }
            }
        } catch (IOException e) {
            //
        }
        return result;
    }

}

