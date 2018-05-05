package com.hxsd.productLine.studentInfo.e.toBespokeInfo;

import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/26.
 * 预约通道信息
 */
public class ToBespokeInfo {
    private Connection conn = null;

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public JSONArray getAllCampuses() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL_CAMPUS);
    }

    public JSONArray getStudentInToBespoke() throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, SQL_SELECT_ALL_STUDENT_IN_TO_BESPOKE);
    }

    public JSONArray getECUsersBy(String campusId) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_GET_ECUSER_INFO_BY_CAMPUS_ID, campusId, campusId, campusId, campusId));
    }

    public JSONArray getAllocatedStudents(String ecuserId) throws SQLException {
        JSONArray result = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_ALLOCATED_STUDENTS, ecuserId));
        for (int i = 0; i < result.size(); i ++) {
            JSONObject student = result.getJSONObject(i);
            String studentId = student.getString("studentid");
            JSONArray studentAssortTypes = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_GET_LAST_ASSORTTYPE, studentId));
            if (studentAssortTypes.size() == 1) {
                int assortTypes = studentAssortTypes.getJSONObject(0).getInt("assorttype");
                student.put("assort", getAssortTypeInfo(assortTypes));
            }
        }
        return result;
    }

    public JSONArray getAllNoReplayedStudent(String ecuserId) throws SQLException {
        return ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_SELECT_NOREPLY, ecuserId));
    }

    private static final String getAssortTypeInfo(int assortType) {
        //1:自动分配，2:手动指定，3:转分，4:申请使用，10:教育顾问自建表，20:预约分配，30:使用旧量
        String assortTypeInfo = "";
        switch (assortType) {
            case 1 : assortTypeInfo = "自动分配"; break;
            case 2 : assortTypeInfo = "手动指定"; break;
            case 3 : assortTypeInfo = "转分"; break;
            case 4 : assortTypeInfo = "申请使用"; break;
            case 10 : assortTypeInfo = "教育顾问自建表"; break;
            case 20 : assortTypeInfo = "预约分配"; break;
            case 30 : assortTypeInfo = "使用旧量"; break;
            default: assortTypeInfo = "未知"; break;
        }
        return (assortTypeInfo + "(" + assortType + ")");
    }

    private static final String SQL_SELECT_ALL_STUDENT_IN_TO_BESPOKE = "SELECT\n" +
            "\tstudentid,\n" +
            "\tstudentname,\n" +
            "\tmobile,\n" +
            "\tc.campusid,\n" +
            "\tIF(oldstudent = 0, '否', '是') `oldStudent`,\n" +
            "\tCONCAT(fa.title, '(', CONVERT (fa.facultyid, CHAR), ')') faculty,\n" +
            "\tCONCAT(c.title, '(', CONVERT (c.campusid, CHAR), ')') campus\n" +
            "FROM\n" +
            "\tt_student s\n" +
            "INNER JOIN t_firstintentionfaculty f USING (studentid)\n" +
            "INNER JOIN t_faculty fa ON f.facultyid = fa.facultyid\n" +
            "INNER JOIN t_campus c ON s.campusid = c.campusid\n" +
            "WHERE\n" +
            "\tstudentid IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\tstudentid\n" +
            "\t\tFROM\n" +
            "\t\t\t(\n" +
            "\t\t\t\tSELECT studentid FROM t_autoassignbespoke WHERE assigned = 0 AND isold = 0 ORDER BY dateline DESC \n" +
            "\t\t\t) a\n" +
            "\t) ORDER BY c.campusid, studentid DESC";

