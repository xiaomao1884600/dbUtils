package com.doubeye.spider.bean;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * @version 1.1.2
 * 公司bean
 * @history
 *  1.1.0 扩展联系人，移动电话，固话，email，公司地址，网址，加入时间和阅读标记
 *  1.1.1 宽展所招聘职位，所在城市
 *  1.1.2 按JOB公司库扩展信息
 */
@SuppressWarnings("unused | WeakerAccess")
public class CompanyBean {
    /**
     * 自增id
     */
    private long id;
    /**
     * 公司名称
     */
    private String company;
    /**
     * 组织机构代码
     */
    private String organizationCode;
    /**
     * 联系人
     */
    private String contacts;
    /**
     * 移动电话
     */
    private String mobile;
    /**
     * 固话
     */
    private String phone;
    /**
     * email
     */
    private String email;
    /**
     * 公司地址
     */
    private String address;
    /**
     * 网址
     */
    private String realUrl;
    /**
     * 添加时间
     */
    private String addedTime;
    /**
     * 阅读标记
     */
    private int flag;
    /**
     * 公司在招聘网站的url
     */
    private String companyUrl;

    /**
     * 公司性质
     */
    private String property;

    /**
     * 公司规模
     */
    private String size;

    /**
     * 行业
     */
    private String industry;
    /**
     * 成立时间
     */
    private String establishedTime;
    /**
     * 注册资金
     */
    private String registeredCapital;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 公司状态
     */
    private String status;

    /**
     * 联系电话图片地址，58同城使用
     */
    private String phoneImageUrl;

    private String originCompanyName;


    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRealUrl() {
        return realUrl;
    }

    public void setRealUrl(String realUrl) {
        this.realUrl = realUrl;
    }

    public String getOriginCompanyName() {
        return originCompanyName;
    }

    public void setOriginCompanyName(String originCompanyName) {
        this.originCompanyName = originCompanyName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstablishedTime() {
        return establishedTime;
    }

    public void setPhoneImageUrl(String phoneImageUrl) {
        this.phoneImageUrl = phoneImageUrl;
    }

    public String getPhoneImageUrl() {
        return phoneImageUrl;
    }

    public void setEstablishedTime(String establishedTime) {
        this.establishedTime = establishedTime;
    }

    private static final String SQL_INSERT = "INSERT INTO spider_dict_company_v2_job51_20171102_add_58page_job_info(organization_code, company_name, contacts, mobile, phone, email, address, url, added_time, flag, company_url, origin_company_name) VALUES" +
            "(substr('([{company}])', 1, 10), '([{company}])', '([{contacts}])', '([{mobile}])', '([{phone}])', '([{email}])', '([{address}])', '([{realUrl}])', '([{addedTime}])', ([{flag}]), '([{companyUrl}])', '([{originCompanyName}])')";

    private static final String SQL_SELECT_COMPANY_WITH_PAGINATION = "SELECT c.id, company_name, organization_code, official_site, property,size,industry,registered_capital,established_time,address,introduction,status FROM t_company c LEFT OUTER JOIN t_company_info i on c.id = i.id LIMIT %s, %s";

    public static JSONArray getCompanies(Connection conn, int start, int size) throws SQLException {
        //TODO 对分页进行封装
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_COMPANY_WITH_PAGINATION, start >= 1 ? start - 1 : 0, size));
    }
    private static final String SQL_SELECT_COMPANY_WITH_COMPANY_NAME = "SELECT c.id, company_name, organization_code, official_site, property,size,industry,registered_capital,established_time,address,introduction,status FROM t_company c LEFT OUTER JOIN t_company_info i on c.id = i.id WHERE company_name = '%s'";
    public static JSONArray getCompanies(Connection conn, String companyName) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_COMPANY_WITH_COMPANY_NAME, companyName));
    }

    private static void importCompany() throws Exception {
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encrytKey);
        List<String> parameterNames = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(SQLExecutor.getSQLFromTemplate(SQL_INSERT, parameterNames));

        //List<String> content = CollectionUtils.loadFromFile("d:/spider/chinaHr/companies_09_19.txt");
        List<String> content = CollectionUtils.loadFromFile("d:/spider/job51/company_names_11_01_addedByTongChengPage.txt");
        System.out.println(content.size());
        SQLExecutor.execute(conn, "TRUNCATE TABLE spider_dict_company_v2_job51_20171102_add_58page_job_info");
        for (String aContent : content) {
            CompanyBean bean = new CompanyBean();
            RefactorUtils.fillByJSON(bean, JSONObject.fromObject(aContent));
            String sql = StringUtils.format(SQL_INSERT, JSONObject.fromObject(bean));
            try {
                SQLExecutor.execute(conn, sql);
            } catch (Exception e) {
                System.out.println(sql + "@@@" +e.getMessage());
            }
        }
    }



    public static void main(String[] args) throws Exception {
        importCompany();
    }


}
