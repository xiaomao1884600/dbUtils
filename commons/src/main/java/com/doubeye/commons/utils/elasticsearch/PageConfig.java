package com.doubeye.commons.utils.elasticsearch;


/**
 * @author doubeye
 * ElasticSearch 分页设置
 */
public class PageConfig {
    /**
     * 起始记录数
     */
    private int start = -1;
    /**
     * 结束记录数
     */
    private int end = 0;
    /**
     * 每页文件数
     */
    private int size = 10;



    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
        this.size = end - start + 1;
    }

    public void setSize(int size) {
        this.size = size;
        end = start + size - 1;
    }

    public int getSize() {
        return size;
    }

}
