package com.doubeye.core.systemProperties;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.automation.classGenerator.ConstantClassHelper;
import com.doubeye.commons.utils.automation.classGenerator.ConstantField;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author douebye
 * @version 1.0.0
 * 系统属性助手类，用来生成系统属性名的常量类
 */
public class SystemPropertiesConstantHelper {
    private static final String SQL_SELECT_ALL_IDENTIFIERS = "SELECT identifier, name FROM core_properties";
    private static final String CLASS_NAME = "SystemPropertiesConstant";
    private static final String PACKAGE_NAME = "com.doubeye.core.systemProperties";
    private static final String FILED_NAME_PREFIX = "SYSTEM_PROPERTY_";
    private static final String CORE_PROJECT_SOURCE_ROOT = "d:/workcode/dbUtils/core/src/main/java";
    public static void main(String[] args) throws SQLException, IOException {
        ApplicationContextInitiator.init();
        ConstantClassHelper constantClassHelper = new ConstantClassHelper();
        constantClassHelper.setPackageName(PACKAGE_NAME);
        constantClassHelper.setAuthor("doubeye");
        constantClassHelper.setVersion("1.0.0");
        constantClassHelper.setClassComment("定义所有系统属性名称的常量");
        constantClassHelper.setClassName(CLASS_NAME);
        constantClassHelper.setProjectSourceRoot(CORE_PROJECT_SOURCE_ROOT);
        try (Connection conn = GlobalApplicationContext.getInstance().getCoreConnection();
             ResultSet rs = SQLExecutor.executeQuery(conn, SQL_SELECT_ALL_IDENTIFIERS)) {
            while (rs.next()) {
                ConstantField field = new ConstantField();
                String propertyName = rs.getString("identifier");
                field.setComment(rs.getString("name"));
                field.setConstantName(FILED_NAME_PREFIX + propertyName);
                field.setConstantValue(propertyName);
                constantClassHelper.addField(field);
            }
        }
        constantClassHelper.generateClassContent();
    }
}
