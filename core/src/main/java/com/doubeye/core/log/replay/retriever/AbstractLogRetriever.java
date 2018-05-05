package com.doubeye.core.log.replay.retriever;

import net.sf.json.JSONArray;

/**
 * @author zhanglu1782
 * 日志提取接口
 */
public abstract class AbstractLogRetriever {
    /**
     * 提取下一页日志，如果已无下一页，则返回空
     * @return 下一页日志，结构为JSONArray
     */
    public abstract JSONArray retrieveNext();

    /**
     * 每页日志数，默认为20
     */
    private int pageSize = 20;

    /**
     * 开始记录数，默认为1
     */
    private int start = 1;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
