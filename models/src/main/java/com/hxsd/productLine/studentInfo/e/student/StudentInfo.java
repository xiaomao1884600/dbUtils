package com.hxsd.productLine.studentInfo.e.student;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/16.
 */
public class StudentInfo {
    public JSONArray getStudent(String student) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_STUDENT, student, student, student));
    }

    public void deleteSecondMobile() throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_UPDATE_DELETE_SECOND_MOBILE, studentId));
    }

    public void deleteSecondQQ() throws SQLException {
        SQLExecutor.execute(conn, String.format(SQL_UPDATE_DELETE_SECOND_QQ, studentId));
    }

    public boolean isStudentInChannel(String student) throws SQLException {
        ResultSet rs = null;
        try {
            rs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT_STUDENT, student, student, student));
            return rs.next() && rs.getString("isInChannel").equals("渠道学生");
        } finally {
            ConnectionHelper.close(rs);
        }
    }

    public void assignStudentToChannel(String studentId, String channelManagerId, String channelUserId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            SQLExecutor.execute(conn, String.format(SQL_INSERT_CHANNEL_STUDENT, studentId, channelManagerId, channelUserId));
            SQLExecutor.execute(conn, String.format(SQL_UPDATE_STUDENT_EC_USER, channelUserId, studentId));
            SQLExecutor.execute(conn, String.format(SQL_INSERT_STUDENT_CONSULTANT_HISTORY, studentId, channelUserId));

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }

    }

    public void unChannel(String studentId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            SQLExecutor.execute(conn, String.format(SQL_CLEAR_STUDENT_ECUSER, studentId));
            SQLExecutor.execute(conn, String.format(SQL_DELETE_CHANNEL_STUDENT, studentId));
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }

    public String getChannelStudentByStudentId(String studentId) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_CHANNEL_STUDENT_BY_STUDENT_ID, studentId)).toString();
    }


    private Connection conn;
    private int studentId;
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    private static final String SQL_SELECT_STUDENT = "SELECT\n" +
            "\ts.studentid,\n" +
            "\tstudentname,\n" +
            "\tIF(s.oldstudent = 0, '否', '是') oldstudent,\n" +
            "\ts.mobile,\n" +
            "\tmobile2,\n" +
            "\ts.qq,\n" +
            "\tqq2,\n" +
            "\ts.userid," +
            "\tIFNULL(username, '无') username,\n " +
            "\tIF(cs.studentid IS NULL, '线下学生', '渠道学生') `isInChannel`\n" +
            "FROM\n" +
            "\tt_student s\n" +
            "LEFT JOIN t_studentecuser sc ON s.studentid = sc.studentid\n" +
            "LEFT JOIN t_user u ON sc.ecuserid = u.userid\n" +
            "LEFT JOIN t_channel_student cs ON s.studentid = cs.studentid\n " +
            "WHERE s.studentid = '%s' OR s.studentname = '%s' OR s.mobile = '%s'";
    private static final String SQL_UPDATE_DELETE_SECOND_MOBILE = "UPDATE t_student SET mobile2 = '' WHERE studentid = %s";
    private static final String SQL_UPDATE_DELETE_SECOND_QQ = "UPDATE t_student SET qq2 = '' WHERE studentid = %s";

    //想学生渠道表插入记录，studentId, channelUserId, userId, 学生编号，渠道经理编号，渠道负责人编号
    private static final String SQL_INSERT_CHANNEL_STUDENT = "INSERT INTO `t_channel_student`(studentid, channeluserid, userid, dateline, ispushwx) VALUES (%s, %s, %s, UNIX_TIMESTAMP(now()), '2')";
    //修改学生负责人 ecUserId, studentId, 负责人（渠道负责人编号）,学生编号
    private static final String SQL_UPDATE_STUDENT_EC_USER = "UPDATE t_studentecuser\n" +
            "SET ecuserid = %s,\n" +
            " ecdateline = UNIX_TIMESTAMP(now()),\n" +
            " isassigned = 1,\n" +
            " createtype = '1'\n" +
            "WHERE\n" +
            "\tstudentid = %s\n" +
            "LIMIT 1";
    //插入负责人历史表，studentId, consultantId, 学生编号, 负责人（渠道负责人）编号
    private static final String SQL_INSERT_STUDENT_CONSULTANT_HISTORY = "INSERT INTO t_studentconsultanthistory (\n" +
            "\tstudentconsultanthistoryid,\n" +
            "\tstudentid,\n" +
            "\tconsultantid,\n" +
            "\tfromcampusid,\n" +
            "\tcampusid,\n" +
            "\tuserid,\n" +
            "\tdateline,\n" +
            "\tcategory,\n" +
            "\tassorttype\n" +
            ")\n" +
            "VALUES\n" +
            "\t(\n" +
            "\t\tNULL,\n" +
            "\t\t%s,\n" +
            "\t\t%s,\n" +
            "\t\t'1',\n" +
            "\t\t'1',\n" +
            "\t\t4304,\n" + // 操作人id
            "\t\tUNIX_TIMESTAMP(now()),\n" +
            "\t\t2,\n" +
            "\t\t99\n" +
            "\t)";

    private static final String SQL_CLEAR_STUDENT_ECUSER = "update t_studentecuser set createtype = 0 where studentid = %s limit 1";
    private static final String SQL_DELETE_CHANNEL_STUDENT = "DELETE FROM t_channel_student WHERE studentid = %s limit 1";
    private static final String SQL_SELECT_CHANNEL_STUDENT_BY_STUDENT_ID = "SELECT * FROM t_channel_student WHERE studentid = %s";
}
