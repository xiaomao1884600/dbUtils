package com.doubeye.commons.database.connection.bean;

import com.doubeye.commons.utils.refactor.RefactorUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author doubeye
 * @version 1.0.0
 * 数据库连接信息
 */
public class DataSource {
    /**
     * 编号
     */
    private int id;
    /**
     * 数据源标示符
     */
    private String identifier;

    /**
     * 数据源名称
     */
    private String typeName;
    /**
     * 数据源类型
     */
    private int datasourceType;
    /**
     * 数据库用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 主机
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * 默认数据库
     */
    private String defaultSchema;

    private String helperClassName;

    public String getHelperClassName() {
        return helperClassName;
    }

    @SuppressWarnings("unused")
    public void setHelperClassName(String helperClassName) {
        this.helperClassName = helperClassName;
    }

    /**
     * 数据源所属组编号
     */
    private int datasourceGroup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    @SuppressWarnings("unused")
    public String getTypeName() {
        return typeName;
    }
    @SuppressWarnings("unused")
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    @SuppressWarnings("unused")
    public int getDatasourceType() {
        return datasourceType;
    }
    @SuppressWarnings("unused")
    public void setDatasourceType(int datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }
    @SuppressWarnings("unused")
    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }
    @SuppressWarnings("unused")
    public int getDatasourceGroup() {
        return datasourceGroup;
    }
    @SuppressWarnings("unused")
    public void setDatasourceGroup(int datasourceGroup) {
        this.datasourceGroup = datasourceGroup;
    }

    /**
     * 获得字符串形式
     * @return 连接信息的字符串形式
     */
    @Override
    public String toString() {
        try {
            return RefactorUtils.toString(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
}
