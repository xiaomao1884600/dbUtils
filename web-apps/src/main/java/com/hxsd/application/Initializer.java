package com.hxsd.application;

import com.doubeye.commons.application.ApplicationInitializer;
import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.sql.SQLExecutor;

import javax.servlet.ServletException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.1.0
 * 应用服务初始化，将系统属性表中的属性配置到应用系统缓存
 */
public class Initializer extends ApplicationInitializer{
    @Override
    public void init() throws ServletException {
        super.init();
        injectSystemParameter();
    }

    /**
     * 将系统参数表中的配置注入到全局变量中
     */
    @SuppressWarnings("WeakerAccess")
    public void injectSystemParameter() {
        GlobalApplicationContext context = GlobalApplicationContext.getInstance();
        try (Connection connection = context.getCoreConnection();
            ResultSet rs = SQLExecutor.executeQuery(connection, SQL_SELECT_ALL_PROPERTIES);) {
            while (rs.next()) {
                context.setParameter(rs.getString("identifier"), rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static final String SQL_SELECT_ALL_PROPERTIES = "SELECT identifier, value FROM core_properties";
}

