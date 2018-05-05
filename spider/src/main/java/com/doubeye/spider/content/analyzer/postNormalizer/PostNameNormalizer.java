package com.doubeye.spider.content.analyzer.postNormalizer;

/**
 * @author doubeye
 * @version 1.0.0
 * 职位名称规范化方法类族接口
 */
public interface PostNameNormalizer {
    /**
     * 对职位名称进行规范化
     * @param postName 需要进行规范化的职位名称
     * @return 规范化后的名称
     */
    String normalize(String postName);
}
