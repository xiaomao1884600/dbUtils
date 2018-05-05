package com.doubeye.record.quality.inspection.speachcraft.matcher;

import com.doubeye.commons.utils.string.StringUtils;
import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;
import com.doubeye.record.quality.inspection.speachcraft.helper.TokenizerAdapterHelper;
import com.doubeye.record.quality.inspection.speachcraft.loader.TermFrequencyLoader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author doubeye
 * 话术匹配器
 */
@SuppressWarnings("unused | WeakerAccess")
public class SpeechcraftMatcher {
    private DecimalFormat decimalFormat = new DecimalFormat("#.0000");
    private DecimalFormat decimalFormat8bit = new DecimalFormat("#.00000000");
    /**
     * 话术词频分析结果
     */
    private JSONArray termFrequencies;
    /**
     * 录音分析结果
     */
    private JSONArray records;

    private TokenizerAdapter tokenizer;

    public JSONArray doMatch() {
        JSONArray result = new JSONArray();
        for (int i = 0 ; i < records.size(); i ++) {
            JSONObject record = records.getJSONObject(i);
            String recordId = record.getString("record_id");
            int adaChannelId = record.getInt("user_channel_id");
            JSONArray conversation = record.getJSONArray("analyze_info");
            JSONObject recordResult = new JSONObject();
            recordResult.put("record_id", recordId);
            recordResult.put("result", analyzeRecord(conversation, adaChannelId));
            result.add(recordResult);
        }
        return result;
    }

    private JSONArray analyzeRecord(JSONArray conversation, int adaChannelId) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < conversation.size(); i ++) {
            JSONObject sentence = conversation.getJSONObject(i);
            if (adaChannelId == sentence.getInt("channel_id")) {
                String content = sentence.getString("text");
                JSONArray sentenceMatchResult = doSentenceMatch(content);
                sentence.put("__index", i);
                sentence.put("matchResult", sentenceMatchResult);
                result.add(sentenceMatchResult);
            }
        }
        return result;
    }

    private JSONArray doSentenceMatch(String content) {
        TreeMap<Double, JSONObject> result = calculateSimilarity(content);
        JSONArray array = new JSONArray();
        result.forEach((key, value) -> {
            JSONObject entry = new JSONObject();
            entry.put("similarity", decimalFormat.format(key));
            entry.put("detail", value);
            array.add(entry);
        });
        return array;
    };

    /**
     * 计算每个句子与话术的余弦相似度，并保留4位小数，随后最后4为数字用作区分相同的相似度用，当两个计算结果取四位小数相同时，在用后四位做区分
     */
    private TreeMap<Double, JSONObject> calculateSimilarity(String content) {
        TreeMap<Double, JSONObject> result = new TreeMap<>((o1, o2) -> o1.compareTo(o2) * -1);
        Map<String, Double> contentFrequency = tokenizer.getTermFrequent(content);
        for (int i = 0; i < termFrequencies.size(); i ++) {
            JSONObject termFrequency = termFrequencies.getJSONObject(i);
            JSONObject frequencyObject = termFrequency.getJSONObject(TermFrequencyLoader.PROPERTY_NAME.TERM_FREQUENCY.toString());
            Map<String, Double> frequencyMap = fromJSONObject(frequencyObject);
            double similarity = StringUtils.cosineSimilarity(contentFrequency, frequencyMap);
            Double similarityKey = getGeneratedSimilarity(result, similarity);
            result.put(similarityKey, termFrequency);
        }
        return result;
    }

    private static Map<String, Double> fromJSONObject(JSONObject frequencyObject) {
        Map<String, Double> result = new HashMap<>(100);
        Set<?> keys = frequencyObject.keySet();
        keys.forEach(key -> {
            result.put(key.toString(), frequencyObject.getDouble(key.toString()));
        });
        return result;
    }

    /**
     * 为了能够保留相同相似度的结果，需要在相同相似度的结果进行补0
     * @param result 相似度结果
     * @param similarity 本次计算的相似度
     * @return 处理后的相似度
     */
    private Double getGeneratedSimilarity(TreeMap<Double, JSONObject> result, Double similarity) {
        Double rounded = Double.parseDouble(decimalFormat.format(similarity));
        for(Double key : result.keySet()) {
            Double roundedKey = Double.parseDouble(decimalFormat.format(key));
            if (rounded.equals(roundedKey)) {
                String keyString = decimalFormat8bit.format(key);
                int tail = Integer.valueOf(keyString.substring(keyString.length() - 4, keyString.length())) + 1;
                return Double.parseDouble(decimalFormat.format(rounded) + String.format("%04d", tail));
            } else if (key > rounded) {
                return Double.parseDouble(decimalFormat.format(rounded) + "0001");
            }
        }
        return Double.parseDouble(decimalFormat.format(rounded) + "0001");
    }


    public JSONArray getTermFrequencies() {
        return termFrequencies;
    }

    public void setTermFrequencies(JSONArray termFrequencies) {
        this.termFrequencies = termFrequencies;
    }

    public JSONArray getRecords() {
        return records;
    }

    public void setRecords(JSONArray records) {
        this.records = records;
    }

    public TokenizerAdapter getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(TokenizerAdapter tokenizer) {
        this.tokenizer = tokenizer;
    }



    public static void main(String[] args) throws IOException {
        SpeechcraftMatcher matcher = new SpeechcraftMatcher();
        double test = 1234.34d;
        System.out.println(matcher.decimalFormat.format(test));


        matcher.setTokenizer(TokenizerAdapterHelper.getDefaultAnsjTokenizer());
        TermFrequencyLoader loader = new TermFrequencyLoader();
        loader.setRootDirectory("d:/speechcraftCategory");
        loader.setOriginPatten("_sc.txt");
        loader.setFrequencyPatten("_tf.txt");
        JSONArray termFrequencies = loader.load();
        matcher.setTermFrequencies(termFrequencies);
        String sentence = "嗯，那你你这边的话，就是说就是对我是涉及的行业，就只只是说这个行业，然后去对这个呃学的东西啊，或者说后面的发展的话，行业怎么样，您不太清楚是吗？";

        JSONArray result = matcher.doSentenceMatch(sentence);
        System.out.println(result.toString());

    }
}
