package com.hxsd.productLine.studentInfo.e.test;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.DiffrentationTeller.ResultSetDifferentiationTellerSingle;
import com.doubeye.commons.database.connection.ConnectionHelper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/13.
 */
public class Assert_ADA_Rank {
    private static final String SQL_JIAOSHOUYANG = "SELECT ecuserid,sum(aa.eNum) as cnt FROM (\n" +
            "                SELECT  ef.*,count(*) as eNum\n" +
            "                FROM(\n" +
            "                    SELECT ecuser.ecuserid, student.campusid, enrollment.termid\n" +
            "                    FROM t_enrollment AS enrollment\n" +
            "                    INNER JOIN t_student AS student ON enrollment.studentid = student.studentid\n" +
            "                    INNER JOIN t_clazz AS clazz ON enrollment.clazzid = clazz.clazzid\n" +
            "                    INNER JOIN t_feelog AS feelog ON feelog.enrollmentid = enrollment.enrollmentid\n" +
            "                    INNER JOIN t_studentecuser AS ecuser ON ecuser.studentid = enrollment.studentid\n" +
            "                    AND enrollment.termid = %d\t \t/*期数*/\n" +
            "                    AND clazz.clazztimetype = 2  \t/*班级属性-长期班*/\n" +
            "                    AND enrollment.deleted = 0   \t/*有效报名-非删除*/\n" +
            "                    AND clazz.deleted = 0\t \t/*有效班级-非删除*/\n" +
            "                    AND feelog.posting = 1       \t/*过账的单据*/\n" +
            "                    AND feelog.deleted = 0\t \t/*有效单据-非删除*/\n" +
            "                    AND clazz.discarded = 0\t \t/*班级属性-非无效*/\n" +
            "                    AND clazz.preparation = 0    \t/*班级属性-非预科*/\n" +
            "                    AND enrollment.status in (1, 6) \t/*当前报名状态是实报或者毕业的*/\n" +
            "                    AND ecuser.ecuserid > 0\t\t/*有负责人的*/\n" +
            "                    AND clazz.tuition > 0\t\t/*学费大于0的*/\n" +
            "                    GROUP BY enrollment.enrollmentid\n" +
            "                    ) as ef\n" +
            "                    GROUP BY ef.ecuserid\n" +
            "                ) aa\n" +
            "            GROUP BY aa.ecuserid";
    private static final String SQL_DOUBEYE = "SELECT ecuser.ecuserid, count(DISTINCT enrollment.enrollmentid) cnt\n" +
            "FROM t_enrollment AS enrollment\n" +
            "                    INNER JOIN t_student AS student ON enrollment.studentid = student.studentid\n" +
            "                    INNER JOIN t_clazz AS clazz ON enrollment.clazzid = clazz.clazzid                  \n" +
            "                    INNER JOIN t_studentecuser AS ecuser ON ecuser.studentid = enrollment.studentid                    \n" +
            "                    AND clazz.termid = %d\t \t/*期数*/\n" +
            "                    AND clazz.clazztimetype = 2  \t/*班级属性-长期班*/\n" +
            "                    AND enrollment.deleted = 0   \t/*有效报名-非删除*/\n" +
            "                    AND clazz.deleted = 0\t \t/*有效班级-非删除*/                   \n" +
            "                    AND clazz.discarded = 0\t \t/*班级属性-非无效*/\n" +
            "                    AND clazz.preparation = 0    \t/*班级属性-非预科*/\n" +
            "                    AND enrollment.status in (1, 6) \t/*当前报名状态是实报或者毕业的*/\n" +
            "                    AND clazz.tuition > 0\n" +
            " and EXISTS (select 1 from t_feelog feelog where feelog.enrollmentid = enrollment.enrollmentid)\n" +
            "GROUP BY ecuser.ecuserid";
    private static final String SQL_GET_ALL_TERM = "SELECT termid FROM t_termmonth";

    public static void main(String[] args) throws Exception {
        /*
        File file = new File("D:\\workcode\\dbUtils\\web-apps\\src\\main\\webapp\\config\\init.json");
        JSONObject initConfig = JSONUtils.getJsonObjectFromFile(file);
        GlobalApplicationContext.init(initConfig);
        */
        ApplicationContextInitiator.init();
        String encrytKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        Connection conn = ConnectionManager.getConnection("E-PRODUCT", encrytKey);
        List<String> result = new ArrayList<>();
        try {
            ResultSet rsTerms = SQLExecutor.executeQuery(conn, SQL_GET_ALL_TERM);
            while (rsTerms.next()) {
                int termId = rsTerms.getInt("termid");
                ResultSet doubeyeRs = SQLExecutor.executeQuery(conn, String.format(SQL_DOUBEYE, termId));
                ResultSet jiaoShouYangRs = SQLExecutor.executeQuery(conn, String.format(SQL_JIAOSHOUYANG, termId));

                ResultSetDifferentiationTellerSingle differentiationTeller = new ResultSetDifferentiationTellerSingle();
                differentiationTeller.setResultSet1(doubeyeRs);
                differentiationTeller.setResultSet2(jiaoShouYangRs);
                differentiationTeller.setObjectKeyPropertyName("ecuserid");
                differentiationTeller.compare();
                if (!differentiationTeller.equal()) {
                    System.err.println("@@@@@@@@@@@@@@ not identical find  " + termId);
                    result.add("" + termId);

                }
            }
            System.out.println("Assert_ADA_Rank result" + result.toString());
        } finally {
            ConnectionHelper.close(conn);
        }
    }
}

