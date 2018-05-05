package com.hxsd.services.productLine.e.enrollment.confirm;

import com.doubeye.commons.application.ConnectionManager;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetCsvWrapper;
import com.doubeye.commons.database.ResultSet.ResultSetWarpper.ResultSetJSONWrapper;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.json.JSONUtils;
import com.doubeye.commons.utils.request.RequestHelper;
import com.doubeye.commons.utils.response.ResponseHelper;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import com.doubeye.core.systemProperties.SystemProperties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * 入学确认服务
 */
@SuppressWarnings("unused | WeakerAccess")
public class EnrollmentConfirmService {
    /*
    private static final String SQL_DELETE = "DELETE FROM etl_enroll_confirm";
    private static final String SQL_SELECT_DETAIL = "SELECT campus_title, clazz_title, studentname, student_number, ada_username,manager_username, CASE type WHEN 1 THEN '准时入学' WHEN 2 THEN '不能准时入学' ELSE '未知' END `type`, feedback_datetime, feedback_username, trackcontent FROM etl_enroll_confirm";
    private static final String SQL_INSERT_WITH_TYPE = "insert into etl_enroll_confirm SELECT\n" +
            "\ta.*\n" +
            "FROM\n" +
            "\t(SELECT * FROM etl_enrolled_feedback) a,\n" +
            "\t(SELECT enrollmentid, max(feedback_id) max_feedback_id FROM etl_enrolled_feedback where type > 0 GROUP BY enrollmentid) b\n" +
            "WHERE\n" +
            "\ta.enrollmentid = b.enrollmentid AND a.feedback_id = b.max_feedback_id";
    private static final String SQL_INSERT_WITHOUT_TYPE = "insert into etl_enroll_confirm SELECT\n" +
            "\ta.*\n" +
            "FROM\n" +
            "\t(SELECT * FROM etl_enrolled_feedback) a,\n" +
            "\t(SELECT enrollmentid, max(feedback_id) max_feedback_id FROM etl_enrolled_feedback GROUP BY enrollmentid) b\n" +
            "WHERE\n" +
            "\ta.enrollmentid = b.enrollmentid AND a.feedback_id = b.max_feedback_id AND a.enrollmentid not in (select enrollmentid from etl_enroll_confirm)";
    private static final String TITLES = "校区,班级,学生姓名,学号,负责人,团队经理,反馈内容关键字,反馈时间,反馈人,反馈内容";
    */
    private static final String SQL_DELETE = "DELETE FROM etl_enroll_confirm WHERE termid = %d";
    private static final String SQL_SELECT_DETAIL = "SELECT campus_title, manager_username, faculty_title, specialty_title, clazz_title, studentname, student_number, ada_username,\n" +
            "CASE type WHEN 1 THEN '准时入学' WHEN 2 THEN '不能准时入学' ELSE '未知' END `type`, \n" +
            "CASE preparation WHEN 1 THEN '基础班' ELSE '专业班' END `preparation`, \n" +
            "CASE create_type WHEN 1 THEN '渠道' ELSE '线下' END `createType`, \n" +
            "feedback_datetime, feedback_username, trackcontent  FROM etl_enroll_confirm WHERE termid = %d";
    private static final String SQL_INSERT_WITH_TYPE = "insert into etl_enroll_confirm SELECT\n" +
            "\ta.*\n" +
            "FROM\n" +
            "\t(SELECT * FROM etl_enrolled_feedback) a,\n" +
            "\t(SELECT enrollmentid, max(feedback_id) max_feedback_id FROM etl_enrolled_feedback where type > 0 GROUP BY enrollmentid) b\n" +
            "WHERE\n" +
            "\ta.enrollmentid = b.enrollmentid AND a.feedback_id = b.max_feedback_id AND termid = %d";
    private static final String SQL_INSERT_WITHOUT_TYPE = "insert into etl_enroll_confirm SELECT\n" +
            "\ta.*\n" +
            "FROM\n" +
            "\t(SELECT * FROM etl_enrolled_feedback) a,\n" +
            "\t(SELECT enrollmentid, max(feedback_id) max_feedback_id FROM etl_enrolled_feedback GROUP BY enrollmentid) b\n" +
            "WHERE\n" +
            "\ta.enrollmentid = b.enrollmentid AND a.feedback_id = b.max_feedback_id AND a.enrollmentid not in (select enrollmentid from etl_enroll_confirm) AND termid = %d";
    private static final String TITLES = "校区,团队经理,系,专业班,班级,学生姓名,学号,负责人,反馈内容关键字,班级类型,是否渠道,反馈时间,反馈人,反馈内容";

