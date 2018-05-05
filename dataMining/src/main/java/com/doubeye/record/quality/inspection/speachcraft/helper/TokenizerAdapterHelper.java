package com.doubeye.record.quality.inspection.speachcraft.helper;

import com.doubeye.commons.utils.string.tokenizer.AnsjTokenizer;
import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;

/**
 * @author doubeye
 * 分词器助手
 */
public class TokenizerAdapterHelper {
    /**
     * 获得默认的Ansj分词器，并设置好字典和停驻词
     * @return 默认的Ansj分词器
     */
    public static TokenizerAdapter getDefaultAnsjTokenizer() {
        TokenizerAdapter tokenizerAdapter = new AnsjTokenizer();
        tokenizerAdapter.setStopWordsFileName(FILE_NAME_STOP_WORDS);
        tokenizerAdapter.init();
        return tokenizerAdapter;
    }

    private static final String FILE_NAME_STOP_WORDS = "d:/stopWords.txt";
}
