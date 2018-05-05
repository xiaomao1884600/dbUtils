package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/25.
 */
public class StudentFeedbackInfoFormatter extends StudentInfoFormatter {
    public StudentFeedbackInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String feedbackTypeId = content.getString(PROPERTY_FEEDBACK_TYPE);
        String feedbackType = getFeedbackType(feedbackTypeId);
        String actionTypeId = content.getString(PROPERTY_ACTION_TYPE);
        String actionType = getActionType(actionTypeId);
        String term = content.getString(PROPERTY_ENROLL_TERM);
        String enrollLevelId = content.getString(PROPERTY_ENROLL_LEVEL);
        String enrollLevel = getEnrollLevel(enrollLevelId);
        String faculty = content.getString(PROPERTY_FACULTY);
        String userType = content.getString(PROPERTY_USER_TYPE).equals("1") ? "是" : "不是";

        return String.format(STUDENT_INFO_TEMPLATE, time, userName, userType, actionType, feedbackType, term, enrollLevel, faculty);
    }

    private static String getFeedbackType(String feedbackTypeId) {
        char typeId = feedbackTypeId.charAt(0);
        String feedback = "";
        switch (typeId) {
            case '1' : feedback = "短信"; break;
            case '2' : feedback = "接待"; break;
            case '3' : feedback = "企业QQ"; break;
            case '4' : feedback = "电话"; break;
            case '5' : feedback = "面谈"; break;
            case '6' : feedback = "邮件"; break;
            case '7' : feedback = "其他"; break;
            case '8' : feedback = "试听"; break;
            default: feedback = "未知"; break;
        }
        return (feedback + "(" + feedbackTypeId + ")");
    }

    private static String getActionType(String actionTypeId) {
        if (actionTypeId.equals("memo")) {
            return "备注";
        } else if (actionTypeId.equals("feedback")) {
            return "反馈";
        } else {
            return ("未知(" + actionTypeId + ")");
        }
    }

    static String getEnrollLevel(String enrollLevelId) {
        if (enrollLevelId.equals("3")) {
            return "A";
        } else if (enrollLevelId.equals("2")) {
            return "B";
        } if (enrollLevelId.equals("1")) {
            return "C";
        } if (enrollLevelId.equals("4")) {
            return "D";
        } else {
            return "未知（" + enrollLevelId + ")";
        }
    }

    /**
     * 格式化字符串模板，其中参数为时间，反馈人，是否为线上ADA，反馈类型，反馈形式（feedbackType），意向期数，等级，专业
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s由%s（%s线上ADA）添加了一条%s,类型为%s,此反馈中意向期数为%s,等级为%s，专业为%s";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_FEEDBACK_TYPE = "feedbacktype";
    private static final String PROPERTY_ACTION_TYPE = "actiontype";
    private static final String PROPERTY_ENROLL_LEVEL = "enrolllevel";
    private static final String PROPERTY_ENROLL_TERM = "enrollTerm";
    private static final String PROPERTY_FACULTY = "faculty";
    private static final String PROPERTY_USER_TYPE = "usertype";
}
