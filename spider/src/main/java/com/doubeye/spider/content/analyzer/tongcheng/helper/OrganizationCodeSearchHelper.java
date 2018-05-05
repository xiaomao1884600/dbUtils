package com.doubeye.spider.content.analyzer.tongcheng.helper;


import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.file.FileUtils;

import com.doubeye.commons.utils.net.UrlContentGetter;
import com.doubeye.spider.bean.CompanyBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 用来获得公司组织机构代码的助手类
 */
public class OrganizationCodeSearchHelper {
    private static Logger logger = LogManager.getLogger(OrganizationCodeSearchHelper.class);

    /**
     * 通过58同城链接获得企业的组织结构代码
     * 注意：58同城查询时需要公司名精确匹配
     * @param organizationName 组织机构名称
     * @return 组织机构代码
     */
    public static String getOrganizationCodeByTongCheng(String organizationName) {
        try {
            organizationName = replaceIllegalCharacters(organizationName);
            JSONObject content = JSONObject.fromObject(UrlContentGetter.getHtmlCode(SEARCH_URL_BY_58_TONGCHENG
                    + organizationName, "utf-8"));
            if (content.containsKey("orgNumber")) {
                return content.getString("orgNumber");
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static JSONObject getOrganizationInfoByTongCheng(String organizationName) {
        organizationName = replaceIllegalCharacters(organizationName);
        return JSONObject.fromObject(UrlContentGetter.getHtmlCode(SEARCH_URL_BY_58_TONGCHENG
                + organizationName, "utf-8"));
    }

    public static void fillOrganizationInfoByTongCheng(String organizationName, CompanyBean bean) {
        JSONObject companyInfo = OrganizationCodeSearchHelper.getOrganizationInfoByTongCheng(bean.getCompany());
        if (!companyInfo.isEmpty()) {
            bean.setOrganizationCode(companyInfo.getString(PROPERTY_NAME_ORGANIZATION_CODE));
            bean.setRegisteredCapital(companyInfo.getString(PROPERTY_NAME_REGISTERED_CAPITAL));
            bean.setEstablishedTime(companyInfo.getString(PROPERTY_NAME_ESTABLISHED_TIME));
            bean.setStatus(companyInfo.getString(PROPERTY_NAME_STATUS));
        }
    }
    private static final String PROPERTY_NAME_ORGANIZATION_CODE = "orgNumber";
    private static final String PROPERTY_NAME_REGISTERED_CAPITAL = "regCapital";
    private static final String PROPERTY_NAME_ESTABLISHED_TIME = "estiblishDate";
    private static final String PROPERTY_NAME_STATUS = "operatingStatus";

    /**
     * 通过天眼来查询组织机构代码
     * 注意：天眼查能够进行模糊查询，天眼查本身将返回多条记录，但此方法只返回第一条结果
     * 此条结果为天眼查认为近似度最高的记录
     * 注意：同时需要注意对于部分抓取的公司名，由...结尾的公司，取药去掉
     * @param organizationName
     * @return
     */
    public static JSONObject getOrganizationCodeByTianYanCha(String organizationName) throws IOException, InterruptedException {
        organizationName = replaceIllegalCharacters(organizationName);
        //Map<String, String> header = UrlContentGetter.getTianYanChaHeader("aliyungf_tc=AQAAABNM2y9KHAAALOz3ckcMlLfjnbUj; csrfToken=NY-bGj-ApG8BrXzjXo27a5aP; TYCID=39cd51008ef811e7a257f9305d665c22; uccid=50b4cf4ccc79d508d43a70acd0a633c8; ssuid=3992077791; jsid=SEM-BAIDU-JP-SY-000035; bannerFlag=true; token=597a93261e5b44cb80eacc4bcadf911a; _utm=c8e32498c0164ddbaa79d8262bb6f708; tyc-user-info=%257B%2522token%2522%253A%2522eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEwNzI5MSwiZXhwIjoxNTIwNjU5MjkxfQ.WfDtsKup7eARftjF_Je5vd08gYSIoDQpGaFkE9smpmRMjU9OiKZI8_2hEbUY6qxQjNbRsjcEx1oHOroVepiQfw%2522%252C%2522integrity%2522%253A%25220%2525%2522%252C%2522state%2522%253A%25220%2522%252C%2522vnum%2522%253A%25220%2522%252C%2522onum%2522%253A%25220%2522%252C%2522mobile%2522%253A%252213581572543%2522%257D; auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEwNzI5MSwiZXhwIjoxNTIwNjU5MjkxfQ.WfDtsKup7eARftjF_Je5vd08gYSIoDQpGaFkE9smpmRMjU9OiKZI8_2hEbUY6qxQjNbRsjcEx1oHOroVepiQfw; _csrf=pMDB9QBvhL+sIuzvlJjZFA==; OA=LeaQu/qj8crONtTHBYNclnSDXkF2sr4qzYpRPTrE0L1aYttwCding6yhN/XqwJFHsnKBOBC0Piaik/tPTk7XaEv14JtrouhumL0FKgMr+gCcb18+aRSqBMZphNkDuIk5; _csrf_bk=4c4656ecc902eaaf2fd914aea2b75220; Hm_lvt_e92c8d65d92d534b0fc290df538b4758=1504258287,1504606052,1504675396; Hm_lpvt_e92c8d65d92d534b0fc290df538b4758=1505117078");

        //Map<String, String> header = UrlContentGetter.getTianYanChaHeader("auth_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzU4MTU3MjU0MyIsImlhdCI6MTUwNTEwNzI5MSwiZXhwIjoxNTIwNjU5MjkxfQ.WfDtsKup7eARftjF_Je5vd08gYSIoDQpGaFkE9smpmRMjU9OiKZI8_2hEbUY6qxQjNbRsjcEx1oHOroVepiQfw");
        Map<String, String> header = UrlContentGetter.getTianYanChaHeader("");
        String cookie = StringUtils.isEmpty(header.get("Cookie")) ? "" : header.get("Cookie");
        //Map<String, String> header = UrlContentGetter.getTianYanChaHeader("");
        if (StringUtils.isEmpty(header.get("Cookie"))) {
            cookie = TianYanChaCompanyListAnalyzer.login();
            header = UrlContentGetter.getTianYanChaHeader(cookie);
        }

        String content = UrlContentGetter.getHtmlByJsoup(String.format(SEARCH_URL_BY_TIANYANCHA_TEMPLATE, organizationName), header);
        //System.out.println(content);
        if (content.contains("我们只是确认一下你不是机器人")) {
            System.err.println("天眼查校验");
        }

        if (!StringUtils.isEmpty(content) && content.contains(TianYanChaCompanyListAnalyzer.CLASS_NAME_LOGIN_BUTTON)) {
            cookie = TianYanChaCompanyListAnalyzer.login();
            header = UrlContentGetter.getTianYanChaHeader(cookie);
            content = UrlContentGetter.getHtml(String.format(SEARCH_URL_BY_TIANYANCHA_TEMPLATE, organizationName), header);
            if (content.contains("我们只是确认一下你不是机器人")) {
                System.err.println("天眼查校验");
            }
            System.out.println(content);
        }

        TianYanChaCompanyListAnalyzer analyzer = new TianYanChaCompanyListAnalyzer();
        //System.out.println("          cokie " + cookie);
        analyzer.setCookie(cookie);
        analyzer.setContent(content);
        JSONArray companies = analyzer.doAnalyze();
        if (companies.size() > 0) {
            return companies.getJSONObject(0);
        } else {
            return new JSONObject();
        }

    }

    /**
     * 58同城查询url
     */
    private static final String SEARCH_URL_BY_58_TONGCHENG = "http://qy.58.com/ajax/getBusinessInfo?userName=";
    /**
     * 天眼查url模板，第一个参数为公司名称
     */
    private static final String SEARCH_URL_BY_TIANYANCHA_TEMPLATE = "https://www.tianyancha.com/search?key=%s&checkFrom=searchBox";

    public static List<Map<String, String>> getOrganizationCodesBy58TongCheng(List<String> companyNames) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        logger.trace("there are " + companyNames.size() + " companies");
        long index = 0;
        for (String companyName : companyNames) {
            index ++;
            Map<String, String> company = new HashMap<>();
            company.put("companyName", companyName);
            String organizationCode = getOrganizationCodeByTongCheng(companyName.replace(" ", ""));
            organizationCode = (StringUtils.isEmpty(organizationCode) ? companyName : organizationCode);
            company.put("organizationCode", organizationCode);
            result.add(company);
            logger.trace(index + " " + companyName + " " + organizationCode);
            builder.append(company).append(" ").append(organizationCode).append("\n");
        }
        FileUtils.toFile("d:/backupCompany.txt", builder);
        return result;
    }

    private static String replaceIllegalCharacters(String companyName) {
        String result = companyName;
        for(String characters : ILLEGAL_CHARACTERS) {
            result = result.replace(characters, "");
        }
        return result;
    }

    private static String[] ILLEGAL_CHARACTERS = new String[] {
            " ",
            "…"
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> companyNames = CollectionUtils.loadFromFile("d:/spider/job51/company_names_11_01.txt");
        JSONArray results = new JSONArray();
        int i = 0;
        for (String companyName : companyNames) {
            i ++;
            try {
                JSONObject companyObject = getOrganizationInfoByTongCheng(companyName);
                if (companyObject.getInt("status") == 0) {
                    companyObject.put("originCompanyName", companyName);
                    results.add(companyObject);
                }
                if (i % 1000 == 0) {
                    System.out.println(i);
                }
            } catch (Exception e) {
                //
            }
        }
        FileUtils.toFile("d:/spider/job51/company_names_11_01_addedByTongCheng.txt", results);
    }
}
