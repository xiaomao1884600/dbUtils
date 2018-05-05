package com.doubeye.commons.utils.json.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 对JSONObject进行转换，用以便于数据交互使用
 */
public class NameMapConfig {
    /**
     * 名称转换对照表
     */
    private Map<String, String> nameMappers = new HashMap<>();
    /**
     * 忽略属性列表
     */
    private List<String> ignores = new ArrayList<>();

    /**
     * 添加名称转换对照表
     * @param originName 原来的名称
     * @param targetName 转换后的名称
     */
    public void addNameMap(String originName, String targetName) {
        nameMappers.put(originName, targetName);
    }

    /**
     * 添加忽略列表
     * @param name 忽略的属性名
     */
    public void addIgnore(String name) {
        ignores.add(name);
    }

    /**
     * 获得忽略列表
     * @return 忽略列表
     */
    public List<String> getIgnores() {
        return ignores;
    }

    /**
     * 获得名称转换对照表
     * @return 转换对照表
     */
    public Map<String, String> getNameMappers() {
        return nameMappers;
    }

    /**
     * 获得反转的名称对照表
     * @return 反转的名称对照表
     */
    public Map<String, String> getReversedNameMapper() {
        Map<String, String> reversed = new HashMap<>();
        nameMappers.forEach((key, value) -> {
            reversed.put(value, key);
        });
        return reversed;
    }

    public void setNameMappers(Map<String, String> nameMappers) {
        this.nameMappers = nameMappers;
    }
}
