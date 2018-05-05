package com.doubeye.commons.utils.automation.classGenerator;

import org.apache.commons.lang.StringUtils;

/**
 * @author doubeye
 * @version 1.0.0
 * 常量字段Bean
 */
public class ConstantField {
    /**
     * 常量名
     */
    private String constantName;
    /**
     * 常量值
     */
    private String constantValue;
    /**
     * 常量的注释
     */
    private String comment;

    private String wrapperFunction = "";

    /**
     * 常量的类型
     */
    private String classType = "String";

    /**
     * 获得字段定义
     * @return 字段定义内容
     */
    String getDefinition() {
        StringBuilder content = new StringBuilder();
        content.append("\t").append(ConstantClassHelper.JAVA_DOC_COMMENT_START);
        ConstantClassHelper.appendCommentContent(content, comment, "\t");
        content.append("\t").append(ConstantClassHelper.JAVA_DOC_COMMENT_END);
        if (StringUtils.isEmpty(wrapperFunction)) {
            content.append("\t").append(String.format(FIELD_DEFINITION_TEMPLATE, classType, constantName, constantValue)).append(ConstantClassHelper.LINE_SEPARATOR);
        } else {
            content.append("\t").append(String.format(FIELD_DEFINITION_TEMPLATE_WITH_WRAPPER_FUNCTION, classType, constantName, wrapperFunction, constantValue.replace("\"", "\\\""))).append(ConstantClassHelper.LINE_SEPARATOR);
        }
        return content.toString();
    }

    private static final String FIELD_DEFINITION_TEMPLATE = "public static final %s %s = \"%s\";";
    private static final String FIELD_DEFINITION_TEMPLATE_WITH_WRAPPER_FUNCTION = "public static final %s %s = %s(\"%s\");";


    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }


    public void setConstantValue(String constantValue) {
        this.constantValue = constantValue;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public void setWrapperFunction(String wrapperFunction) {
        this.wrapperFunction = wrapperFunction;
    }
}
