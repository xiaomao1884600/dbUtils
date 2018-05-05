package com.doubeye.spider.content.analyzer.postNormalizer;

/**
 * @author doubeye
 * @version 1.0.0
 * 移除职位中的修饰词
 */
public class AdjectiveRemover implements PostNameNormalizer {
    @Override
    public String normalize(String postName) {
        String normalizedPostName = postName;
        for (String keyword : KEYWORDS) {
            normalizedPostName = normalizedPostName.replace(keyword, "");
        }
        return normalizedPostName;
    }

    private static final String[] KEYWORDS = {
            "急聘",
            "诚聘",
            "高薪"
    };
}
