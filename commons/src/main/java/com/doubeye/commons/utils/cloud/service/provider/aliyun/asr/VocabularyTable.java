package com.doubeye.commons.utils.cloud.service.provider.aliyun.asr;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author doubeye
 * @version 1.0.0
 * 热词表对象
 */
public class VocabularyTable {
    private String id;
    private int globalWeight = 0;
    private List<Vocabulary> vocabularies = new ArrayList<>();
    private String name;

    /**
     * 获得全局权重
     * @return 全局权重
     */
    public int getGlobalWeight() {
        return globalWeight;
    }

    /**
     * 设置全局权重，当设置全局权重大于0后，单个此的权重将失效
     * @param globalWeight 全局权重
     */
    public void setGlobalWeight(int globalWeight) {
        this.globalWeight = globalWeight;
    }

    /**
     * 添加词汇
     * @param vocabulary 词汇对象
     */
    public void addVocabulary(Vocabulary vocabulary) {
        vocabularies.add(vocabulary);
    }

    /**
     * 清除全部词汇
     */
    public void clearAll() {
        vocabularies.clear();
    }

    /**
     * 获得词汇及权重
     * @return 词汇及权重
     */
    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    /**
     * 获得词汇表名称
     * @return 词汇表名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置词汇表名称
     * @param name 词汇表名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获得热词表id，该词汇表在阿里云中被创建时由阿里云生成，如果为空字符串或空，则表示该词汇表没有被保存
     * @return 词汇表id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置热词表id
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        if (globalWeight > 0) {
            result.put(PROPERTY_GLOBAL_WEIGHT, globalWeight);
        }
        JSONArray words = new JSONArray();
        JSONObject wordWeights = new JSONObject();
        for (Vocabulary vocabulary : vocabularies) {
            if (vocabulary.getWeight() > 0) {
                wordWeights.put(vocabulary.getWord(), vocabulary.getWeight());
            } else {
                words.add(vocabulary.getWord());
            }
        }
        if (words.size() > 0) {
            result.put(PROPERTY_WORDS, words);
        }
        if (wordWeights.size() > 0) {
            result.put(PROPERTY_WORD_WEIGHTS, wordWeights);
        }
        result.put(PROPERTY_ID, id);
        result.put(PROPERTY_NAME, name);
        return result.toString();
    }

    /**
     * 仅内容字符串
     * @return 不包括id和name
     */
    public String toContentString() {
        JSONObject result = JSONObject.fromObject(toString());
        result.remove(PROPERTY_ID);
        result.remove(PROPERTY_NAME);
        return result.toString();
    }

    /**
     * 将词汇表字符串转化到词汇表对象
     * @param vocabularyTableString 词汇表字符串
     */
    public void fromString(String vocabularyTableString) {
        vocabularies.clear();
        JSONObject vocabularyObject = JSONObject.fromObject(vocabularyTableString);
        globalWeight = vocabularyObject.containsKey(PROPERTY_GLOBAL_WEIGHT) ? vocabularyObject.getInt(PROPERTY_GLOBAL_WEIGHT) : 0;
        if (vocabularyObject.containsKey(PROPERTY_WORDS)) {
            JSONArray words = JSONArray.fromObject(vocabularyObject.getJSONArray(PROPERTY_WORDS));
            for (int i = 0; i < words.size(); i ++) {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setWord(words.getString(i));
                vocabulary.setWeight(0);
                addVocabulary(vocabulary);
            }
        }
        if (vocabularyObject.containsKey(PROPERTY_WORD_WEIGHTS)) {
            JSONObject wordWeights = JSONObject.fromObject(vocabularyObject.getJSONObject(PROPERTY_WORD_WEIGHTS));
            Set<?> keySet = wordWeights.keySet();
            for (Object key : keySet) {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setWord(key.toString());
                vocabulary.setWeight(wordWeights.getInt(key.toString()));
                addVocabulary(vocabulary);
            }
        }
        if (vocabularyObject.containsKey(PROPERTY_ID)) {
            setId(vocabularyObject.getString(PROPERTY_ID));
        }

        if (vocabularyObject.containsKey(PROPERTY_NAME)) {
            setName(vocabularyObject.getString(PROPERTY_NAME));
        }
    }


    private static final String PROPERTY_GLOBAL_WEIGHT = "global_weight";
    private static final String PROPERTY_WORDS = "words";
    public static final String PROPERTY_WORD_WEIGHTS = "word_weights";
    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_NAME = "name";
}
