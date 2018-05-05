package com.doubeye.spider.content.analyzer.taskConfig;

public class JogGetterTaskConfig {
    private String mainClassName;
    private String resultFileDirectory;
    private String originFileNameTemplate;
    private String processedFileNameTemplate;
    private int dataSourceId;
    private String tableNameTemplate;

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public String getResultFileDirectory() {
        return resultFileDirectory;
    }

    public void setResultFileDirectory(String resultFileDirectory) {
        this.resultFileDirectory = resultFileDirectory;
    }

    public String getOriginFileNameTemplate() {
        return originFileNameTemplate;
    }

    public void setOriginFileNameTemplate(String originFileNameTemplate) {
        this.originFileNameTemplate = originFileNameTemplate;
    }

    public String getProcessedFileNameTemplate() {
        return processedFileNameTemplate;
    }

    public void setProcessedFileNameTemplate(String processedFileNameTemplate) {
        this.processedFileNameTemplate = processedFileNameTemplate;
    }

    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getTableNameTemplate() {
        return tableNameTemplate;
    }

    public void setTableNameTemplate(String tableNameTemplate) {
        this.tableNameTemplate = tableNameTemplate;
    }
}
