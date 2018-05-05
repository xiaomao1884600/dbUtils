package com.hxsd.productLine.studentInfo.edu.studentInfoFormatter;

import net.sf.json.JSONObject;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/21.
 * 所有的学生信息格式化的基类
 */
public abstract class StudentInfoFormatter {
    protected JSONObject content;
    /**
     * 构造函数
     * @param content 内容对象
     */
    public StudentInfoFormatter(JSONObject content) {
        this.content = content;
    }

    /**
     * 获得格式化信息
     * @return 格式化的信息
     */
    public abstract String toString();
}
