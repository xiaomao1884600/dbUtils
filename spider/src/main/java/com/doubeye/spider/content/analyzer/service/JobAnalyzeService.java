package com.doubeye.spider.content.analyzer.service;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@SuppressWarnings("unused")
public class JobAnalyzeService {
    public JSONObject getProvinceResult(Map<String, String[]> parameters) throws Exception {
        String tableIdentifier = RequestHelper.getString(parameters, "tableIdentifier");
        String tableName = String.format(TABLE_NAME, tableIdentifier);
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encrytKey)) {
            JSONArray provinceResult = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_PROVINCE_ANALYZE_RESULT, tableName));
            JSONArray jobTypeResult = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_JOB_TYPE_ANALYZE_RESULT, tableName));
            JSONObject result = ResponseHelper.getSuccessObject();
            result.put("provinceResult", provinceResult);
            result.put("jobTypeResult", jobTypeResult);
            return result;
        }
    }

    public JSONObject getJobTypeProvinceAnalyzeResult(Map<String, String[]> parameters) throws Exception {
        String jobType = RequestHelper.getString(parameters, "id");
        String tableIdentifier = RequestHelper.getString(parameters, "tableIdentifier");
        String tableName = String.format(TABLE_NAME, tableIdentifier);
        return getDetailResult(jobType, tableName, SQL_SELECT_JOB_TYPE_PROVINCE_ANALYZE_RESULT, SQL_SELECT_EXPERIENCE_BY_JOB_TYPE, SQL_SELECT_DEGREE_BY_BY_JOB_TYPE, SQL_SELECT_SALARY_BY_JOB_TYPE);
    }

    public JSONObject getProvinceAndJobTypeAnalyzeResult(Map<String, String[]> parameters) throws Exception {
        String jobType = RequestHelper.getString(parameters, "jobType");
        String province = RequestHelper.getString(parameters, "province");
        String tableIdentifier = RequestHelper.getString(parameters, "tableIdentifier");
        String tableName = String.format(TABLE_NAME, tableIdentifier);

        JSONObject result = ResponseHelper.getSuccessObject();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encrytKey)) {
            JSONArray experience = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ANALYZE_BY_JOB_TYPE_AND_PROVINCE, "experience", tableName, jobType, province, "experience"));
            JSONArray degree = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ANALYZE_BY_JOB_TYPE_AND_PROVINCE, "degree", tableName, jobType, province, "degree"));
            JSONArray salary = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ANALYZE_BY_JOB_TYPE_AND_PROVINCE, "salary", tableName, jobType, province, "salary"));

            result.put("experience", experience);
            result.put("degree", degree);
            result.put("salary", salary);

            return result;
        }
    }


    public JSONObject getProvinceJobTypeAnalyzeResult(Map<String, String[]> parameters) throws Exception {
        String province = RequestHelper.getString(parameters, "id");
        String tableIdentifier = RequestHelper.getString(parameters, "tableIdentifier");
        String tableName = String.format(TABLE_NAME, tableIdentifier);;
        return getDetailResult(province, tableName, SQL_SELECT_PROVINCE_JOB_TYPE_ANALYZE_RESULT, SQL_SELECT_EXPERIENCE_BY_PROVINCE, SQL_SELECT_DEGREE_BY_PROVINCE, SQL_SELECT_SALARY_BY_PROVINCE);
    }

    private static JSONObject getDetailResult(String id, String tableName, String summarySql, String experienceSql, String degreeSql, String salarySql) throws Exception {
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encrytKey)) {
            JSONObject result = ResponseHelper.getSuccessObject();

            JSONArray detailResult = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(summarySql, tableName, id));
            JSONArray experience = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(experienceSql, tableName, id));
            JSONArray degree = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(degreeSql, tableName, id));
            JSONArray salary = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(salarySql, tableName, id));

            result.put("detailResult", detailResult);
            result.put("experience", experience);
            result.put("degree", degree);
            result.put("salary", salary);

            return result;
        }
    }




    private static final String SQL_SELECT_PROVINCE_ANALYZE_RESULT = "SELECT province, count(DISTINCT company) `companyCount`, count(DISTINCT post) `postCount`, sum(count) `enrollCount` FROM %s GROUP BY province ORDER BY `enrollCount` DESC";
    private static final String SQL_SELECT_JOB_TYPE_ANALYZE_RESULT = "SELECT job_type jobType, count(DISTINCT company) `companyCount`, count(DISTINCT post) `postCount`, sum(count) `enrollCount` FROM %s GROUP BY job_type ORDER BY `enrollCount` DESC";

    private static final String SQL_SELECT_JOB_TYPE_PROVINCE_ANALYZE_RESULT = "SELECT province id, count(DISTINCT company) `companyCount`, count(DISTINCT post) `postCount`, sum(count) `enrollCount` FROM %s WHERE job_type = '%s' GROUP BY province ORDER BY `enrollCount` DESC";
    private static final String SQL_SELECT_PROVINCE_JOB_TYPE_ANALYZE_RESULT = "SELECT job_type id, count(DISTINCT company) `companyCount`, count(DISTINCT post) `postCount`, sum(count) `enrollCount` FROM %s WHERE province = '%s' GROUP BY job_type ORDER BY `enrollCount` DESC";



    private static final String SQL_SELECT_EXPERIENCE_BY_PROVINCE = "SELECT experience, count(*) cnt FROM %s WHERE province = '%s' GROUP BY experience HAVING count(*) > 50";
    private static final String SQL_SELECT_DEGREE_BY_PROVINCE = "SELECT degree, count(*) cnt FROM %s WHERE province = '%s' GROUP BY degree";
    private static final String SQL_SELECT_SALARY_BY_PROVINCE = "SELECT salary, count(*) cnt FROM %s WHERE province = '%s' GROUP BY salary ORDER BY cnt DESC limit 10";

    private static final String SQL_SELECT_EXPERIENCE_BY_JOB_TYPE = "SELECT experience, count(*) cnt FROM %s WHERE job_type = '%s' GROUP BY experience HAVING count(*) > 50";
    private static final String SQL_SELECT_DEGREE_BY_BY_JOB_TYPE = "SELECT degree, count(*) cnt FROM %s WHERE job_type = '%s' GROUP BY degree";
    private static final String SQL_SELECT_SALARY_BY_JOB_TYPE = "SELECT salary, count(*) cnt FROM %s WHERE job_type = '%s' GROUP BY salary ORDER BY cnt DESC limit 10";


    private static final String SQL_SELECT_ANALYZE_BY_JOB_TYPE_AND_PROVINCE = "SELECT %s, count(*) cnt FROM %s WHERE job_type = '%s' AND province = '%s' GROUP BY %s ORDER BY cnt DESC limit 10";
    //private static final String SQL_SELECT_DEGREE_BY_JOB_TYPE_AND_PROVINCE = "SELECT degree, count(*) cnt FROM %s WHERE job_type = '%s' AND province = '%s' GROUP BY degree";
    //private static final String SQL_SELECT_SALARY_BY_JOB_TYPE_AND_PROVINCE = "SELECT salary, count(*) cnt FROM %s WHERE job_type = '%s' AND province = '%s' GROUP BY salary HAVING cnt > 100";

    private static final String TABLE_NAME = "spider_job_types_%s_0830";
}
