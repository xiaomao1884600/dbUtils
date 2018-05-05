package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/12/9.
 */
public class StudentEnrollmentInfoFormatter extends StudentInfoFormatter{

    public StudentEnrollmentInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String ada = content.getString(PROPERTY_ADA);
        String seatNumber = content.getString(PROPERTY_SEAT_NUMBER);
        String statusType = content.getString(PROPERTY_STATUS_TYPE);
        String statusInfo = getStatusInfo(statusType);
        String introducer = content.getString(PROPERTY_INTRODUCER);
        return String.format(STUDENT_INFO_TEMPLATE, time, userName, userName, ada, seatNumber, statusInfo, introducer);
    }

    private static String getStatusInfo(String statusType) {
        int typeId = Integer.parseInt(statusType);
        String statusInfo = "";
        switch (typeId) {
            case 0 : statusInfo = "虚报"; break;
            case 1 : statusInfo = "实报"; break;
            case 2 : statusInfo = "延班"; break;
            case 3 : statusInfo = "转班"; break;
            case 4 : statusInfo = "休学到复学"; break;
            case 44 : statusInfo = "休学中"; break;
            case 5 : statusInfo = "退学"; break;
            case 6 : statusInfo = "毕业"; break;
            default: statusInfo = "未知"; break;
        }
        return (statusInfo + "(" + statusType + ")");
    }


    /**
     * 格式化字符串模板，其中参数为时间，操作人，ada，班级，座位号，状态，介绍人
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s由%s为此学生报名,负责的ADA为%s,座位号为%s,状态为%s,介绍人为%s";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_ADA = "ada";
    private static final String PROPERTY_SEAT_NUMBER = "seatnum";
    private static final String PROPERTY_STATUS_TYPE = "status";
    private static final String PROPERTY_INTRODUCER = "introducer";
}
