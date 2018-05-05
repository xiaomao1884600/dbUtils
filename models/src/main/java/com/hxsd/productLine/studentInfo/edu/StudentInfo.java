package com.hxsd.productLine.studentInfo.edu;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.hxsd.productLine.studentInfo.edu.studentInfoFormatter.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/** TODO 手机号为13022019067，当存在多个学生时，页面点击学生姓名时，详细信息会混
 * Created by doubeye(doubeye@sina.com) on 2016/9/21.
 */
public class StudentInfo {
    public static JSONArray getStudentidByMobile(Connection connection, String mobile) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLExecutor.executeQuery(connection,String.format(SQL_SELECT_STUDENTID_BY_MOBILE, mobile));
        JSONArray result = new JSONArray();
        while (rs.next()) {
            long studentId = rs.getLong("studentid");
            result.addAll(ResultSetJSONWrapper.getJSONArrayFromSQL(connection, String.format(SQL_SELECT_STUDENT_BY_MOBILE, studentId, studentId)));
        }
        ConnectionHelper.close(rs);
        return result;
    }

    public static JSONArray getUserInfoByUserIds (Connection conn, String ids) throws SQLException, ClassNotFoundException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_USER_BY_IDS, ids));
    }

    public JSONArray getStudentAllocate() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_ALLOCATE, studentId));
        fillInfo(result, StudentAllocateInfoFormatter.class);
        return result;
    }

    public JSONArray getStudentAutoAssign() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_AUTO_ASSIGN, studentId));
        fillInfo(result, StudentAutoAssignFormatter.class);
        return result;
    }

    public JSONArray getStudentAutoAssignBeSpoke() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_AUTO_ASSIGN_BESPOKE, studentId));
        fillInfo(result, StudentAutoAssignBeSpokeInfoFormatter.class);
        return result;
    }

    public JSONArray getStudentFeedback() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_FEEDBACK, studentId));
        fillInfo(result, StudentFeedbackInfoFormatter.class);
        return result;
    }

    public JSONArray getStudentInvalid() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_STUDENT_INVALID, studentId, studentId, studentId));
        fillInfo(result, StudentInvalidInfoFormatter.class);
        return result;
    }

    public JSONArray getStudentDiscard() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_STUDENT_DISCARD, studentId, studentId, studentId));
        fillInfo(result, StudentDiscardInfoFormatter.class);
        return result;
    }

    public JSONArray getStudentEnrollment() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_ENROLLMENT, studentId));
        fillInfo(result, StudentEnrollmentInfoFormatter.class);
        return result;
    }

    public JSONArray getUseOldStudent() throws SQLException, ClassNotFoundException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(mainConn, String.format(SQL_SELECT_USE_OLD_STUDENT, studentId));
        fillInfo(result, UseOldStudentInfoFormatter.class);
        return result;
    }

    public JSONArray getRecoverStudent() throws SQLException, ClassNotFoundException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(logConn, String.format(SQL_SELECT_RECOVER_STUDENT_LOG, studentId));
    }

    public JSONArray getOldStudentLog() throws SQLException, ClassNotFoundException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(logConn, String.format(SQL_SELECT_OLD_STUDENT_LOG, studentId));
    }

    private static void fillInfo(JSONArray infoType, Class<?> formatterClass) {
        StudentInfoFormatter formatter = null;
        try {
            Constructor constructor = formatterClass.getConstructor(JSONObject.class);
            for (int i = 0; i < infoType.size(); i ++) {
                JSONObject element = infoType.getJSONObject(i);
                formatter = (StudentInfoFormatter) constructor.newInstance(element);
                element.put("info", formatter.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("进行学生信息格式化时出错，" + e.getMessage(), e);
        }
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public Connection getMainConn() {
        return mainConn;
    }

    public void setMainConn(Connection mainConn) {
        this.mainConn = mainConn;
    }

    public Connection getLogConn() {
        return logConn;
    }

    public void setLogConn(Connection logConn) {
        this.logConn = logConn;
    }

    public JSONObject getStudentTimeLine() {
        JSONObject result = new JSONObject();
        return result;
    }

    private long studentId;
    private Connection mainConn;
    private Connection logConn;

    /**
     * 建表时间
     */
    private static final String SQL_SELECT_CRATE_TIME = "SELECT dateline `createTime` FROM student WHERE studentid = %d";
    /**
     * 建表分配历史
     */
    private static final String SQL_SELECT_ALLOCATE = "SELECT  CONCAT(IFNULL(title, '未知'), '(', CONVERT(sc.campusid, CHAR), ')') `campusName`, FROM_UNIXTIME(sc.dateline) dateline, IF((category = 1), '建表', '分配') category, \n" +
            "'建表分配历史' `infoType`\n" +
            "FROM studentconsultanthistory sc \n" +
            "LEFT OUTER JOIN user ON sc.userid = `user`.userid \n" +
            "LEFT OUTER JOIN campus ON sc.campusid = campus.campusid\n" +
            "WHERE studentid = %d\n" +
            "ORDER BY dateline DESC ";
    /**
     * 分配历史表
     */
    private static final String SQL_SELECT_AUTO_ASSIGN = "SELECT \n" +
            "'分配历史表' `infoType`,\n" +
            "CONCAT(IFNULL(u1.username,'未知'), '(', CONVERT(a.userid, CHAR), + ')') `userName`,\n" +
            "CONCAT(IFNULL(u2.username,'未知'), '(', CONVERT(ecuserid, CHAR), + ')') `ecUserName`,\n" +
            "FROM_UNIXTIME(dateline) `dateline`,\n" +
            "invalid, changeconsultant, kfuserid\n" +
            "FROM autoassign a \n" +
            "LEFT OUTER JOIN user u1 ON a.userid = u1.userid\n" +
            "LEFT OUTER JOIN user u2 ON a.ecuserid = u2.userid\n" +
            "WHERE studentid = %d\n" +
            "ORDER BY dateline DESC";
    /**
     * 预约分配表
     */
    private static final String SQL_SELECT_AUTO_ASSIGN_BESPOKE = "SELECT\n" +
            "'预约分配表' `infoType`,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(ab.userid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(dateline) dateline,\n" +
            "FROM_UNIXTIME(bespokedateline) bespokedateline,\n" +
            "assigned, isold, ispriority\n" +
            "FROM autoassignbespoke ab \n" +
            "LEFT OUTER JOIN user u ON ab.userid = u.userid\n" +
            "WHERE studentid = %d\n" +
            "ORDER BY dateline DESC";
    /**
     * 反馈
     */
    private static final String SQL_SELECT_FEEDBACK = "SELECT \n" +
            "'反馈表' `infoType`,\n" +
            "feedbacktype, actiontype, enrolllevel,\n" +
            "IFNULL(CONCAT(tm.title, '(', CONVERT(enrolltermid, CHAR), ')'), '无') `enrollTerm`," +
            "IFNULL(CONCAT(fa.title, '(', CONVERT(fa.facultyid, CHAR), ')'), '无') `faculty`,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(f.userid, CHAR), + ')') `userName`,\n" +
            "usertype,\n" +
            "FROM_UNIXTIME(f.dateline) dateline\n" +
            "FROM feedback f \n" +
            "LEFT OUTER JOIN termmonth tm ON f.enrolltermid = tm.termid\n" +
            "LEFT JOIN faculty fa ON f.facultyid = fa.facultyid\n" +
            "LEFT OUTER JOIN user u ON f.userid = u.userid\n" +
            "WHERE studentid = %d\n" +
            "ORDER BY dateline DESC\n";

    /**
     * 无效审批记录
     private static final String SQL_SELECT_STUDENT_INVALID = "SELECT \n" +
     "reason, approveresult\n" +
     "CONCAT(IFNULL(u1.username,'未知'), '(', si.userid, + ')') `userName`,\n" +
     "CONCAT(IFNULL(u2.username,'未知'), '(', si.leaderid, + ')') `leaderName`,\n" +
     "CONCAT(IFNULL(u3.username,'未知'), '(', si.directorid, + ')') `directorName`,\n" +
     "FROM_UNIXTIME(si.dateline) dateline,\n" +
     "FROM_UNIXTIME(si.leaderdateline) `leaderDateline`,\n" +
     "FROM_UNIXTIME(si.directordateline) `directorDateline`\n" +
     "FROM studentinvalid si \n" +
     "LEFT OUTER JOIN user u1 ON si.userid = u1.userid\n" +
     "LEFT OUTER JOIN user u2 ON si.learderid = u2.userid\n" +
     "LEFT OUTER JOIN user u3 ON si.directorid = u3.userid\n" +
     " WHERE studentid = %d";
     */
    private static final String SQL_SELECT_STUDENT_INVALID = "SELECT * FROM (SELECT \n" +
            "'无效提交' `infoType`, reason, approveresult,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(si.userid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(si.dateline) dateline\n" +
            "FROM studentinvalid si \n" +
            "LEFT OUTER JOIN user u ON si.userid = u.userid\n" +
            "WHERE studentid = %d\n" +
            "UNION\n" +
            "SELECT \n" +
            "'无效一审' `infoType`, reason, approveresult,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(si.leaderid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(si.leaderdateline) dateline\n" +
            "FROM studentinvalid si \n" +
            "LEFT OUTER JOIN user u ON si.leaderid = u.userid\n" +
            "WHERE studentid = %d\n" +
            "UNION\n" +
            "SELECT \n" +
            "'无效二审' `infoType`, reason, approveresult,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(si.directorid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(si.directordateline) dateline\n" +
            "FROM studentinvalid si \n" +
            "LEFT OUTER JOIN user u ON si.directorid = u.userid\n" +
            "WHERE studentid = %d) t \n" +
            "ORDER BY dateline DESC";
    /**
     * 废弃审批
     SELECT
     reasontype, reason,
     CONCAT(IFNULL(u1.username,'未知'), '(', sd.userid, + ')') `userName`,
     CONCAT(IFNULL(u2.username,'未知'), '(', sd.leaderid, + ')') `leaderName`,
     CONCAT(IFNULL(u3.username,'未知'), '(', sd.directorid, + ')') `directorName`,
     FROM_UNIXTIME(sd.userdate) dateline,
     FROM_UNIXTIME(sd.leaderdate) `leaderDateline`,
     FROM_UNIXTIME(sd.directordate) `directorDateline`,
     leadermessage,
     leaderresult,
     directormessage,
     directorresult
     FROM studentdiscard sd
     LEFT OUTER JOIN user u1 ON sd.userid = u1.userid
     LEFT OUTER JOIN user u2 ON sd.userid = u2.userid
     LEFT OUTER JOIN user u3 ON sd.userid = u3.userid
     WHERE studentid = 2;
     select * from studentdiscard;
     */

    private static final String SQL_SELECT_STUDENT_DISCARD = "SELECT * FROM (SELECT \n" +
            "'提交废弃' `infoType`, reasontype, reason,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(sd.userid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(sd.userdate) dateline, '' `resultMessage`\n" +
            "FROM studentdiscard sd\n" +
            "LEFT OUTER JOIN user u ON sd.userid = u.userid\n" +
            "WHERE studentid = %d\n" +
            "UNION\n" +
            "SELECT \n" +
            "'废弃一审' `infoType`, leaderresult, leadermessage,\n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(sd.leaderid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(sd.leaderdate) dateline, leadermessage `resultMessage`\n" +
            "FROM studentdiscard sd\n" +
            "LEFT OUTER JOIN user u ON sd.leaderid = u.userid\n" +
            "WHERE studentid = %d\n" +
            "UNION\n" +
            "SELECT \n" +
            "'废弃二审' `infoType`, directorresult, directormessage, \n" +
            "CONCAT(IFNULL(u.username,'未知'), '(', CONVERT(sd.directorid, CHAR), + ')') `userName`,\n" +
            "FROM_UNIXTIME(sd.directordate) dateline, directormessage `resultMessage`\n" +
            "FROM studentdiscard sd \n" +
            "LEFT OUTER JOIN user u ON sd.directorid = u.userid\n" +
            "WHERE studentid = %d) t\n" +
            "ORDER BY dateline DESC";
    /**
     * 报名
     */
    private static final String SQL_SELECT_ENROLLMENT = "SELECT \n" +
            "'报名信息' `infoType`,\n" +
            "CONCAT(clazztitle, '(', CONVERT(clazzid, CHAR), ')') clazz,\n" +
            "seatnum, e.status,\n" +
            "CONCAT(u2.username, '(', CONVERT(adaid, CHAR), ')') ada,\n" +
            "CONCAT(u1.username, '(', CONVERT(e.userid, CHAR), ')') userName,\n" +
            "CONCAT(tm.title, '(', CONVERT(e.termid, CHAR), ')'),\n" +
            "enrollqueueid,\n" +
            "introducer,\n" +
            "ss.`status`, ss.userid, ss.dateline `enrollStatusDateline`,\n" +
            "insertstart, insertend, onlinetermid,\n" +
            "FROM_UNIXTIME(e.dateline) dateline,\n" +
            "e.enrollmentid, newenrollmentid, reason\n" +
            "FROM enrollment e\n" +
            "LEFT OUTER JOIN user u1 ON e.userid = u1.userid\n" +
            "LEFT OUTER JOIN user u2 ON e.userid = u2.userid\n" +
            "LEFT OUTER JOIN studentstatuslog ss ON e.enrollmentid = ss.enrollmentid\n" +
            "INNER JOIN termmonth tm ON e.termid = tm.termid\n" +
            " WHERE e.studentid = %d\n" +
            "ORDER BY dateline DESC";
    /**
     * 旧量使用表
     */
    private static final String SQL_SELECT_USE_OLD_STUDENT = "SELECT \n"+
            "'旧量使用' `infoType`,\n"+
            "CONCAT(u.username, '(', CONVERT(us.userid, CHAR), ')') userName,\n"+
            "FROM_UNIXTIME(us.dateline) dateline,\n"+
            "invalid, feedbacked\n"+
            "FROM useoldstudentlog us\n"+
            "LEFT OUTER JOIN user u ON us.userid = u.userid\n"+
            " WHERE us.studentid = %d\n"+
            "ORDER BY dateline DESC";
    /**
     * 回收日志表
     */
    private static final String SQL_SELECT_RECOVER_STUDENT_LOG = "SELECT\n" +
            "'回收日志' `infoType`,\n"+
            "\tecuserid,\n" +
            "\tFROM_UNIXTIME(dateline) dateline,\n" +
            "\tenrolllevel,\n" +
            "\trecovertype\n" +
            "FROM\n" +
            "\thxsdedu_recoverstudentlog r\n" +
            "WHERE\n" +
            "\tstudentid = %d\n" +
            "ORDER BY dateline DESC";
    /**
     * 离职人员回收日志
     */
    private static final String SQL_SELECT_OLD_STUDENT_LOG = "SELECT\n" +
            "'离职人员回收' `infoType`,\n"+
            "\tuserid, FROM_UNIXTIME(dateline) dateline\n" +
            "FROM\n" +
            "\thxsdedu_oldstudentlog o\n" +
            "WHERE\n" +
            "\tstudentid = %d\n" +
            "ORDER BY dateline desc";

    private static final String SQL_SELECT_USER_BY_IDS = "SELECT userid, username FROM user WHERE userid IN (%s)";

    private static final String SQL_SELECT_STUDENTID_BY_MOBILE = "SELECT studentid FROM student WHERE mobile = '%s'";
    private static final String SQL_SELECT_STUDENT_BY_MOBILE = "SELECT\n" +
            "\ts.studentid,\n" +
            "\tstudentname,\n" +
            "\tc.title `campusName`,\n" +
            "\tallFaculty,\n" +
            "\tidcard,\n" +
            "\tmobile,\n" +
            "\tmobile2,\n" +
            "\ts.qq,\n" +
            "\twechat,\n" +
            "\tinvalid,\n" +
            "\ts.deleted,\n" +
            "\t`discard`,\n" +
            "\tFROM_UNIXTIME(s.dateline) dateline,\n" +
            "\tconcat(CONVERT(s.ecuserid, CHAR), '(', IFNULL(u.username, '未知'), ')') ecuser,\n" +
            "\tFROM_UNIXTIME(ecdateline) ecdateline,\n" +
            "\tenrollmentid,\n" +
            "\tFROM_UNIXTIME(enrolldateline) enrolldateline,\n" +
            "\tenrolllevel, -- 最后一次报名等级ABCD量\n" +
            "\tfeedbackid,\n" +
            "\tFROM_UNIXTIME(feedbackdateline) feedbackdateline,\n" +
            "\t\n" +
            "\toldstudent -- 如果是1，则有可能是旧量，在旧系统中，没有负责人（ecuserid=0）且此字段为1是旧量\n" +
            "FROM\n" +
            "\tstudent s \n" +
            "INNER JOIN\n" +
            " campus c on s.campusid = c.campusid\n" +
            "INNER JOIN (select studentid, CAST(GROUP_CONCAT(CONCAT(title, '(', CONVERT(f.facultyid, CHAR), ')')) as CHAR) `allFaculty` from firstregistrationintentionfacultyid ff, faculty f where ff.facultyid = f.facultyid and studentid = %d group by studentid) f\n" +
            "on s.studentid = f.studentid\n" +
            "LEFT OUTER JOIN \n" +
            "`user` u on s.ecuserid = u.userid\n" +
            "WHERE\n" +
            "\ts.studentid = %d";

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        try (Connection conn = ConnectionManager.getConnection("EDU-TEST", "")) {
            StudentInfo si = new StudentInfo();
            si.setMainConn(conn);
            si.setStudentId(2);
            SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_ENROLLMENT, 2));
        }
    }
}
