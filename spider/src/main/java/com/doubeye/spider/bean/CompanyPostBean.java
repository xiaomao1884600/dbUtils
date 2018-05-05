package com.doubeye.spider.bean;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author doubeye
 * @version 1.0.0
 * 企业招聘职位Bean
 */
public class CompanyPostBean {
    private String company;
    private String city;
    private String post;
    private String companyUrl;

    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    private static final String SQL_INSERT = "INSERT INTO %s(company_name, contacts, mobile, phone, email, address, url, added_time, company_url) VALUES" +
            "('([{company}])', '([{contacts}])', '([{mobile}])', '([{phone}])', '([{email}])', '([{address}])', '([{realUrl}])', '([{date}])', '([{companyUrl}])')";

    private static void importCompanyPost() throws Exception {
        ApplicationContextInitiator.init();
        String tableName = "spider_dict_company_chinahr_init";
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encryptKey);

        List<String> content = CollectionUtils.loadFromFile("D:\\spider\\chinaHr\\companies_09_19.txt");
        System.out.println(content.size());
        SQLExecutor.execute(conn, "TRUNCATE TABLE " + tableName);
        String sqlTemplate = String.format(SQL_INSERT, tableName);
        for (String aContent : content) {
            //CompanyPostBean bean = new CompanyPostBean();
            //RefactorUtils.fillByJSON(bean, JSONObject.fromObject(aContent));
            JSONObject bean = JSONObject.fromObject(aContent);
            String sql = StringUtils.format(sqlTemplate, JSONObject.fromObject(bean));
            try {
                SQLExecutor.execute(conn, sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage() + "  " + sql);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        importCompanyPost();
    }
}
