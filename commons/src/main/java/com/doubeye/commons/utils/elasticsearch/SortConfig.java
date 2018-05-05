package com.doubeye.commons.utils.elasticsearch;


import org.elasticsearch.search.sort.SortOrder;

/**
 * @author doubeye
 * @version 1.0.0
 * ElasticSearch 排序配置
 */
public class SortConfig {
    /**
     * 字段名称
     */
    private String filedName;
    /**
     * 排序方式，默认为升序（SortOrder.ASC）
     */
    private SortOrder sort = SortOrder.ASC;

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public SortOrder getSort() {
        return sort;
    }

    public void setSort(SortOrder sort) {
        this.sort = sort;
    }

    public SortConfig(String fieldName, SortOrder order) {
        this.filedName = fieldName;
        this.sort = sort;
    }
}
