package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/24.
 */
public class StudentAutoAssignFormatter extends StudentInfoFormatter{
    public StudentAutoAssignFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String ecUserName = content.getString(PROPERTY_EC_USER_NAME);
        String invalidFlag = content.getString(PROPERTY_INVALID).equals("1") ? "无效" : "有效";
        // String changeConsultant = content.getString(PROPERTY_CHANGE_CONSULTANT).equals("0") ? "没有" : "并";
        return String.format(STUDENT_INFO_TEMPLATE, time, ecUserName, invalidFlag, userName);
    }

    /**
     * 格式化字符串模板，其中参数为时间，负责人，有效或无效,建表人
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s分配给%s，%s，该量的建表人为%s";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_EC_USER_NAME = "ecUserName";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_INVALID = "invalid";
    // private static final String PROPERTY_CHANGE_CONSULTANT = "changeconsultant";
}
