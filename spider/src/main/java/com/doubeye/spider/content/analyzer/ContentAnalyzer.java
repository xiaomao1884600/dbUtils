package com.doubeye.spider.content.analyzer;

import net.sf.json.JSONArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author doubeye
 * @version 1.0,0
 * 分析网页内容
 */
public interface ContentAnalyzer {
    void setContent(String content);
    JSONArray doAnalyze() throws IOException;
    void setAdditionInfo(String additionInfo);
    int getCurrentPage();
    Document getDocument();
    void setUrl(String url);
    void setIgnoreDetail(boolean ignoreDetail);

    /**
     * 获得父元素下制定选择器的第一个元素的内容
     * @param parent 父元素
     * @param selector 选择器
     * @return 如果存在指定元素，则返回元素的html处理后的字符串，否则返回空字符串
     */
    static String getFirstParsedElementContentBySelector(Element parent, String selector) {
        return getElementContent(getFirstElement(parent, selector));
    }

    /**
     * 获得指定父元素下的指定元素，如果不存在，则返回null
     * @param parent 父元素
     * @param selector 选择器
     * @return 指定元素
     */
    static Element getFirstElement(Element parent, String selector) {
        Elements selectedElements = parent.select(selector);
        if (selectedElements.size() > 0) {
            return selectedElements.first();
        } else {
            return null;
        }
    }

    /**
     * 获得元素的内容，如果元素为空，则返回空字符串
     * @param element 元素
     * @return 元素的内容
     */
    static String getElementContent(Element element) {
        if (element != null) {
            return Jsoup.parse(element.html()).text();
        } else {
            return "";
        }
    }

    /**
     * 获得父元素下制定选择器的第一个元素指定属性值
     * @param parent 父元素
     * @param selector 选择器
     * @param attributeName 属性名
     * @return 如果指定元素存在，则返回父元素下制定选择器的第一个元素指定属性值，否则返回空
     */
    static String getFirstParsedElementPropertyBySelector(Element parent, String selector, String attributeName) {
        return getElementAttribute(getFirstElement(parent, selector), attributeName);
    }

    /**
     * 获得元素的指定属性，如果元素为null，返回空字符串
     * @param element 元素
     * @param attributeName 属性名
     * @return 属性值，如果元素为null，返回空字符串
     */
    static String getElementAttribute(Element element, String attributeName) {
        if (element != null) {
            return element.attr(attributeName);
        } else {
            return "";
        }
    }
}
