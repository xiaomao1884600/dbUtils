package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/25.
 */
public class StudentInvalidInfoFormatter extends StudentInfoFormatter{
    public StudentInvalidInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String userName = content.getString(PROPERTY_USER_NAME);
        String reason = content.getString(PROPERTY_REASON);
        String infoType = content.getString(PROPERTY_INFO_TYPE);
        String approveResultType = content.getString(PROPERTY_APPROVERESULT);
        return String.format(STUDENT_INFO_TEMPLATE, time, userName, infoType, reason, getApproveResult(approveResultType));
    }

    private static String getApproveResult(String approveResultType) {
        if (approveResultType.equals("1")) {
            return "组长同意（" + approveResultType + ")";
        } else if (approveResultType.equals("2")) {
            return "组长驳回（" + approveResultType + ")";
        } else if (approveResultType.equals("3")) {
            return "主任同意（" + approveResultType + ")";
        } else if (approveResultType.equals("4")) {
            return "主任驳回（" + approveResultType + ")";
        }
        return "未知（" + approveResultType + ")";
    }

    /**
     * 格式化字符串模板，其中参数为时间，审批人，动作（提交、一审、二审），原因和结果
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s由%s进行%s,原因为%s,结果为%s";
    private static final String PROPERTY_USER_NAME = "userName";
    private static final String PROPERTY_REASON = "reason";
    private static final String PROPERTY_APPROVERESULT = "approveresult";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_INFO_TYPE = "infoType";
}
