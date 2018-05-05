package com.doubeye.commons.database.ResultSet.DiffrentationTeller;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.jsonBuilder.JSONWrapper;
import com.doubeye.commons.utils.collection.JSONArrayDifferentiationTeller;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** TODO 复合键
 * Created by doubeye(doubeye@sina.com) on 2017/3/16.
 */
public class ResultSetDifferentiationTellerSingle {
    /**
     * 要比较的第一个结果集
     */
    private ResultSet resultSet1;
    /**
     * 要比较的第二个结果集
     */
    private ResultSet resultSet2;
    /**
     * 结果集中主键字段 TODO 暂时只支持单一字段作为键
     */
    private String objectKeyPropertyName;

    private List<String> bothKeys = new ArrayList<>();
    private List<String> identicalKeys = new ArrayList<>();
    private List<String> keyOnlyInArray1 = new ArrayList<>();
    private List<String> keyOnlyInArray2 = new ArrayList<>();
    private List<Map<String, JSONObject>> diffs = new ArrayList<>();

    public void compare() throws SQLException {
        JSONArray array1 = JSONWrapper.getJSON(resultSet1);
        JSONArray array2 = JSONWrapper.getJSON(resultSet2);
        JSONArrayDifferentiationTeller teller = new JSONArrayDifferentiationTeller();
        teller.setArray1(array1);
        teller.setArray2(array2);
        teller.setObjectKeyPropertyName(objectKeyPropertyName);
        teller.compare();
        bothKeys = teller.getBothKeys();
        identicalKeys = teller.getIdenticalKeys();
        keyOnlyInArray1 = teller.getKeyOnlyInArray1();
        keyOnlyInArray2 = teller.getKeyOnlyInArray2();
        diffs = teller.getDiffs();
    }

    public ResultSet getResultSet1() {
        return resultSet1;
    }

    public void setResultSet1(ResultSet resultSet1) {
        this.resultSet1 = resultSet1;
    }

    public ResultSet getResultSet2() {
        return resultSet2;
    }

    public void setResultSet2(ResultSet resultSet2) {
        this.resultSet2 = resultSet2;
    }

    public String getObjectKeyPropertyName() {
        return objectKeyPropertyName;
    }

    public void setObjectKeyPropertyName(String objectKeyPropertyName) {
        this.objectKeyPropertyName = objectKeyPropertyName;
    }

    public List<String> getBothKeys() {
        return bothKeys;
    }

    public void setBothKeys(List<String> bothKeys) {
        this.bothKeys = bothKeys;
    }

    public List<String> getIdenticalKeys() {
        return identicalKeys;
    }

    public void setIdenticalKeys(List<String> identicalKeys) {
        this.identicalKeys = identicalKeys;
    }

    public List<String> getKeyOnlyInArray1() {
        return keyOnlyInArray1;
    }

    public void setKeyOnlyInArray1(List<String> keyOnlyInArray1) {
        this.keyOnlyInArray1 = keyOnlyInArray1;
    }

    public List<String> getKeyOnlyInArray2() {
        return keyOnlyInArray2;
    }

    public void setKeyOnlyInArray2(List<String> keyOnlyInArray2) {
        this.keyOnlyInArray2 = keyOnlyInArray2;
    }

