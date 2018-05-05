package com.doubeye.record.quality.inspection.speachcraft.tokenization;

import com.doubeye.commons.utils.collection.CollectionUtils;
import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;

import java.io.IOException;
import java.util.List;

/**
 * @author doubeye
 * 分词化抽象类
 */

@SuppressWarnings("WeakerAccess")
public abstract class AbstractTokenization implements Tokenization {
    /**
     * 保存词频结果的文件名
     */
    protected String resultFileName;
    /**
     * 分词器
     */
    protected TokenizerAdapter tokenizer;
    /**
     * 保存话术句子的文件名
     */
    private String originFileName;

    /**
     * 获得话术文件中的句子，每个换行符表示一句话
     * @return 话术中的句子
     * @throws IOException IOException
     */
    protected List<String> getSentences() throws IOException {
        return CollectionUtils.loadFromFile(originFileName);
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public TokenizerAdapter getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(TokenizerAdapter tokenizer) {
        this.tokenizer = tokenizer;
    }
}
