package com.doubeye.spider.content.analyzer;

import com.doubeye.spider.content.analyzer.postNormalizer.AdjectiveRemover;
import com.doubeye.spider.content.analyzer.postNormalizer.ContentInBracketsRemover;
import com.doubeye.spider.content.analyzer.postNormalizer.PostNameNormalizerCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PostNameNormalizerTest {
    @Test
    public void doTest() {
        List<String> posts = getTestingNames();
        PostNameNormalizerCollection normalizers = new PostNameNormalizerCollection();
        normalizers.addNormalizer(new AdjectiveRemover());
        normalizers.addNormalizer(new ContentInBracketsRemover());

        posts.forEach(post ->
            System.out.println(normalizers.normalize(post))
        );
    }

    private static List<String> getTestingNames() {
        List<String> list = new ArrayList<>();

        list.add("我是一个正常的职位");
        list.add("急聘职位名称");
        list.add("职位急聘名称");
        list.add("职位名称急聘");
        list.add("诚聘职位高薪名称急聘");

        list.add("（我应该被去掉）职位名称");
        list.add("职位名称（我应该被去掉）");
        list.add("职位（我应该被去掉）名称");
        list.add("职位名称（我应该被去掉");
        return list;
    }
}
