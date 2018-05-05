package com.doubeye.spider.content.analyzer.university.gx211;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.spider.content.analyzer.GeneralRecordSaver;
import com.doubeye.spider.content.analyzer.GeneralRecordSaver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 中国高校网抓取数据保存到MySQL
 */
public class GxUniversitySaver extends GeneralRecordSaver {
    @Override
    protected void doSave(JSONObject universityObject) throws SQLException {
        //UniversityBean bean = new UniversityBean();
        //RefactorUtils.fillByJSON(bean, universityObject);
        SQLExecutor.execute(conn, StringUtils.format(SQL_INSERT_JOB_TYPE, universityObject));
    }

    @Override
    protected JSONObject lineToObject(String line) {
        JSONObject result = JSONObject.fromObject(line);
        if (result.containsKey("majors")) {
            result.put("majors", GxMajorPageContentAnalyzer.formatMajors(result.getJSONArray("majors")));
        }
        return result;
    }


    private static void printMajor() {
        String majors = "[{\"majorName\":\"城乡规划(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"建筑类\"},{\"majorName\":\"环境生态工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"环境科学与工程类\"},{\"majorName\":\"能源化学工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"化工与制药类\"},{\"majorName\":\"电气工程及其自动化(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"电气类\"},{\"majorName\":\"乳品工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"食品科学与工程类\"},{\"majorName\":\"农业水利工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"农业工程类\"},{\"majorName\":\"生物工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"生物工程类\"},{\"majorName\":\"动物科学(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"动物生产类\"},{\"majorName\":\"食品科学与工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"食品科学与工程类\"},{\"majorName\":\"园林(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"林学类\"},{\"majorName\":\"草业科学(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"草学类\"},{\"majorName\":\"制药工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"化工与制药类\"},{\"majorName\":\"化学工程与工艺(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"化工与制药类\"},{\"majorName\":\"园艺(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"植物生产类\"},{\"majorName\":\"动物药学(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"动物医学类\"},{\"majorName\":\"动物医学(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"动物医学类\"},{\"majorName\":\"给水排水工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"建筑类\"},{\"majorName\":\"藏药学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"中药学类\"},{\"majorName\":\"城市规划(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"建筑类\"},{\"majorName\":\"土木工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"土木类\"},{\"majorName\":\"药物制剂(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"药学类\"},{\"majorName\":\"药学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"药学类\"},{\"majorName\":\"护理学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"护理学类\"},{\"majorName\":\"藏医学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"中医学类\"},{\"majorName\":\"中医学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"中医学类\"},{\"majorName\":\"口腔医学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"口腔医学类\"},{\"majorName\":\"康复治疗学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"医学技术类\"},{\"majorName\":\"医学影像学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"临床医学类\"},{\"majorName\":\"临床医学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"临床医学类\"},{\"majorName\":\"预防医学(本科)\",\"majorType\":\"医学\",\"majorSubtype\":\"公共卫生与预防医学类\"},{\"majorName\":\"电气工程与自动化(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"电气类\"},{\"majorName\":\"自动化(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"自动化类\"},{\"majorName\":\"计算机科学与技术(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"计算机类\"},{\"majorName\":\"过程装备与控制工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"机械类\"},{\"majorName\":\"机械电子工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"机械类\"},{\"majorName\":\"材料成型及控制工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"机械类\"},{\"majorName\":\"工业设计(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"机械类\"},{\"majorName\":\"机械设计制造及其自动化(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"机械类\"},{\"majorName\":\"冶金工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"材料类\"},{\"majorName\":\"材料科学与工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"材料类\"},{\"majorName\":\"地质工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"地质类\"},{\"majorName\":\"资源勘查工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"地质类\"},{\"majorName\":\"环境工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"环境科学与工程类\"},{\"majorName\":\"测绘工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"测绘类\"},{\"majorName\":\"水文与水资源工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"水利类\"},{\"majorName\":\"水利水电工程(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"水利类\"},{\"majorName\":\"林学(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"林学类\"},{\"majorName\":\"植物保护(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"植物生产类\"},{\"majorName\":\"农学(本科)\",\"majorType\":\"农学\",\"majorSubtype\":\"植物生产类\"},{\"majorName\":\"行政管理(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"公共管理类\"},{\"majorName\":\"公共事业管理(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"公共管理类\"},{\"majorName\":\"统计学(本科)\",\"majorType\":\"理学\",\"majorSubtype\":\"统计学类\"},{\"majorName\":\"环境科学(本科)\",\"majorType\":\"工学\",\"majorSubtype\":\"环境科学与工程类\"},{\"majorName\":\"生物技术(本科)\",\"majorType\":\"理学\",\"majorSubtype\":\"生物科学类\"},{\"majorName\":\"应用化学(本科)\",\"majorType\":\"理学\",\"majorSubtype\":\"化学类\"},{\"majorName\":\"电子商务(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"电子商务类\"},{\"majorName\":\"人力资源管理(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"工商管理类\"},{\"majorName\":\"旅游管理(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"旅游管理类\"},{\"majorName\":\"财务管理(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"工商管理类\"},{\"majorName\":\"市场营销(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"工商管理类\"},{\"majorName\":\"工商管理(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"工商管理类\"},{\"majorName\":\"财政学(本科)\",\"majorType\":\"经济学\",\"majorSubtype\":\"财政学类\"},{\"majorName\":\"经济学(本科)\",\"majorType\":\"经济学\",\"majorSubtype\":\"经济学类\"},{\"majorName\":\"国际经济与贸易(本科)\",\"majorType\":\"经济学\",\"majorSubtype\":\"经济与贸易类\"},{\"majorName\":\"金融学(本科)\",\"majorType\":\"经济学\",\"majorSubtype\":\"金融学类\"},{\"majorName\":\"会计学(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"工商管理类\"},{\"majorName\":\"信息管理与信息系统(本科)\",\"majorType\":\"管理学\",\"majorSubtype\":\"管理科学与工程类\"},{\"majorName\":\"针灸推拿(专科)\",\"majorType\":\"医药卫生\",\"majorSubtype\":\"临床医学\"}]";
        String formattedMajors = GxMajorPageContentAnalyzer.formatMajors(JSONArray.fromObject(majors));
        System.out.println(formattedMajors);
    }

    private static final String SQL_INSERT_JOB_TYPE = "INSERT INTO spider_university_gx211(name, url, belongs_to, property,   type, city, phone,introduction, major_url, majors, level)" +
            " VALUES ('([{universityName}])', '([{officialSite}])', '([{manager}])', '([{property}])', '([{type}])', '([{city}])', '([{phone}])', '([{introduction}])', '([{majorPageUrl}])', '([{majors}])', '([{level}])')";


    public static void main(String[] args) throws Exception {

        ApplicationContextInitiator.init();
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("DATA-ANALYZE", encryptKey)) {
            GeneralRecordSaver saver = new GxUniversitySaver();
            saver.setConn(conn);


            saver.setRecordFileName("d:/spider/gx211/universities.txt");
            String errorLogFileName = "d:/spider/gx211/error.txt";


            org.apache.commons.io.FileUtils.deleteQuietly(new File(errorLogFileName));
            saver.setTableName("spider_university_gx211");
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
