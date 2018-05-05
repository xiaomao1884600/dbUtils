package com.doubeye.spider.content.analyzer.job51.dictionary;

import com.doubeye.commons.utils.json.mapper.JSONMapperHelper;
import com.doubeye.commons.utils.json.mapper.NameMapConfig;
import net.sf.json.JSONArray;

import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 前程无忧其他字典信息助手
 * 所有这些字典数据通过以下url获得
 * http://js.51jobcdn.com/in/js/2016/merge_data_c.js
 */
public class Job51OtherDirectoryHelper {
    private static final String EXPERIENCE = "[\n" +
            "{'k':'1','v':'在读学生'},\n" +
            "{'k':'2','v':'应届毕业生'},\n" +
            "{'k':'3','v':'1年'},\n" +
            "{'k':'4','v':'2年'},\n" +
            "{'k':'5','v':'3-4年'},\n" +
            "{'k':'6','v':'5-7年'},\n" +
            "{'k':'7','v':'8-9年'},\n" +
            "{'k':'8','v':'10年以上'}\n" +
            "]";
    private static final String SALARY = "[\n"+
            "{'k':'01','v':'2万以下'},\n"+
            "{'k':'02','v':'2-3万'},\n"+
            "{'k':'03','v':'3-4万'},\n"+
            "{'k':'04','v':'4-5万'},\n"+
            "{'k':'05','v':'5-6万'},\n"+
            "{'k':'06','v':'6-8万'},\n"+
            "{'k':'07','v':'8-10万'},\n"+
            "{'k':'08','v':'10-15万'},\n"+
            "{'k':'13','v':'15-20万'},\n"+
            "{'k':'09','v':'20-30万'},\n"+
            "{'k':'14','v':'30-40万'},\n"+
            "{'k':'10','v':'40-50万'},\n"+
            "{'k':'15','v':'50-60万'},\n"+
            "{'k':'11','v':'60-80万'},\n"+
            "{'k':'16','v':'80-100万'},\n"+
            "{'k':'12','v':'100万以上'}\n"+
            "]";

    private static final String JOB_TERM = "[\n" +
            "{'k':'0','v':'全职'},\n" +
            "{'k':'1','v':'兼职'},\n" +
            "{'k':'2','v':'实习'},\n" +
            "{'k':'3','v':'全/兼职'}\n" +
            "]";

    private static final String DEGREE = "[\n" +
            "{'k':'1','v':'初中及以下'},\n" +
            "{'k':'2','v':'高中'},\n" +
            "{'k':'3','v':'中技'},\n" +
            "{'k':'4','v':'中专'},\n" +
            "{'k':'5','v':'大专'},\n" +
            "{'k':'6','v':'本科'},\n" +
            "{'k':'7','v':'硕士'},\n" +
            "{'k':'8','v':'博士'},\n" +
            "{'k':'-1','v':'MBA'}\n" +
            "]";

    private static final String COMPANY_SIZE = "[\n" +
            "{'k':'1','v':'少于50人'},\n" +
            "{'k':'2','v':'50-150人'},\n" +
            "{'k':'3','v':'150-500人'},\n" +
            "{'k':'4','v':'500-1000人'},\n" +
            "{'k':'5','v':'1000-5000人'},\n" +
            "{'k':'6','v':'5000-10000人'},\n" +
            "{'k':'7','v':'10000人以上'}\n" +
            "]";

    private static final String COMPANY_TYPE = "[\n"+
            "{'k':'01','v':'外资（欧美）'},\n"+
            "{'k':'02','v':'外资（非欧美）'},\n"+
            "{'k':'03','v':'合资'},\n"+
            "{'k':'05','v':'国企'},\n"+
            "{'k':'06','v':'民营公司'},\n"+
            "{'k':'13','v':'上市公司'},\n"+
            "{'k':'14','v':'创业公司'},\n"+
            "{'k':'07','v':'外企代表处'},\n"+
            "{'k':'09','v':'政府机关'},\n"+
            "{'k':'10','v':'事业单位'},\n"+
            "{'k':'11','v':'非营利机构'}\n"+
            "]";


    private static NameMapConfig getFieldMapper() {
        NameMapConfig config = new NameMapConfig();
        config.addNameMap("k", "id");
        config.addNameMap("v", "value");
        return config;
    }

    public static void main(String[] args) {
        NameMapConfig mapConfig = getFieldMapper();
        JSONArray experiences = JSONMapperHelper.doNameMapper(JSONArray.fromObject(EXPERIENCE), mapConfig);
        System.out.println("EXPERIENCE-------------");
        System.out.println(experiences.toString());

        JSONArray salaries = JSONMapperHelper.doNameMapper(JSONArray.fromObject(SALARY), mapConfig);
        System.out.println("SALARY-------------");
        System.out.println(salaries.toString());

        JSONArray degrees = JSONMapperHelper.doNameMapper(JSONArray.fromObject(DEGREE), mapConfig);
        System.out.println("DEGREE-------------");
        System.out.println(degrees.toString());

        JSONArray companySizes = JSONMapperHelper.doNameMapper(JSONArray.fromObject(COMPANY_SIZE), mapConfig);
        System.out.println("COMPANY SIZE-------------");
        System.out.println(companySizes.toString());

        JSONArray companyTypes = JSONMapperHelper.doNameMapper(JSONArray.fromObject(COMPANY_TYPE), mapConfig);
        System.out.println("COMPANY TYPE-------------");
        System.out.println(companyTypes.toString());

        JSONArray jobTerms = JSONMapperHelper.doNameMapper(JSONArray.fromObject(JOB_TERM), mapConfig);
        System.out.println("JOB TERM-------------");
        System.out.println(jobTerms.toString());
    }
}
