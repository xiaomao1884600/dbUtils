package com.doubeye.core.dataSource.bean;

/**
 * Created by doubeye(doubeye@sina.com) on 2016/10/6.
 * 数据源组
 */
@SuppressWarnings("unused")
public class DataSourceGroup {
    /**
     * 编号
     */
    private int id;

    /**
     * 标示符
     */
    private String identifier;
    /**
     * 组名称
     */
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
