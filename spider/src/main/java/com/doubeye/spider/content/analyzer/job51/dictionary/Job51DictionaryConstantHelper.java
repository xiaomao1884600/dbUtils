package com.doubeye.spider.content.analyzer.job51.dictionary;

import com.doubeye.commons.utils.automation.classGenerator.ConstantClassHelper;
import com.doubeye.commons.utils.automation.classGenerator.ConstantFieldHelper;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author doubeye
 * @history 1.0.0
 * 前程无忧常量助手
 */
public class Job51DictionaryConstantHelper {

    private static final String CLASS_NAME = "Job51Dictionary";
    private static final String PACKAGE_NAME = "com.doubeye.spider.contentAnalyzer.job51.dictionary";
    private static final String FILED_NAME_PREFIX = "JOB51_";
    private static final String CLASS_TYPE_JSON_ARRAY = "JSONArray";
    private static final String CORE_PROJECT_SOURCE_ROOT = "D:/workcode/dbUtils/spider/src/main/java";

    /**
     * 学历
     */
    private static final String DEGREE = "[{\"id\":\"1\",\"value\":\"初中及以下\"},{\"id\":\"2\",\"value\":\"高中\"},{\"id\":\"3\",\"value\":\"中技\"},{\"id\":\"4\",\"value\":\"中专\"},{\"id\":\"5\",\"value\":\"大专\"},{\"id\":\"6\",\"value\":\"本科\"},{\"id\":\"7\",\"value\":\"硕士\"},{\"id\":\"8\",\"value\":\"博士\"},{\"id\":\"-1\",\"value\":\"MBA\"}]";
    /**
     * 公司类型
     */
    private static final String COMPANY_TYPE = "[{\"id\":\"01\",\"value\":\"外资（欧美）\"},{\"id\":\"02\",\"value\":\"外资（非欧美）\"},{\"id\":\"03\",\"value\":\"合资\"},{\"id\":\"05\",\"value\":\"国企\"},{\"id\":\"06\",\"value\":\"民营公司\"},{\"id\":\"13\",\"value\":\"上市公司\"},{\"id\":\"14\",\"value\":\"创业公司\"},{\"id\":\"07\",\"value\":\"外企代表处\"},{\"id\":\"09\",\"value\":\"政府机关\"},{\"id\":\"10\",\"value\":\"事业单位\"},{\"id\":\"11\",\"value\":\"非营利机构\"}]";
    /**
     * 公司规模
     */
    private static final String COMPANY_SIZE = "[{\"id\":\"1\",\"value\":\"少于50人\"},{\"id\":\"2\",\"value\":\"50-150人\"},{\"id\":\"3\",\"value\":\"150-500人\"},{\"id\":\"4\",\"value\":\"500-1000人\"},{\"id\":\"5\",\"value\":\"1000-5000人\"},{\"id\":\"6\",\"value\":\"5000-10000人\"},{\"id\":\"7\",\"value\":\"10000人以上\"}]";
    /**
     * 经验
     */
    private static final String EXPERIENCE = "[{\"id\":\"1\",\"value\":\"在读学生\"},{\"id\":\"2\",\"value\":\"应届毕业生\"},{\"id\":\"3\",\"value\":\"1年\"},{\"id\":\"4\",\"value\":\"2年\"},{\"id\":\"5\",\"value\":\"3-4年\"},{\"id\":\"6\",\"value\":\"5-7年\"},{\"id\":\"7\",\"value\":\"8-9年\"},{\"id\":\"8\",\"value\":\"10年以上\"}]";
    /**
     * 工作类型
     */
    private static final String JOB_PROPERTY = "[{\"id\":\"0\",\"value\":\"全职\"},{\"id\":\"1\",\"value\":\"兼职\"},{\"id\":\"2\",\"value\":\"实习\"},{\"id\":\"3\",\"value\":\"全/兼职\"}]";
    /**
     * 省和直辖市一级的城市
     */
    private static final String CITY = ProvinceDictionaryHelper.getAllProvinces().toString();

    private static final String JOB_TYPES = JobTypeDictionaryHelper.getAllJobTypes().toString();

    private static final String WRAPPER_FUNCTION = "JSONArray.fromObject";

    public static void main(String[] args) throws SQLException, IOException {
        ApplicationContextInitiator.init();
        ConstantClassHelper constantClassHelper = new ConstantClassHelper();
        constantClassHelper.setPackageName(PACKAGE_NAME);
        constantClassHelper.setAuthor("doubeye");
        constantClassHelper.setVersion("1.0.0");
        constantClassHelper.setClassComment("定义前程无忧中所有字典的常量");
        constantClassHelper.setClassName(CLASS_NAME);
        constantClassHelper.setProjectSourceRoot(CORE_PROJECT_SOURCE_ROOT);

        constantClassHelper.addImport("net.sf.json.JSONArray");

        //城市
        constantClassHelper.addField(ConstantFieldHelper.getField("城市",
                FILED_NAME_PREFIX + "CITY", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, CITY));
        //学历
        constantClassHelper.addField(ConstantFieldHelper.getField("学历", FILED_NAME_PREFIX + "DEGREE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(DEGREE).toString()));
        //公司类型
        constantClassHelper.addField(ConstantFieldHelper.getField("公司类型", FILED_NAME_PREFIX + "COMPANY_TYPE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(COMPANY_TYPE).toString()));
        //公司规模
        constantClassHelper.addField(ConstantFieldHelper.getField("公司规模", FILED_NAME_PREFIX + "COMPANY_SIZE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(COMPANY_SIZE).toString()));
        //经验
        constantClassHelper.addField(ConstantFieldHelper.getField("经验", FILED_NAME_PREFIX + "EXPERIENCE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(EXPERIENCE).toString()));
        //工作性质
        constantClassHelper.addField(ConstantFieldHelper.getField("经验", FILED_NAME_PREFIX + "JOB_PROPERTY", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(JOB_PROPERTY).toString()));
        //职位类型
        constantClassHelper.addField(ConstantFieldHelper.getField("职位类型", FILED_NAME_PREFIX + "JOB_TYPE", CLASS_TYPE_JSON_ARRAY, WRAPPER_FUNCTION, JSONArray.fromObject(JOB_TYPES).toString()));
        constantClassHelper.generateClassContent();
    }
}
