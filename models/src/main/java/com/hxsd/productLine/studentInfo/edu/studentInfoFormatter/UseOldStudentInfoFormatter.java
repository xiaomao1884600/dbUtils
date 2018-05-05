package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/9.
 */
public class UseOldStudentInfoFormatter extends StudentInfoFormatter{
    public UseOldStudentInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String invalid = content.getString(PROPERTY_INVALID);
        String feedbacked = content.getString(PROPERTY_FEEDBACKED).equals("0") ? "尚未" : "已";
        return String.format(STUDENT_INFO_TEMPLATE, time, userName, invalid, feedbacked);
    }

    /**
     * 格式化字符串模板，其中参数为时间，操作人，无效标记，反馈标记
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s由%s从旧量池中捞起此学生,无效标记为%s,%s反馈";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_INVALID = "invalid";
    private static final String PROPERTY_FEEDBACKED = "feedbacked";
}
