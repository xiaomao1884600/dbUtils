package com.doubeye.record.quality.inspection.speachcraft.tokenization;


import java.io.IOException;


/**
 * @author doubeye
 * 分词化接口
 */
public interface Tokenization {
    /**
     * 执行分词，并保存结果，在结果中
     * @throws IOException IO异常
     */
    void tokenize() throws IOException;
}
