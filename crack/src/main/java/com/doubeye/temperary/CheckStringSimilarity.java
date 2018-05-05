package com.doubeye.temperary;


import com.doubeye.commons.utils.string.tokenizer.AnsjTokenizer;
import org.ansj.recognition.impl.StopRecognition;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author doubeye
 * 检查各种字符串相似算法的结果
 */
public class CheckStringSimilarity {
    private static final String SENTENCE_1 = "我是火星时代教育的XX老师";
    private static final String SENTENCE_2 = "是火星时代，教育的左右工作了，是您之前咨询那个一万的话，只能现在可以免费试听";
    private static final String SENTENCE_3 = "哎呦哎，你好，我这边是广州黄金时代的王老师啊之前看您有咨询过我们这边的特产，想问你们一下近期的学习情况，是怎样打过";
    private static final String SENTENCE_4 = "哦，那到时";
    private static final String NON_RELATED_SENTENCE = "啊，不好意思，我现在不方便跟你沟通，这件好吗嗯好好那我就不打扰了，谢谢拜拜嗯拜拜";
    private static final String TEMPLATE = "我是火星时代教育老师";

    private static final String NON_SENSE = "嗯为什么呀？嗯嗯嗯对嗯这样子哦嗯嗯嗯嗯嗯，你发吧";

    public static void main(String[] args) throws Exception {

        System.out.println(getMinCosDistinct(SENTENCE_1, TEMPLATE));
        System.out.println(getMinCosDistinct(SENTENCE_2, TEMPLATE));
        System.out.println(getMinCosDistinct(SENTENCE_3, TEMPLATE));
        System.out.println(getMinCosDistinct(SENTENCE_4, TEMPLATE));
        System.out.println(getMinCosDistinct(NON_RELATED_SENTENCE, TEMPLATE));

        System.out.println(com.doubeye.commons.utils.string.StringUtils.getWordCount(getWordFrequent(NON_SENSE)));
    }

    private static int getMinDistinct(String source, String template) {
        int result = Integer.MAX_VALUE;
        for (int i = 0; i < source.length(); i++) {
            String temp = StringUtils.substring(source, i);
            result = Math.min(result, StringUtils.getLevenshteinDistance(temp, template));
        }
        return result;
    }

    private static double getMinCosDistinct(String source, String template) throws Exception {
        Map<String, Double> termsInSource = getWordFrequent(source);
        Map<String, Double> termsInTemplate = getWordFrequent(template);
        return com.doubeye.commons.utils.string.StringUtils.cosineSimilarity(termsInSource, termsInTemplate);
    }


    private static Map<String, Double> getWordFrequent(String sentence) throws Exception {
        AnsjTokenizer tokenizer = new AnsjTokenizer();
        //分词结果的一个封装，主要是一个List<Term>的terms
        StopRecognition stopWords = new StopRecognition();
        stopWords.insertStopWords("我").insertStopWords("的");
        //感叹词
        stopWords.insertStopNatures("e");
        //数字
        stopWords.insertStopNatures("m");
        //拟声词
        stopWords.insertStopNatures("o");
        //标点符号
        stopWords.insertStopNatures("w");
        //语气词
        stopWords.insertStopNatures("y");
        tokenizer.setStopWords(stopWords);
        return tokenizer.getTermFrequent(sentence);
    }
}
