package com.hxsd.tempNeeds;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/26.
 */
public class FeedbackExportor {

    private static final String SQL_SELECT_STUDENT_ID = "select studentid from t_student s\n" +
            "where s.isrefresh = 0\n" +
            "and s.dateline >= UNIX_TIMESTAMP('2016-12-19') and s.dateline <= UNIX_TIMESTAMP('2016-12-20') ORDER BY studentid";

    private static final String SQL_SELECT_FEEDBACK_CONTENT = "select content from t_feedbackcontent where feedbackid in (select feedbackid from t_feedback where studentid = %d) ORDER BY feedbackid";

    public static void main(String[] args) throws Exception {
        doExport();
    }

    public static void doFeedbackMergeExport() throws Exception {
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("E-PRODUCT", encrytKey);
        ResultSet rs = SQLExecutor.executeQuery(conn, SQL_SELECT_STUDENT_ID);
        File output = new File("d:/feedback.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        try {
            while (rs.next()) {
                int studentId = rs.getInt("studentid");
                System.out.println(studentId);
                ResultSet rsContent = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_FEEDBACK_CONTENT, studentId));
                StringBuffer content = new StringBuffer();
                while (rsContent.next()) {
                    if (rsContent.getString("content").length() > 0) {
                        content.append(rsContent.getString("content").replace("\n", "").replace("\r", "").replace("\t", "").replace("<br>", "").replace("&nbsp;", "")).append("|||");
                    }

                }
                String line = studentId + "\t" + content.toString();
                fileOutputStream.write((line + "\r\n").getBytes());
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileOutputStream.close();
        }
    }

    public static void doExport() throws Exception {
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("E-PRODUCT", encrytKey);
        File output = new File("d:/feedback.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        try {

            ResultSet rsContent = SQLExecutor.executeQuery(conn, SQL_EXPORT_FEEDBACK_BY_SPECIALTY);
            StringBuffer content = new StringBuffer();
            int columnCount = rsContent.getMetaData().getColumnCount();
            while (rsContent.next()) {
                String line = "";
                for (int i = 1; i < columnCount; i ++) {
                    String columnValue = rsContent.getString(i);
                    if (StringUtils.isEmpty(columnValue)) {
                        line += " " + "\t";
                    } else {
                        line += rsContent.getString(i).replace("\n", "").replace("\r", "").replace("\t", "").replace("<br>", "").replace("&nbsp;", "") + "\t";
                    }
                }
                line += "\r\n";
                content.append(line);
            }
            fileOutputStream.write((content + "\r\n").getBytes());
            fileOutputStream.flush();
        } finally {
            fileOutputStream.close();
        }
    }

    public static final String SQL = "SELECT\n" +
            " s.studentid, s.studentname,\n" +
            "#min(f.feedbackid) as feedbackid, \n" +
            "f.feedbacktype,\n" +
            "#f.facultyid, f.specialtyid,\n" +
            "#tm.title as termtitle,\n" +
            "fty.title as facultytitle,\n" +
            "spl.title as specialtytitle,\n" +
            "fc.trackcontent, FROM_UNIXTIME(f.dateline, '%Y-%m-%d %H:%i:%s'),\n" +
            "#ec.ecuserid, ec.ecdateline, \n" +
            "#u.username,\n" +
            "cps.title\n" +
            "FROM `t_feedback` f\n" +
            "inner join t_student s on f.studentid = s.studentid and s.dateline BETWEEN UNIX_TIMESTAMP('2017-02-01') AND UNIX_TIMESTAMP('2017-02-28')\n" +// TODO 郭锴铭 这里改开始和结束时间
            "inner join t_studentecuser ec on s.studentid = ec.studentid\n" +
            "#inner join t_user as u on ec.ecuserid = u.userid\n" +
            "inner join campus_bk as cps on s.campusid = cps.campusid\n" +
            "inner join faculty_bk as fty on f.facultyid = fty.facultyid\n" +
            "inner join specialty_bk as spl on f.specialtyid = spl.specialtyid\n" +
            "INNER JOIN t_feedbackcontent fc on f.feedbackid = fc.feedbackid\n" +
            "where f.dateline > UNIX_TIMESTAMP('2017-02-01')\n" +// TODO 郭锴铭 这里改开始时间
            "AND f.facultyid > 0 and f.specialtyid = 99\n" +  // TODO 郭锴铭 这里改id
            "#group by f.studentid";

    private static final String SQL_EXPORT_FEEDBACK_BY_SPECIALTY = "SELECT\n" +
            "\ts.studentid,\n" +
            "\ts.studentname,\n" +
            "\tf.feedbacktype,\n" +
            "\tfty.title AS facultytitle,\n" +
            "\tspl.title AS specialtytitle,\n" +
            "\tfc.trackcontent,\n" +
            "\tFROM_UNIXTIME(\n" +
            "\t\tf.dateline,\n" +
            "\t\t'%Y-%m-%d %H:%i:%s'\n" +
            "\t),\n" +
            "\tcps.title\n" +
            "FROM\n" +
            "\t`t_feedback` f\n" +
            "INNER JOIN t_student s ON f.studentid = s.studentid \n" +
            "INNER JOIN campus_bk AS cps ON s.campusid = cps.campusid\n" +
            "INNER JOIN faculty_bk AS fty ON f.facultyid = fty.facultyid\n" +
            "INNER JOIN specialty_bk AS spl ON f.specialtyid = spl.specialtyid\n" +
            "INNER JOIN t_feedbackcontent fc ON f.feedbackid = fc.feedbackid\n" +
            "WHERE\n" +
            "\tf.studentid IN (\n" +
            "\t\tSELECT DISTINCT\n" +
            "\t\t\tstudentid\n" +
            "\t\tFROM\n" +
            "\t\t\tt_feedback\n" +
            "\t\tWHERE\n" +
            "\t\t\tspecialtyid = 99\n" +
            "\t\tAND dateline BETWEEN UNIX_TIMESTAMP('2017-02-01')\n" +
            "\t\tAND UNIX_TIMESTAMP('2017-02-28')\n" +
            "\t)\n" +
            "ORDER BY\n" +
            "\tf.studentid";
}