    public List<Map<String, JSONObject>> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<Map<String, JSONObject>> diffs) {
        this.diffs = diffs;
    }

    public boolean equal() {
        return (keyOnlyInArray1.size() + keyOnlyInArray2.size() + diffs.size()) == 0;
    }
    /* TODO 放到单元测试中
    public static void main(String[] args) throws SQLException {
        String sql1 = "SELECT\n" +
                "\tf.userid,\n" +
                "\tCOUNT(*) AS ft,\n" +
                "\tSUM(CASE f.feedbacktype WHEN 5 THEN 1 ELSE 0 END) AS fm,\n" +
                "\tSUM(CASE f.feedbacktype WHEN 2 THEN 1 ELSE 0 END) AS fj\n" +
                "FROM\n" +
                "\tt_feedback AS f\n" +
                "INNER JOIN t_student_all AS s ON f.studentid = s.studentid\n" +
                "WHERE\n" +
                "\tf.dateline BETWEEN 1467302400 AND 1469980799\n" +
                "AND f.userid IN (529,627,3692,3654,3454,3149,2999,1924,1524,776,599,907,4012,3952,3691,3456,3373,2703,2529,2040,1883,1252,4045,3849,3802,3796,3291,3201,2068,1767,1565,4011,3794,3786,3777,3511,3505,1766,1825,3533,4013,3648,3647,3645,3513,3480,2818,1998,1088,3518,908,2397,1447,2479,2083,1762,4129,4140,4130,3975,3923,3921,3918,3613,2918,2808,650,4115,1810,4173,3943,4170,3872,3848,3496,2776,2775,4172,4164,694,4191,4048,3987,3941,3750,3426,3408,3407,2433,2283,808,2066,2598,2267,2265,3603,2089,2155,2195,2895,3705,1470,1516,1729,2705,3612,777,1997,2751,2634,2310,3857,4076,3594,2948,2916,2899,1828,4121,4122,1534,3624,3547,3198,3173,2833,2531,1864,1472,3224,2706,3982,4037,1471,4137,2106,1761,2794,3643,3641,3615,3297,670,3479,3309,2930,2788,2781,2618,3933,3532,3616,4123,4056,3979,3934,2253,1906,1894,1896,1461,2972,3187,3265,4040,3820,3610,4182,4186,2693,2231,1443,3913,3188,2596,1659,2202,3462,3665,2049,3904,3889,3887,3704,3630,4193,3611,2967,2966,2128,2064,3885,3572,4194,4189,3748,4166,1403,2403,3421,3618,4156,3467,3683,3655,2783,1732,3608,3097,3030,2984,3883,3791,3773,3614,3136,1014,3015,4169,3196,3898,3865,3416,3818,3417,4168)\n" +
                "AND s.status IN (0, 11, 12)\n" +
                "GROUP BY\n" +
                "\tf.userid";
        String sql2 = "SELECT\n" +
                "\tf.userid,\n" +
                "\tCOUNT(*) AS ft,\n" +
                "\tSUM(CASE f.feedbacktype WHEN 5 THEN 1 ELSE 0 END) AS fm,\n" +
                "\tSUM(CASE f.feedbacktype WHEN 2 THEN 1 ELSE 0 END) AS fj\n" +
                "FROM\n" +
                "\tt_feedback AS f\n" +
                "INNER JOIN t_student_all AS s ON f.studentid = s.studentid\n" +
                "WHERE\n" +
                "\tf.dateline BETWEEN 1467302400 AND 1469980799\n" +
                "AND f.userid IN (529,627,3692,3654,3454,3149,2999,1924,1524,776,599,907,4012,3952,3691,3456,3373,2703,2529,2040,1883,1252,4045,3849,3802,3796,3291,3201,2068,1767,1565,4011,3794,3786,3777,3511,3505,1766,1825,3533,4013,3648,3647,3645,3513,3480,2818,1998,1088,3518,908,2397,1447,2479,2083,1762,4129,4140,4130,3975,3923,3921,3918,3613,2918,2808,650,4115,1810,4173,3943,4170,3872,3848,3496,2776,2775,4172,4164,694,4191,4048,3987,3941,3750,3426,3408,3407,2433,2283,808,2066,2598,2267,2265,3603,2089,2155,2195,2895,3705,1470,1516,1729,2705,3612,777,1997,2751,2634,2310,3857,4076,3594,2948,2916,2899,1828,4121,4122,1534,3624,3547,3198,3173,2833,2531,1864,1472,3224,2706,3982,4037,1471,4137,2106,1761,2794,3643,3641,3615,3297,670,3479,3309,2930,2788,2781,2618,3933,3532,3616,4123,4056,3979,3934,2253,1906,1894,1896,1461,2972,3187,3265,4040,3820,3610,4182,4186,2693,2231,1443,3913,3188,2596,1659,2202,3462,3665,2049,3904,3889,3887,3704,3630,4193,3611,2967,2966,2128,2064,3885,3572,4194,4189,3748,4166,1403,2403,3421,3618,4156,3467,3683,3655,2783,1732,3608,3097,3030,2984,3883,3791,3773,3614,3136,1014,3015,4169,3196,3898,3865,3416,3818,3417,4168)\n" +
                "AND s.status IN (0, 11, 12)\n" +
                "GROUP BY\n" +
                "\tf.userid";
        String sql3 = "select userid, sum(ft) ft, sum(fm) fm, sum(fj) fj from(\n" +
                "SELECT\n" +
                "\tf.userid,FROM_UNIXTIME(f.dateline, '%Y-%m-%D'), f.studentid,\n" +
                "\tCOUNT(*) AS ft,\n" +
                "\tSUM(CASE f.feedbacktype WHEN 5 THEN 1 ELSE 0 END) AS fm,\n" +
                "\tSUM(CASE f.feedbacktype WHEN 2 THEN 1 ELSE 0 END) AS fj\n" +
                "FROM\n" +
                "\tt_feedback AS f\n" +
                "INNER JOIN t_student_all AS s ON f.studentid = s.studentid\n" +
                "WHERE\n" +
                "\tf.dateline BETWEEN 1467302400 AND 1469980799\n" +
                "AND f.userid IN (529,627,3692,3654,3454,3149,2999,1924,1524,776,599,907,4012,3952,3691,3456,3373,2703,2529,2040,1883,1252,4045,3849,3802,3796,3291,3201,2068,1767,1565,4011,3794,3786,3777,3511,3505,1766,1825,3533,4013,3648,3647,3645,3513,3480,2818,1998,1088,3518,908,2397,1447,2479,2083,1762,4129,4140,4130,3975,3923,3921,3918,3613,2918,2808,650,4115,1810,4173,3943,4170,3872,3848,3496,2776,2775,4172,4164,694,4191,4048,3987,3941,3750,3426,3408,3407,2433,2283,808,2066,2598,2267,2265,3603,2089,2155,2195,2895,3705,1470,1516,1729,2705,3612,777,1997,2751,2634,2310,3857,4076,3594,2948,2916,2899,1828,4121,4122,1534,3624,3547,3198,3173,2833,2531,1864,1472,3224,2706,3982,4037,1471,4137,2106,1761,2794,3643,3641,3615,3297,670,3479,3309,2930,2788,2781,2618,3933,3532,3616,4123,4056,3979,3934,2253,1906,1894,1896,1461,2972,3187,3265,4040,3820,3610,4182,4186,2693,2231,1443,3913,3188,2596,1659,2202,3462,3665,2049,3904,3889,3887,3704,3630,4193,3611,2967,2966,2128,2064,3885,3572,4194,4189,3748,4166,1403,2403,3421,3618,4156,3467,3683,3655,2783,1732,3608,3097,3030,2984,3883,3791,3773,3614,3136,1014,3015,4169,3196,3898,3865,3416,3818,3417,4168)\n" +
                "AND s.status IN (0, 11, 12)\n" +
                "GROUP BY\n" +
                "\tf.userid,FROM_UNIXTIME(f.dateline, '%Y-%m-%D'),f.studentid\n" +
                ")\n" +
                "a\n" +
                "group by userid\n";
        ApplicationContextInitiator.init();
        Connection conn = ConnectionManager.getConnection("E-JC-228");
        ResultSet rs1 = SQLExecutor.executeQuery(conn, sql3);
        ResultSet rs2 = SQLExecutor.executeQuery(conn, sql2);
        ResultSetDifferentiationTellerSingle teller = new ResultSetDifferentiationTellerSingle();
        teller.setResultSet1(rs1);
        teller.setResultSet2(rs2);
        teller.setObjectKeyPropertyNames("userid");
        teller.compare();
        System.out.println(teller.equal());
        System.out.println(JSONArray.fromObject(teller.diffs));
    }
    */
}
