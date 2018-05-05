package com.doubeye.spider.content.analyzer;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * 职位的id存在与职位详情的url中
 */
public class UrlIdGetter implements RecordIdGetter{
    /**
     * 标记id开始的字符串
     */
    private String startWith = "/";
    /**
     * id结尾
     */
    private String endWith;
    /**
     * 包含id的属性名
     */
    private String idPropertyName;
    @Override
    public String getId(JSONObject jobObject) {
        String result = jobObject.getString(idPropertyName);
        if (!StringUtils.isEmpty(startWith) && result.contains(startWith)) {
            result = StringUtils.substringAfterLast(result, startWith);
        }
        if (!StringUtils.isEmpty(endWith) && result.contains(endWith)) {
            result = StringUtils.substringBefore(result, endWith);
        }
        return result;
        /*
        if (StringUtils.isEmpty(endWith)) {
            return jobObject.getString(idPropertyName);
        } else {
            return StringUtils.substringBeforeLast(StringUtils.substringAfterLast(jobObject.getString(idPropertyName), "/"), endWith);
        }
        */
    }

    public void setEndWith(String endWith) {
        this.endWith = endWith;
    }

    public void setIdPropertyName(String idPropertyName) {
        this.idPropertyName = idPropertyName;
    }

    public void setStartWith(String startWith) {
        this.startWith = startWith;
    }

    public static void main(String[] args) {
        String test = "http://sfds/sfa/versf/5596065365590528.html";
        String result = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(test, "/"), ".html");
        System.out.println(result);
        System.out.println(result.equals("5596065365590528"));
    }
}
