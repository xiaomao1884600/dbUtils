package com.doubeye.spider.content.analyzer;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class GeneralRecordSaver {
    protected Connection conn;
    private String recordFileName;
    private String recordFileCharset = "utf-8";
    private String tableName;
    private String errorLogFileName;

    private static Logger logger = LogManager.getLogger(GeneralRecordSaver.class);
    public void saveToDB() throws IOException, SQLException {

        JSONObject jobObject;
        ApplicationContextInitiator.init();
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(recordFileName), recordFileCharset);
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            SQLExecutor.execute(conn, String.format(SQL_TRUNCATE_JOB_TYPE, tableName));
            String line;
            long errorCount = 0;
            StringBuilder errorBuffer = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    jobObject = lineToObject(line);
                    doSave(jobObject);
                } catch (JSONException | SQLException e) {
                    errorCount ++;
                    String errorMessage = e.getMessage() + " " + line;
                    logger.error(errorMessage);
                    errorBuffer.append(errorMessage);
                    if (errorCount % 1000 == 0) {
                        writeErrorLog(errorBuffer);
                        errorBuffer = new StringBuilder();
                    }
                }
            }
            writeErrorLog(errorBuffer);
        }
    }

    protected void doSave(JSONObject jobObject) throws SQLException {
        String post = jobObject.getString("post");
        String company = jobObject.getString("company");
        String salary = jobObject.getString("salary");
        String city = jobObject.getString("city");
        String postedAt = jobObject.getString("postedAt");
        String jobType = jobObject.getString("jobType");
        String experience = jobObject.getString("experience");
        String degree = jobObject.getString("degree");
        String countString = jobObject.getString("count");

        String companyUrl = jobObject.getString("companyUrl");
        String postTypeFromPage = jobObject.getString("postTypeFromPage");
        String retrieveDate = jobObject.getString("date");
        String warning;
        if (jobObject.containsKey("warning")) {
            warning = jobObject.getString("warning");
        } else {
            warning = "";
        }
        int count;
        try {
            count = Integer.parseInt(countString);
        } catch (NumberFormatException e) {
            count = 1;
        }
        String province = jobObject.getString("province");
        SQLExecutor.execute(conn, String.format(SQL_INSERT_JOB_TYPE,
                tableName, post, company, salary, city, postedAt, jobType, experience, degree, count, province, companyUrl, postTypeFromPage, retrieveDate, warning));
    }

    protected JSONObject lineToObject(String line) {
        return JSONObject.fromObject(line);
    }

    private void writeErrorLog(StringBuilder errorBuffer) throws IOException {
        if (!StringUtils.isEmpty(errorLogFileName)) {
            FileUtils.toFile(errorLogFileName, errorBuffer, true);
        }
    }

    private static final String SQL_TRUNCATE_JOB_TYPE = "TRUNCATE TABLE %s";
    private static final String SQL_INSERT_JOB_TYPE = "INSERT INTO %s(post, company, salary, city, posted_at, job_type, experience, degree, count, province, company_url, post_type_from_page, retrieve_date, warning)" +
            " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')";

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRecordFileCharset(String recordFileCharset) {
        this.recordFileCharset = recordFileCharset;
    }

    public void setErrorLogFileName(String errorLogFileName) {
        this.errorLogFileName = errorLogFileName;
    }

    public static void main(String[] args) throws Exception {

        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encrytKey)) {
            GeneralRecordSaver saver = new GeneralRecordSaver();
            saver.setConn(conn);

            //智联招聘
            //saver.setRecordFileName("d:/spider/zhaopin/processedJobs_08_30.txt");
            //saver.setTableName("spider_job_types_zhaopin_0830");
            //String errorLogFileName = "d:/spider/tongcheng/error_0830.txt";
            //58同城

            saver.setRecordFileName("d:/spider/tongcheng/processedJobs_09_04.txt");
            saver.setTableName("spider_job_types_tongcheng_0904");
            String errorLogFileName = "d:/spider/tongcheng/error.txt";

            //中华英才
            /*
            saver.setRecordFileName("d:/spider/chinahr/processedJobs_09_04.txt");
            saver.setTableName("spider_job_types_chinahr_0904");
            String errorLogFileName = "d:/spider/chinahr/error_09_04.txt";
            */

            org.apache.commons.io.FileUtils.deleteQuietly(new File(errorLogFileName));
            saver.setErrorLogFileName(errorLogFileName);
            saver.saveToDB();
        }
    }
}
