package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/9.
 */
public class StudentDiscardInfoFormatter extends StudentInfoFormatter{
    public StudentDiscardInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String reason = content.getString(PROPERTY_REASON);
        String infoType = content.getString(PROPERTY_INFO_TYPE);
        String resultMessage = content.getString(PROPERTY_RESULT_MESSAGE);
        return String.format(STUDENT_INFO_TEMPLATE, time, userName, infoType, reason, resultMessage);
    }


    /**
     * 格式化字符串模板，其中参数为时间，审批人，动作（提交、一审、二审），原因和结果
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s由%s进行%s,原因为：【%s】,结果为%s";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_REASON = "reason";
    private static final String PROPERTY_RESULT_MESSAGE = "resultMessage";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_INFO_TYPE = "infoType";
}
