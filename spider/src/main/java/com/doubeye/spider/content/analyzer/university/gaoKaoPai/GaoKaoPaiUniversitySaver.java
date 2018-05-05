package com.doubeye.spider.content.analyzer.university.gaoKaoPai;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.refactor.RefactorUtils;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.spider.content.analyzer.GeneralRecordSaver;
import com.doubeye.spider.content.analyzer.GeneralRecordSaver;
import com.doubeye.spider.content.analyzer.university.gaoKaoPai.bean.UniversityBean;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class GaoKaoPaiUniversitySaver extends GeneralRecordSaver {

    @Override
    protected void doSave(JSONObject universityObject) throws SQLException {
        UniversityBean bean = new UniversityBean();
        RefactorUtils.fillByJSON(bean, universityObject);
        SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_JOB_TYPE, JSONObject.fromObject(bean)));
    }

    @Override
    protected JSONObject lineToObject(String line) {
        JSONObject result = JSONObject.fromObject(line);
        result.put("is_211", result.containsKey("211") ? 1 : 0);
        result.put("is_985", result.containsKey("985") ? 1 : 0);
        result.put("has_master", result.containsKey("研究生院") ? 1 : 0);
        result.put("is_enroll", result.containsKey("自主招生") ? 1 : 0);
        result.put("has_nds", result.containsKey("国防生") ? 1 : 0);
        result.put("is_excellent", result.containsKey("卓越计划") ? 1 : 0);
        result.put("introduction", result.getString("introduction").replace("'", ""));
        return result;
    }

    private static final String SQL_INSERT_JOB_TYPE = "INSERT INTO spider_university(name, url, belongs_to, key_disciplines, master_stations, doctor_stations, is_211, is_985, has_master, is_enroll, has_nds, is_excellent, type, city, phone, email, introduction, major_url, majors)" +
            " VALUES ('([{name}])', '([{url}])', '([{belongsTo}])', '([{keyDisciplines}])', '([{masterStations}])', '([{doctorStations}])', '([{is_211}])', '([{is_985}])', '([{has_master}])', '([{is_enroll}])', '([{has_nds}])', '([{is_excellent}])', '([{type}])', '([{city}])', '([{phone}])', '([{email}])', '([{introduction}])', '([{majorUrl}])', '([{majors}])')";


    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encryptKey)) {
            GeneralRecordSaver saver = new GaoKaoPaiUniversitySaver();
            saver.setConn(conn);


            saver.setRecordFileName("d:/spider/gaoKaoPai/universities.txt.bak");
            String errorLogFileName = "d:/spider/gaoKaoPai/error.txt";


            org.apache.commons.io.FileUtils.deleteQuietly(new File(errorLogFileName));
            saver.setTableName("spider_university");
            saver.setErrorLogFileName(errorLogFileName);
            long t1 = System.currentTimeMillis();
            saver.saveToDB();
            long t2 = System.currentTimeMillis();
            System.out.println(t2 - t1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
