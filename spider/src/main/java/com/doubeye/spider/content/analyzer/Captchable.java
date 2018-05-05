package com.doubeye.spider.content.analyzer;

import org.jsoup.nodes.Document;

/**
 * @author doubeye
 * @version 1.0.0
 * 判断是否产生人机验证的接口
 */
public interface Captchable {
    /**
     * 是否需要进行人机验证
     * @param document 抓取的内容
     * @return 如果需要返回true，否则返回false
     */
    boolean needCaptcha(Document document);

    /**
     * 获得需要进行人机验证的内容url
     * @param document 抓取的内容
     * @return 如果需要，则返回验证图片的url，不需要，则返回空字符串""
     */
    String getCaptchaContentUrl(Document document);
}
