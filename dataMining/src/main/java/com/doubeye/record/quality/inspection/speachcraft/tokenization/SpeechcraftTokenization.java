package com.doubeye.record.quality.inspection.speachcraft.tokenization;

import com.doubeye.commons.utils.file.FileUtils;
import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;
import com.doubeye.record.quality.inspection.speachcraft.helper.TokenizerAdapterHelper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SpeechcraftTokenization extends AbstractTokenization{
    /**
     * 对整个文档进行分词，所有的句子作为一个段落进行分词
     * @throws IOException IO异常
     */
    @Override
    public void tokenize() throws IOException {
        StringBuilder document = new StringBuilder();
        List<String> sentences = getSentences();
        sentences.forEach(sentence -> {
            document.append(sentence).append("\r");
        });
        Map<String, Double> frequent = tokenizer.getTermFrequent(document.toString());
        FileUtils.toFile(resultFileName, JSONObject.fromObject(frequent));
    }

    public static void main(String[] args) throws IOException {
        List<File> fileNames = FileUtils.getAllFileInDirectory("d:/speechcraftCategory", "sc.txt", 0);
        TokenizerAdapter tokenizerAdapter = TokenizerAdapterHelper.getDefaultAnsjTokenizer();
        SpeechcraftTokenization sentenceTokenization = new SpeechcraftTokenization();
        sentenceTokenization.setTokenizer(tokenizerAdapter);
        fileNames.forEach(fileName -> {
            String fullFileName = fileName.getAbsolutePath();
            sentenceTokenization.setOriginFileName(fullFileName);
            sentenceTokenization.setResultFileName(StringUtils.substringBeforeLast(fullFileName, "_sc") + "_whole.txt");
            try {
                sentenceTokenization.tokenize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
