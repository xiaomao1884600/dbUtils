package com.doubeye.spider.content.analyzer.postNormalizer;

import java.util.ArrayList;
import java.util.List;

public class PostNameNormalizerCollection implements PostNameNormalizer{
    private List<PostNameNormalizer> normalizers = new ArrayList<>();

    @Override
    public String normalize(String postName) {
        String normalizedPostName = postName;
        for (PostNameNormalizer normalizer : normalizers) {
            normalizedPostName = normalizer.normalize(normalizedPostName);
        }
        return normalizedPostName;
    }

    public void addNormalizer(PostNameNormalizer normalizer) {
        normalizers.add(normalizer);
    }
}
