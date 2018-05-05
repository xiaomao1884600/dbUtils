package com.doubeye.record.quality.inspection.speachcraft.tokenization;

import com.doubeye.commons.utils.file.FileUtils;

import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;
import com.doubeye.record.quality.inspection.speachcraft.helper.TokenizerAdapterHelper;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author doubeye
 * 对话术的每个句子进行分词，并计算词频
 */
public class SentenceTokenization extends AbstractTokenization {

    /**
     * 执行分词，并保存结果，在结果中，每一行为一句话的词频结果，行号即为句子号
     * @throws IOException IO异常
     */
    @Override
    public void tokenize() throws IOException {
        List<String> sentences = getSentences();
        JSONArray results = new JSONArray();
        sentences.forEach(sentence -> {
            Map<String, Double> frequent = tokenizer.getTermFrequent(sentence);
            results.add(frequent);
        });
        FileUtils.toFile(resultFileName, results);
    }

    public static void main(String[] args) throws IOException {
        List<File> fileNames = FileUtils.getAllFileInDirectory("d:/speechcraftCategory", "sc.txt", 0);
        TokenizerAdapter tokenizerAdapter = TokenizerAdapterHelper.getDefaultAnsjTokenizer();
        SentenceTokenization sentenceTokenization = new SentenceTokenization();
        sentenceTokenization.setTokenizer(tokenizerAdapter);
        fileNames.forEach(fileName -> {
            String fullFileName = fileName.getAbsolutePath();
            sentenceTokenization.setOriginFileName(fullFileName);
            sentenceTokenization.setResultFileName(StringUtils.substringBeforeLast(fullFileName, "_sc") + "_tf.txt");
            try {
                sentenceTokenization.tokenize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
