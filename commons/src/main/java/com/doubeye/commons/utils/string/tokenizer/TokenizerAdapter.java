package com.doubeye.commons.utils.string.tokenizer;

import java.util.Collection;
import java.util.Map;

/**
 * @author doubeye
 * 分词器匹配器的接口，用来统一各种分词器的接口
 */
public interface TokenizerAdapter {
    /**
     * 执行分词
     * @param sentence 要分词的句子
     * @return 分词数组
     */
     String[] doTokenize(String sentence);

    /**
     * 获得词频
     * @param sentence 要分词的句子
     * @return 词频
     */
    Map<String, Double> getTermFrequent(String sentence);

    /**
     * 添加一个停驻词
     * @param stopWord 停驻词
     */
    void addStopWord(String stopWord);

    /**
     * 添加集合中的所有停驻词
     * @param stopWordCollection 停驻词集合
     */
    void addStopWords(Collection<String> stopWordCollection);

    /**
     * 将一类词性添加到停驻词
     * @param natureName 停驻词词性
     */
    void addStopWordByNature(String natureName);

    /**
     * 初始化
     */
    void init();

    /**
     * 设置停驻词文件名，该文件为utf-8格式，每行一个停驻词
     * @param stopWordsFileName 停驻词文件名
     */
    void setStopWordsFileName(String stopWordsFileName);
}
