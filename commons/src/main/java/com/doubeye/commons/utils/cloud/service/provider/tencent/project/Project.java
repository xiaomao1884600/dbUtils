package com.doubeye.commons.utils.cloud.service.provider.tencent.project;

/**
 * @author doubeye
 * 项目信息
 */
public class Project {
    /**
     * 腾讯appid，登录账户后，在账号信息中获得
     */
    private int appId;
    /**
     * 账号下项目编号，默认为0，代表默认项目
     */
    private int projectId = 0;

    /**
     * 构造器
     * @param appId appId
     * @param projectId 项目编号
     */
    public Project(int appId, int projectId) {
        this.appId = appId;
        this.projectId = projectId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
