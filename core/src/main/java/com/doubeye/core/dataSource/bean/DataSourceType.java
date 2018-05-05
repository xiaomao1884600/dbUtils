package com.doubeye.core.dataSource.bean;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/6.
 * 数据源类型
 */
public class DataSourceType {
    /**
     * 编号
     */
    private int id;
    /**
     * 标示符
     */
    private String identifier;

    /**
     * 类型名称

     */
    private String typeName;
    /**
     * 驱动类名
     */
    private String driverClassName;
    /**
     * 助手类名称
     */
    private String helperClassName;

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getHelperClassName() {
        return helperClassName;
    }

    public void setHelperClassName(String helperClassName) {
        this.helperClassName = helperClassName;
    }
}
