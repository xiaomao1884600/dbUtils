package com.hxsd.services.productLine.management.projectManagement;

/**
 * 项目bean
 */
public class ProjectEnvironment {
    /**
     * 编号
     */
    private int id;
    /**
     * 描述
     */
    private String name;
    /**
     * 项目编号
     */
    private int projectId;
    /**
     * 环境编号
     */
    private int environmentId;
    /**
     * 域名
     */
    private String domainName;
    /**
     * ip
     */
    private String ip;
    /**
     * 是否稳定
     */
    private boolean stable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @SuppressWarnings("unused")
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    @SuppressWarnings("unused")
    public void setEnvironmentId(int environmentId) {
        this.environmentId = environmentId;
    }
    @SuppressWarnings("unused")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
    @SuppressWarnings("unused")
    public void setIp(String ip) {
        this.ip = ip;
    }
    @SuppressWarnings("unused")
    public void setStable(boolean stable) {
        this.stable = stable;
    }
    @SuppressWarnings("unused")
    public int getProjectId() {
        return projectId;
    }
    @SuppressWarnings("unused")
    public int getEnvironmentId() {
        return environmentId;
    }
    @SuppressWarnings("unused")
    public String getDomainName() {
        return domainName;
    }
    @SuppressWarnings("unused")
    public String getIp() {
        return ip;
    }
    @SuppressWarnings("unused")
    public boolean isStable() {
        return stable;
    }
    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
