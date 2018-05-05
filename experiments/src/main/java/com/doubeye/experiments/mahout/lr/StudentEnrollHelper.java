package com.doubeye.experiments.mahout.lr;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetCsvWrapper;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author doubeye
 * 根据学号、期数来获得
 */
public class StudentEnrollHelper {
    private Connection conn;
    /**
     * 学号
     */
    private List<String> studentNumbers;
    /**
     * 期数
      */
    private int termId;


    public List<String> getStudentNumbers() {
        return studentNumbers;
    }

    public void setStudentNumbers(List<String> studentNumbers) {
        this.studentNumbers = studentNumbers;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public List<String> getEnrollmentInfo() throws SQLException {
        return ResultSetCsvWrapper.getCsvFromSQL(conn, String.format(SQL_SELECT_ENROLLMENT_INFO,
                CollectionUtils.split(studentNumbers, CommonConstant.SEPARATOR.COMMA.toString(),
                        CommonConstant.SEPARATOR.SINGLE_QUOTE_MARK.toString()),
                termId));
    }

    public List<String> getDropStudentNumber(String startTime, String endTime) throws SQLException {
        List ids =  ResultSetCsvWrapper.getCsvFromSQL(conn, String.format(SQL_SELECT_DROP_STUDENT_NUMBER, startTime, endTime, termId));
        /*
        ids.add("0010116113788");
        ids.add("0010118033294");
        */
        return ids;
    }

    /**
     * 获得指定学生的报名信息以及缴费信息
     */
    private static final String SQL_SELECT_ENROLLMENT_INFO = "SELECT\n" +
            "\tp.studentid, studentname, s.studentnumber, c.title campus, cl.title clazz, e.receivabletuition, p.paid, FROM_UNIXTIME(e.dateline) `enroll_time`\n" +
            "FROM\n" +
            "\tt_student s\n" +
            "INNER JOIN t_enrollment e ON s.studentid = e.studentid\n" +
            "INNER JOIN t_campus c ON e.campusid = c.campusid\n" +
            "INNER JOIN t_clazz cl ON e.clazzid = cl.clazzid\n" +
            "INNER JOIN (\n" +
            "\tSELECT s.studentid, e.enrollmentid, sum(f.money) paid\n" +
            "\tFROM\n" +
            "\t\tt_student s\n" +
            "\tLEFT OUTER JOIN t_enrollment e ON s.studentid = e.studentid\n" +
            "\tLEFT OUTER JOIN t_feelog f ON e.enrollmentid = f.enrollmentid\n" +
            "\tWHERE\n" +
            "\t\ts.studentnumber IN (%s) AND f.posting = 1 AND f.deleted <> 1 AND f.feetypeid = 1 AND e.termid = % d\n" +
            "\t\tAND e.dateline < UNIX_TIMESTAMP('2018-03-26')\n" +
            "\t\tAND e.`status` IN (0, 1)\n" +
            "\tGROUP BY\n" +
            "\t\ts.studentid,\n" +
            "\t\te.enrollmentid\n" +
            ") p ON p.studentid = s.studentid AND p.enrollmentid = e.enrollmentid";

    private static final String SQL_SELECT_DROP_STUDENT_NUMBER = "SELECT\n" +
            "\ts.studentnumber\n" +
            "FROM\n" +
            "\tt_studentstatuslog l,\n" +
            "\tt_enrollment e,\n" +
            "\tt_student s,\n" +
            "\tt_clazz c\n" +
            "WHERE\n" +
            "\te.enrollmentid = l.enrollmentid\n" +
            "AND e.studentid = s.studentid\n" +
            "AND e.clazzid = c.clazzid\n" +
            "AND l.`status` IN (5)\n" +
            "AND e.dateline < UNIX_TIMESTAMP('%s')\n" +
            "AND l.dateline > UNIX_TIMESTAMP('%s')\n" +
            "AND e.termid = %d\n" +
            "AND c.preparation = 0;";
}
