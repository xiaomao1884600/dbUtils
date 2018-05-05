package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/9.
 */
public class RecoverStudentInfoFormatter extends StudentInfoFormatter{
    public RecoverStudentInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String enrollLevel = content.getString(PROPERTY_ENROLL_LEVEL);
        String recoverType = content.getString(PROPERTY_RECOVER_TYPE);
        return String.format(STUDENT_INFO_TEMPLATE, time, userName, StudentFeedbackInfoFormatter.getEnrollLevel(enrollLevel), getRecoverTypeInfo(recoverType));
    }

    private static String getRecoverTypeInfo(String recoverType) {
        char typeId = recoverType.charAt(0);
        String recoverTypeInfo = "";
        switch (typeId) {
            case '1' : recoverTypeInfo = "短信"; break;
            case '2' : recoverTypeInfo = "接待"; break;
            case '3' : recoverTypeInfo = "企业QQ"; break;
            case '4' : recoverTypeInfo = "企业QQ"; break;
            default : recoverTypeInfo = "未知";
        }
        return (recoverTypeInfo + "(" + recoverType + ")");
    }

    /**
     * 格式化字符串模板，其中参数为时间，负责人，报名等级，回收类型
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s从%s手中回收此学生,此学生为%s量，回收类型为";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_ENROLL_LEVEL = "enrolllevel";
    private static final String PROPERTY_RECOVER_TYPE = "recovertype";
}