    public JSONObject generateConfirmService(Map<String, String[]> parameters) throws Exception {
        int term = RequestHelper.getInt(parameters, "term", 167);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey);
             Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            SQLExecutor.execute(conn, String.format(SQL_DELETE, term));
            SQLExecutor.execute(conn, String.format(SQL_INSERT_WITH_TYPE, term));
            SQLExecutor.execute(conn, String.format(SQL_INSERT_WITHOUT_TYPE, term));
            String dateGeneratedTime = DateTimeUtils.getCurrentTime();
            SystemProperties.saveValue(coreConnection, "ENROLL_CONFIRM_GENERATED_TIME_" + term, "", dateGeneratedTime);
            JSONObject result =  ResponseHelper.getSuccessObject();
            result.put("dataGeneratedTime", dateGeneratedTime);
            return result;
        }
    }

    public JSONObject getStatistic(Map<String, String[]> parameters) throws Exception {
        int classType = RequestHelper.getInt(parameters, "classType", -1);
        int createType = RequestHelper.getInt(parameters,"createType", -1);
        int term = RequestHelper.getInt(parameters, "term", 167);
        String condition = getSearchCondition(classType, createType, term);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            JSONObject all = singleMerge(ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_STATISTIC_ALL, condition)));
            JSONArray campuses = merge(ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_STATISTIC_CAMPUS, condition)), "campus", CollectionUtils.split("campus", CommonConstant.SEPARATOR.COMMA.toString()));
            JSONArray adas = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_STATISTIC_CAMPUS_ADA, condition));
            JSONArray campusSpecialties = ResultSetJSONWrapper.getJSONArrayFromSQL(conn, String.format(SQL_STATISTIC_CAMPUS_SPECIALTY, condition));
            merge(campuses, campusSpecialties, adas);

            JSONObject result = ResponseHelper.getSuccessObject();
            result.put("all", all);
            result.put("campuses", campuses);
            return result;
        }
    }

    private static String getSearchCondition(int classType, int createType, int termId) {

        List<String> conditions = new ArrayList<>();
        switch (classType) {
            case 1 :
                conditions.add("preparation = 0");
                break;
            case 2 :
                conditions.add("preparation = 1");
                break;
                default :
        }
        switch (createType) {
            case 1 :
                conditions.add("create_type = 1");
                break;
            case 2 :
                conditions.add("create_type <> 1");
                break;
            default :
        }
        /*
        StringBuilder conditionBuilder = new StringBuilder();
        if (conditions.size() > 0) {
            conditionBuilder.append(" WHERE ");
        }
        conditions.forEach(condition ->
            conditionBuilder.append(condition).append(" AND ")
        );
        return StringUtils.substringBeforeLast(conditionBuilder.toString(), " AND ");
        */
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE termid = " + termId);
        conditions.forEach(condition ->
                conditionBuilder.append(" AND ").append(condition)
        );
        return conditionBuilder.toString();
    }

    private static JSONObject singleMerge(JSONArray origin) {
        JSONObject result = new JSONObject();
        int total = 0, confirm = 0, drop = 0, unknown = 0;
        for (int i = 0; i < origin.size(); i ++) {
            JSONObject entry = origin.getJSONObject(i);
            String type = entry.getString("type");
            int value = entry.getInt("cnt");
            total += value;
            if ("准时入学".equals(type)) {
                result.put("confirm", value);
                confirm = value;
            } else if ("不能准时入学".equals(type)) {
                drop = value;
                result.put("drop", value);
            } else {
                unknown = value;
                result.put("unknown", value);
            }
        }
        result.put("total", total);
        result.put("confirmPercent", getRoundedPercent(confirm, total));
        result.put("dropPercent", getRoundedPercent(drop, total));
        result.put("unknownPercent", getRoundedPercent(unknown, total));
        return result;
    }

    private static float getRoundedPercent(int count, int total) {
        return Float.valueOf(String.format("%.2f", (float) count * 100 / total));
    }
    /*
    private static JSONArray merge(JSONArray origin, String mergeProperty) {
        JSONArray result = new JSONArray();
        if (origin.size() > 0) {
            JSONObject entry = origin.getJSONObject(0);
            String campusName = entry.getString(mergeProperty);
            JSONArray campusData = new JSONArray();
            campusData.add(entry);
            for (int i = 1; i < origin.size(); i++) {
                entry = origin.getJSONObject(i);
                if (campusName.equals(entry.getString(mergeProperty))) {
                    campusData.add(entry);
                } else {
                    JSONObject campusMergedObject = singleMerge(campusData);
                    campusMergedObject.put(mergeProperty, campusName);
                    result.add(campusMergedObject);
                    campusData = new JSONArray();
                    campusData.add(entry);
                    campusName = entry.getString(mergeProperty);
                }
            }
            JSONObject campusMergedObject = singleMerge(campusData);
            campusMergedObject.put(mergeProperty, campusName);
            result.add(campusMergedObject);
        }
        return result;
    }
    */

    private static JSONArray merge(JSONArray origin, String mergeProperty, List<String> additionProperties) {
        JSONArray result = new JSONArray();
        if (origin.size() > 0) {
            JSONObject entry = origin.getJSONObject(0);
            String uniqueValue = entry.getString(mergeProperty);
            Map<String, String> additionValues = getValueByProperties(entry, additionProperties);
            JSONArray data = new JSONArray();
            data.add(entry);
            for (int i = 1; i < origin.size(); i++) {
                entry = origin.getJSONObject(i);
                if (uniqueValue.equals(entry.getString(mergeProperty))) {
                    data.add(entry);
                } else {
                    JSONObject mergedObject = singleMerge(data);
                    mergedObject.accumulateAll(additionValues);
                    result.add(mergedObject);
                    data = new JSONArray();
                    data.add(entry);
                    uniqueValue = entry.getString(mergeProperty);
                    additionValues = getValueByProperties(entry, additionProperties);
                }
            }
            JSONObject mergedObject = singleMerge(data);
            mergedObject.accumulateAll(additionValues);
            result.add(mergedObject);
        }
        return result;
    }

    private static Map<String, String> getValueByProperties(JSONObject source, List<String> properties) {
        Map<String, String> result = new HashMap<>(16);
        if (properties != null) {
            for (String property : properties) {
                if (source.containsKey(property)) {
                    result.put(property, source.getString(property));
                }
            }
        }
        return result;
    }

    private static void merge(JSONArray campuses, JSONArray campusSpecialties, JSONArray adas) {
        for (int i = 0; i < campuses.size(); i ++) {
            JSONObject campusEntry = campuses.getJSONObject(i);
            JSONObject condition = new JSONObject();
            condition.put("campus", campusEntry.getString("campus"));
            JSONArray campusSpecialtyEntries = JSONUtils.findAll(campusSpecialties, condition);
            if (campusSpecialtyEntries.size() > 0) {
                JSONArray specialtyEntry = merge(campusSpecialtyEntries, "specialty", CollectionUtils.split("faculty,specialty", CommonConstant.SEPARATOR.COMMA.toString()));
                campusEntry.put("specialty", specialtyEntry);
            }
            JSONArray adaEntries = JSONUtils.findAll(adas, condition);
            if (adaEntries.size() > 0) {
                JSONArray adaEntry = merge(adaEntries, "ada", CollectionUtils.split("manager,ada", CommonConstant.SEPARATOR.COMMA.toString()));
                campusEntry.put("ada", adaEntry);
            }
        }
    }

    private static final String SQL_STATISTIC_ALL = "SELECT CASE type WHEN 1 THEN '准时入学' WHEN 2 THEN '不能准时入学' ELSE '未知' END type, COUNT(*) cnt FROM etl_enroll_confirm %s group by type";
    private static final String SQL_STATISTIC_CAMPUS = "SELECT campus_title campus, CASE type WHEN 1 THEN '准时入学' WHEN 2 THEN '不能准时入学' ELSE '未知' END type, COUNT(*) cnt FROM etl_enroll_confirm %s group by campus_title, type ORDER BY campus_id";
    private static final String SQL_STATISTIC_CAMPUS_SPECIALTY = "SELECT campus_title campus, faculty_title faculty, specialty_title specialty, CASE type WHEN 1 THEN '准时入学' WHEN 2 THEN '不能准时入学' ELSE '未知' END type, COUNT(*) cnt FROM etl_enroll_confirm %s group by campus_title, specialty_title, type ORDER BY campus_id, facultyid, specialtyid";
    private static final String SQL_STATISTIC_CAMPUS_ADA = "SELECT campus_title campus, manager_username manager, ada_username ada, CASE type WHEN 1 THEN '准时入学' WHEN 2 THEN '不能准时入学' ELSE '未知' END type, COUNT(*) cnt FROM etl_enroll_confirm %s GROUP BY campus_title, ada_username, type ORDER BY campus_id, manager_userid, adaid, type";

    public JSONObject getDetailFile(Map<String, String[]> parameters) throws Exception {
        int term = RequestHelper.getInt(parameters, "term", 167);
        String termTitle = RequestHelper.getInt(parameters, "termTitle", 1803) + "期";
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            List<String> result = new ArrayList<>();
            result.add(TITLES);
            List<String> content = ResultSetCsvWrapper.getCsvFromSQL(conn, String.format(SQL_SELECT_DETAIL, term));
            result.addAll(content);
            return ResponseHelper.getDownloadStringListContentObject(result, termTitle + "确认详情.csv", "gbk");
        }
    }

    public JSONObject getDataGeneratedTime(Map<String, String[]> parameters) throws SQLException {
        int term = RequestHelper.getInt(parameters, "term", 167);
        try (Connection coreConnection = GlobalApplicationContext.getInstance().getCoreConnection()) {
            String propertyName = "ENROLL_CONFIRM_GENERATED_TIME_" + term;
            String dateGeneratedTime = SystemProperties.getString(coreConnection, propertyName);
            JSONObject result =  ResponseHelper.getSuccessObject();
            result.put("dataGeneratedTime", dateGeneratedTime);
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContextInitiator.init();
        EnrollmentConfirmService service = new EnrollmentConfirmService();
        System.out.println(service.getStatistic(null).toString());
    }

    public JSONObject getResignedEnrollment(Map<String, String[]> parameters) throws Exception {
        int term = RequestHelper.getInt(parameters, "term", 167);
        String encryptKey = GlobalApplicationContext.getInstance().getStringParameter("dataSourceKey");
        try (Connection conn = ConnectionManager.getConnection("RECORD-ANALYZE-PRODUCT", encryptKey)) {
            List<String> result = new ArrayList<>();
            result.add(RESIGNED_ENROLLMENT_TITLES);
            List<String> content = ResultSetCsvWrapper.getCsvFromSQL(conn, String.format(SQL_SELECT_RESIGNED_ENROLLMENT, term));
            result.addAll(content);
            return ResponseHelper.getDownloadStringListContentObject(result, "离职ADA持有报名量.csv", "gbk");
        }
    }

    private static final String RESIGNED_ENROLLMENT_TITLES = "校区,系,专业班,班级,团队经理,ADA,学号,学生姓名";
    private static final String SQL_SELECT_RESIGNED_ENROLLMENT = "SELECT campus_title, faculty_title, specialty_title, clazz_title, manager_username, ada_username, student_number, studentname \n" +
            "FROM etl_enroll_confirm WHERE user_deleted > 0 AND termid = %d\n" +
            "ORDER BY campus_id, facultyid, specialtyid, clazzid, manager_userid, adaid";

}
