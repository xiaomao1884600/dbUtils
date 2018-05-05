package com.doubeye.commons.utils.string.tokenizer;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.string.StringUtils;
import org.ansj.domain.Result;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * Ansj分词匹配器
 */
public class AnsjTokenizer implements TokenizerAdapter{
    private StopRecognition stopWords = new StopRecognition();
    private String stopWordsFileName;

    /**
     * 添加一个停驻词
     * @param stopWord 停驻词
     */
    @Override
    public void addStopWord(String stopWord) {
        stopWords.insertStopWords(stopWord);
    }

    /**
     * 添加集合中的所有停驻词
     * @param stopWordCollection 停驻词集合
     */
    @Override
    public void addStopWords(Collection<String> stopWordCollection) {
        stopWords.insertStopWords(stopWordCollection);
    }

    /**
     * 将一类词性添加到停驻词
     * @param natureName 停驻词词性 @see 停驻词磁性https://github.com/NLPchina/ansj_seg/wiki/%E8%AF%8D%E6%80%A7%E6%A0%87%E6%B3%A8%E8%A7%84%E8%8C%83
     */
    @Override
    public void addStopWordByNature(String natureName) {
        stopWords.insertStopNatures(natureName);
    }

    @Override
    public String[] doTokenize(String sentence) {
        return new String[0];
    }

    @Override
    public Map<String, Double> getTermFrequent(String sentence) {
        Map<String, Double> frequents = new HashMap<>(sentence.length() / 2);
        Result result = ToAnalysis.parse(sentence).recognition(stopWords);
        //System.out.println(result.toString());
        result.forEach(term -> {
            String word = term.getName();
            if (frequents.containsKey(word)) {
                frequents.put(word, frequents.get(word) + 1);
            } else {
                Double frequent = 1d;
                frequents.put(word, frequent);
            }
        });
        return frequents;
    }

    @Override
    public void init() {
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
        if (org.apache.commons.lang.StringUtils.isNotEmpty(stopWordsFileName)) {
            try {
                List<String> stopWords = CollectionUtils.loadFromFile(stopWordsFileName);
                addStopWords(stopWords);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStopWords(StopRecognition stopWords) {
        this.stopWords = stopWords;
    }

    @Override
    public void setStopWordsFileName(String stopWordsFileName) {
        this.stopWordsFileName = stopWordsFileName;
    }
}
