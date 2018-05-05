package com.doubeye.commons.utils.automation.classGenerator;

/**
 * @author doubeye
 * @version 1.0.0
 * 帮助生成常量属性的助手类
 */
public class ConstantFieldHelper {
    /**
     * 获得常量对象
     * @param comment 注释
     * @param constantName 常量的名字
     * @param value 常量值
     * @param classType 常量类型
     * @param wrapperFunction 常量值的包裹函数
     * @return 常量对象
     */
    public static ConstantField getField(String comment, String constantName, String classType, String wrapperFunction, String value) {
        ConstantField field = new ConstantField();
        field.setComment(comment);
        field.setConstantName(constantName);
        field.setClassType(classType);
        field.setWrapperFunction(wrapperFunction);
        field.setConstantValue(value);
        return field;
    }
}