private static final String SQL_GET_ECUSER_INFO_BY_CAMPUS_ID = "SELECT\n" +
        "\tu.userid,\n" +
        "\tu.username,\n" +
        "\treceivemaxnum,\n" +
        "\tCASE workstatus WHEN 1 THEN '在线' WHEN 2 THEN '忙碌' WHEN 0 THEN '不在线' WHEN 4 THEN '离开' ELSE '未知' END workstatus,\n" +
        "\tadvisorys,\n" +
        "\tIFNULL(countab, 0) countab,\n" +
        "\tIFNULL(countToday, 0) `countToday`,\n" +
        "\tIFNULL(noReply, 0) `noReply`,\n" +
        "\t(((receivemaxnum = 0) or (receivemaxnum > IFNULL(countToday, 0))) and ((advisorys = 0) or (advisorys > IFNULL(countab, 0))) and (IFNULL(noReply, 0) < 3) and (workstatus = 1)) `canGetNewStudent`\n" +
        "FROM\n" +
        "\t(\n" +
        "\t\tSELECT\n" +
        "\t\t\tu.userid,\n" +
        "\t\t\tu.username,\n" +
        "\t\t\treceivemaxnum,\n" +
        "\t\t\tIFNULL(advisorys, 0) advisorys,\n" +
        "\t\t\tworkstatus\n" +
        "\t\tFROM\n" +
        "\t\t\tt_user u\n" +
        "\t\tINNER JOIN t_usercampus uc ON u.userid = uc.userid\n" + // AND u.usergroupid NOT IN (8, 60) 新版的权限中，已经不再使用usergroupid
        "\t\tAND uc.campusid = %s\n" +
        "\t\tAND u.autoassign = 1 AND u.deleted = 0\n" +
        "\t\tLEFT OUTER JOIN t_advisory a ON u.userid = a.userid\n" +
        "\t) u\n" +
        "LEFT OUTER JOIN (\n" +
        "\tSELECT\n" +
        "\t\tecuserid,\n" +
        "\t\tcount(*) countab\n" +
        "\tFROM\n" +
        "\t\tt_usercampus uc\n" +
        "\tLEFT OUTER JOIN t_studentecuser ec ON uc.userid = ec.ecuserid\n" +
        "\tLEFT OUTER JOIN t_student_lastfeedback lf ON ec.studentid = lf.studentid\n" +
        "\tWHERE\n" +
        "\t\tuc.userid > 0\n" +
        "\tAND uc.campusid = %s\n" +
        "\tAND enrolllevel IN (2, 3)\n" +
        "\tAND ec.isenrollment = 0\n" +
        "\tGROUP BY\n" +
        "\t\tecuserid\n" +
        ") f ON u.userid = f.ecuserid\n" +
        "LEFT OUTER JOIN (\n" +
        "(SELECT  taa.ecuserid ecuserid, count(*) countToday\n" +
        " FROM `t_autoassign` taa\n" +
        "inner join t_student ts on taa.studentid = ts.studentid\n" +
        "where taa.assigndate = CURDATE()\n" +
        "group by ecuserid)\n" +
        ") countToday ON u.userid = countToday.ecuserid\n" +
        "LEFT OUTER JOIN (\n" +
        "SELECT\n" +
        "\tUSER .userid,\n" +
        "\tCOUNT(*) AS `noReply`\n" +
        "FROM\n" +
        "\tt_studentecuser AS studentecuser\n" +
        "INNER JOIN t_user AS USER ON (\n" +
        "\tstudentecuser.ecuserid = USER .userid\n" +
        ")\n" +
        "INNER JOIN t_student AS student ON (\n" +
        "\tstudentecuser.studentid = student.studentid\n" +
        ")\n" +
        "INNER JOIN t_usercampus AS usercampus ON (\n" +
        "\t\tUSER .userid = usercampus.userid\n" +
        ")\n" +
        "WHERE\n" +
        "\tstudentecuser.isfeedback = 0\n" +
        "AND student.userid <> USER .userid\n" +
        "AND USER .autoassign = 1\n" +
        "AND studentecuser.isenrollment = 0\n" +
        "AND usercampus.campusid = %s\n" +
        "GROUP BY\n" +
        "\tUSER .userid" +
        ") noreply ON u.userid = noreply.userid\n" +
        "ORDER BY canGetNewStudent DESC, countToday DESC, u.userid";

    private static final String SQL_SELECT_ALLOCATED_STUDENTS = "SELECT\n" +
        "\t\ts.studentid, mobile, studentname, FROM_UNIXTIME(a.dateline) `assignDate`\n" +
        "\tFROM\n" +
        "\t\t t_autoassign a \n" +
        " INNER JOIN t_student s  ON a.studentid = s.studentid\n" +
        "\tWHERE\n" +
        "\t\ta.ecuserid = %s\n" +
        "\tAND a.assigndate = CURDATE()\n" +
        "ORDER BY a.dateline DESC";

    private static final String SQL_SELECT_ALL_CAMPUS = "SELECT campusid, title FROM t_campus  WHERE deleted = 0 ORDER BY sort";

    private static final String SQL_GET_LAST_ASSORTTYPE = "SELECT\n" +
            "\tassorttype\n" +
            "FROM\n" +
            "\tt_studentconsultanthistory\n" +
            "WHERE\n" +
            "\tstudentconsultanthistoryid = (\n" +
            "\t\tSELECT\n" +
            "\t\t\tmax(studentconsultanthistoryid)\n" +
            "\t\tFROM\n" +
            "\t\t\tt_studentconsultanthistory\n" +
            "\t\tWHERE\n" +
            "\t\t\tstudentid = %s\n" +
            "\t)";

    private static final String SQL_SELECT_NOREPLY = "SELECT\n" +
            "\tstudent.studentid, student.studentname, student.mobile\n" +
            "FROM\n" +
            "\tt_studentecuser AS studentecuser\n" +
            "INNER JOIN t_user AS USER ON (\n" +
            "\tstudentecuser.ecuserid = USER .userid\n" +
            ")\n" +
            "INNER JOIN t_student AS student ON (\n" +
            "\tstudentecuser.studentid = student.studentid\n" +
            ")\n" +
            "WHERE\n" +
            "\tstudentecuser.isfeedback = 0\n" +
            "AND student.userid <> USER .userid\n" +
            "AND USER .autoassign = 1\n" +
            "AND studentecuser.isenrollment = 0\n" +
            "AND `USER`.userid = %s";
}
