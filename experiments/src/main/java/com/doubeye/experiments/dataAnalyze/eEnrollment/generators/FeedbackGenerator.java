package com.doubeye.experiments.dataAnalyze.eEnrollment.generators;

import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.DateTimeUtils.DateTimeUtils;
import com.doubeye.experiments.dataAnalyze.eEnrollment.StudentInfo;
import com.doubeye.experiments.dataGeneration.DataGenerator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;


public class FeedbackGenerator extends DataGenerator {
    @Override
    public void generate() throws SQLException {
        StudentInfo studentInfo = getStudentInfo();
        JSONArray feedbacks = studentInfo.getFeedbackContents();
        JSONObject feedBacksDetail = studentInfo.getFeedbackDetails();

        int first3Days = 0;
        String addDateline = studentInfo.getAddTime();
        int enrollLevel = 0;
        try (ResultSet resultSet = SQLExecutor.executeQuery(connection, String.format(SQL_SELECT_FEEDBACK, studentInfo.getStudentId(), addDateline, studentInfo.getFirstEnrollmentDate()))) {
            while (resultSet.next()) {
                JSONObject feedback = new JSONObject();
                feedback.put("enrollLevel", resultSet.getString("enrolllevel"));
                feedback.put("feedbackType", resultSet.getString("feedbacktype"));
                String content = resultSet.getString("content");
                String trackContent = resultSet.getString("trackcontent");
                String trackProgram = resultSet.getString("trackprogram");
                feedback.put("content", StringUtils.isEmpty(content) ? "" : content.replaceAll("[\\p{Cntrl}]", ""));
                feedback.put("trackContent", StringUtils.isEmpty(trackContent) ? "" : trackContent.replaceAll("[\\p{Cntrl}]", ""));
                feedback.put("trackProgram", StringUtils.isEmpty(trackProgram) ? "" : trackProgram.replaceAll("[\\p{Cntrl}]", ""));
                String feedbackDateline = resultSet.getString("dateline");
                feedback.put("dateline", feedbackDateline);
                long feedBackDiff = DateTimeUtils.timeDiff(feedbackDateline, addDateline) / MILLISECOND_IN_ONE_DAY;
                if (feedBackDiff >= 0 && feedBackDiff <= 3) {
                    first3Days++;
                }
                feedbacks.add(feedback);
                enrollLevel = resultSet.getInt("enrolllevel");
            }
            feedBacksDetail.put("first3DayCount", first3Days);
            feedBacksDetail.put("feedbackCount", feedbacks.size());
            studentInfo.setEnrollLevel(enrollLevel);
        }
    }

    private static final String SQL_SELECT_FEEDBACK = "SELECT feedbacktype, enrolllevel, content, trackcontent, trackprogram, DATE_ADD(FROM_UNIXTIME(dateline), INTERVAL 0 HOUR) `dateline` FROM t_feedback f\n" +
            "INNER JOIN t_feedbackcontent fc ON f.feedbackid = fc.feedbackid\n" +
            "WHERE f.actiontype = 'feedback'\n" +
            "AND studentid = %s AND f.dateline BETWEEN UNIX_TIMESTAMP(DATE_ADD('%s', INTERVAL -0 HOUR)) and UNIX_TIMESTAMP(DATE_ADD('%s', INTERVAL -0 HOUR)) ORDER BY f.feedbackid";


    private static final long MILLISECOND_IN_ONE_DAY = 1000 * 60 * 60 * 24;
}
