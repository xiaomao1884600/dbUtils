package com.doubeye.commons.utils.cloud.service.provider.aliyun.asr;

/**
 * @author doubeye
 * @version 1.0.0
 * 热词对象
 */
public class Vocabulary {
    /**
     * 权重
     */
    private int weight = 0;
    /**
     * 热词
     */
    private String word;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
