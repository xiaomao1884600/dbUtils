package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/24.
 */
public class StudentAutoAssignBeSpokeInfoFormatter extends StudentInfoFormatter{
    public StudentAutoAssignBeSpokeInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String beSpokeDateLine = content.getString(PROPERTY_BE_SPOKE_TIME);
        String assignFlag = content.getString(PROPERTY_ASSIGNED);
        String oldFlag = content.getString(PROPERTY_OLD_FLAG).equals("0") ? "不" : "";
        String priorityFlag = content.getString(PROPERTY_PRIORITY_FLAG).equals("0") ? "不" : "";
        return String.format(STUDENT_INFO_TEMPLATE, time, beSpokeDateLine, assignFlag, oldFlag, priorityFlag, userName);
    }

    /**
     * 格式化字符串模板，其中参数为时间，预约时间，分配标记，是否属于旧量，是否属于紧急分配，建表人
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s进入预约通道，预约时间为%s，分配标记为%s，%s属于旧量，%s属于紧急分配，该量的建表人为%s";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_BE_SPOKE_TIME = "bespokedateline";
    private static final String PROPERTY_ASSIGNED = "assigned";
    private static final String PROPERTY_OLD_FLAG = "isold";
    private static final String PROPERTY_PRIORITY_FLAG = "ispriority";
}
