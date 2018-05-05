package com.hxsd.monitor;

/**
 * @author doubeye
 * @version 1.0.0
 * @ MemCache服务器配置
 */
public class MemCacheServerConfigBean {
    /**
     * id
     */
    private int id;
    /**
     * 名称
     */
    private String name;
    /**
     * 主机名或ip
     */
    private String host;
    /**
     * 端口号
     */
    private int port;
    /**
     * 所服务的项目列表，逗号分隔
     */
    private String projects;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public String getServerParam() {
        return host + ":" + port;
    }
}
