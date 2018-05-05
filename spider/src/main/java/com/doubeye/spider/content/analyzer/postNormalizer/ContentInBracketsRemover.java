package com.doubeye.spider.content.analyzer.postNormalizer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * @version 1.0.0
 * 移除职位中的括号及内容
 */
public class ContentInBracketsRemover implements PostNameNormalizer{
    @Override
    public String normalize(String postName) {
        String normalizedPostName = postName;
        for (Map item : SORT_OF_BRACKETS) {
            normalizedPostName = StringUtils.substringBefore(normalizedPostName, item.get("start").toString()) +
                StringUtils.substringAfter(normalizedPostName, item.get("end").toString());
        }
        return normalizedPostName;
    }

    private static final List<Map<String, String>> SORT_OF_BRACKETS = new ArrayList<>();

    static {
        Map<String, String> parentheses = new HashMap<>();
        parentheses.put("start", "(");
        parentheses.put("end", ")");
        SORT_OF_BRACKETS.add(parentheses);

        Map<String, String> squareBrackets = new HashMap<>();
        squareBrackets.put("start", "[");
        squareBrackets.put("end", "]");
        SORT_OF_BRACKETS.add(squareBrackets);

        Map<String, String> braces = new HashMap<>();
        braces.put("start", "{");
        braces.put("end", "}");
        SORT_OF_BRACKETS.add(braces);

        Map<String, String> chineseParentheses = new HashMap<>();
        chineseParentheses.put("start", "（");
        chineseParentheses.put("end", "）");
        SORT_OF_BRACKETS.add(chineseParentheses);

        Map<String, String> chineseSquareBrackets = new HashMap<>();
        chineseSquareBrackets.put("start", "【");
        chineseSquareBrackets.put("end", "】");
        SORT_OF_BRACKETS.add(chineseSquareBrackets);

        Map<String, String> chineseBraces = new HashMap<>();
        chineseBraces.put("start", "｛");
        chineseBraces.put("end", "｝");
        SORT_OF_BRACKETS.add(chineseBraces);
    }
}
