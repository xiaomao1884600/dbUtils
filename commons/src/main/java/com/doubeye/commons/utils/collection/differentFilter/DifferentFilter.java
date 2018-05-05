package com.doubeye.commons.utils.collection.differentFilter;

import net.sf.json.JSONObject;

/**
 * @author doubeye
 * @version 1.0.0
 * 比较结果过滤器接口
 */
public interface DifferentFilter {
    /**
     * 执行过滤
     * @return 过滤后的结果
     */
    public JSONObject doFilter();
}
