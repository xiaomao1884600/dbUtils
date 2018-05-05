package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/24.
 * 分配信息格式化工具
 */
public class StudentAllocateInfoFormatter extends StudentInfoFormatter{
    public StudentAllocateInfoFormatter(JSONObject content) {
        super(content);
    }
    @Override
    public String toString() {
        String time = content.getString(PROPERTY_TIME);
        String category = content.getString(PROPERTY_CATEGORY);
        String campusName = content.getString(PROPERTY_CAMPUS);
        return String.format(STUDENT_INFO_TEMPLATE, time, category, campusName);
    }

    /**
     * 格式化字符串模板，其中参数为时间，分配类型，校区
     */
    private static final String STUDENT_INFO_TEMPLATE = "于%s%s到%s校区";
    private static final String PROPERTY_TIME = "dateline";
    private static final String PROPERTY_CATEGORY = "category";
    private static final String PROPERTY_CAMPUS = "campusName";
}
