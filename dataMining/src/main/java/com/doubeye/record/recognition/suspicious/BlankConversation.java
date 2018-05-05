package com.doubeye.record.recognition.suspicious;

import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.doubeye.record.recognition.AbstractRecognitionProcessing;

import java.util.Map;

/**
 * @author doubeye
 * 空对话识别
 * 参数需包含以下内容
 * tokenizer TokenizerAdapter实例
 * 返回结果中将包括以下属性
 * isBlank boolean 是否判断为空对话
 * characterCount long 对话中的字数
 * callDuration long 对话时长，单位为秒
 */
public class BlankConversation extends AbstractRecognitionProcessing{

    private long callDuration;
    private long characterCount;
    private TokenizerAdapter tokenizer;
    private Map<String, Double> termFrequent;

    @Override
    public void prepareData() {
        getData().getLong(PROPERTY_NAME_CALL_DURATION);
        tokenizer = (TokenizerAdapter) getParameter().get(PROPERTY_NAME_TOKENIZER);
    }

    @Override
    public void process() {
        String allConversation = getAllConversation(getRecognitionResult());
        termFrequent = tokenizer.getTermFrequent(allConversation);
        computeWordCount();
        JSONObject processResult = getProcessResult();
        processResult.put(PROPERTY_NAME_RESULT_CALL_DURATION, callDuration);
        processResult.put(PROPERTY_NAME_RESULT_CHARACTER_COUNT, characterCount);
        processResult.put(PROPERTY_NAME_RESULT_IS_BLANK, (characterCount / callDuration > WORDS_PER_SECOND));
    }

    /**
     * 将识别结果中的所有的句子连接到一起
     * @param recognitionResult 识别结果
     * @return 连在一起的识别结果
     */
    private String getAllConversation(JSONArray recognitionResult) {
        StringBuilder allConversation = new StringBuilder();
        for (int i = 0; i < recognitionResult.size(); i ++) {
            JSONObject result = recognitionResult.getJSONObject(i);
            allConversation.append(result.getString(PROPERTY_NAME_CONVERSATION)).append(" ");
        }
        return allConversation.toString();
    }

    private void computeWordCount() {
        characterCount = 0;
        for (Map.Entry<String, Double> entry : termFrequent.entrySet()) {
            characterCount += (entry.getKey().length() * entry.getValue());
        }
    }

    /**
     * 每秒钟通话的字数，根据目前数据来说，平均下每秒的平均字数为3-4，为了避免误报，将此值定为2
     */
    private static final int WORDS_PER_SECOND = 2;

    /**
     * 通话时长，计费时长
     */
    private static final String PROPERTY_NAME_CALL_DURATION = "billable";
    /**
     * 分词器
     */
    private static final String PROPERTY_NAME_TOKENIZER = "tokenizer";
    /**
     * 识别结果中的对话文字属性
     */
    private static final String PROPERTY_NAME_CONVERSATION = "text";
    /**
     * 结果属性中是否为空对话
     */
    private static final String PROPERTY_NAME_RESULT_IS_BLANK = "isBlank";
    /**
     * 结果属性中的有效文字数
     */
    private static final String PROPERTY_NAME_RESULT_CHARACTER_COUNT = "characterCount";
    /**
     * 结果属性中是对话时长
     */
    private static final String PROPERTY_NAME_RESULT_CALL_DURATION = "callDuration";
}
