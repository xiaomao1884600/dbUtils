package com.doubeye.spider.job.service;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.spider.bean.CompanyBean;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 公司查询服务
 */
@SuppressWarnings("unused")
public class CompanyService {
    /**
     * 获得公司列表
     * @param parameters 参数，如果没有指定公司名称，则需要指定分页信息，如未指定，则显示第一页的10条记录
     *                   参数包括以下内容
     *                   companyName 公司名称 可选
     *                   start 开始的记录数
     *                   size 返回的记录数
     * @return 公司列表
     * @throws Exception 异常
     */
    public JSONArray getCompanies(Map<String, String[]> parameters) throws Exception {
        //todo 考虑弄一个总页数
        String companyName = RequestHelper.getString(parameters, PROPERTY_NAME_COMPANY_NAME);
        int start = RequestHelper.getInt(parameters, RequestHelper.PROPERTY_NAME_PAGE_START, 1);
        int size = RequestHelper.getInt(parameters, RequestHelper.PROPERTY_NAME_PAGE_SIZE, 10);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        JSONArray companies;
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encryptKey)) {
            if (StringUtils.isEmpty(companyName)) {
                companies = CompanyBean.getCompanies(conn, start, size);
            } else {
                companies = CompanyBean.getCompanies(conn, companyName);
            }
            return companies;
        }
    }


    private static final String PROPERTY_NAME_COMPANY_NAME = "companyName";
}
