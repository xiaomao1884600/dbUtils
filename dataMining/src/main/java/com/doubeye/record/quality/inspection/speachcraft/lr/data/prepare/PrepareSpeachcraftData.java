package com.doubeye.record.quality.inspection.speachcraft.lr.data.prepare;

import com.doubeye.commons.utils.constant.CommonConstant;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;
import com.doubeye.commons.utils.string.tokenizer.TokenizerAdapter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author doubeye
 * 预处理对话数据，将当时标注的对话分类，整理成不分角色，学生，ADA的对话，并保存到数据库中
 * 同时将对话进行分词处理
 */
public class PrepareSpeachcraftData {
    private CustomSearchHelper searchHelper;
    private TokenizerAdapter tokenizationAdapter;
    public PrepareSpeachcraftData(CustomSearchHelper searchHelper, TokenizerAdapter tokenizationAdapter) {
        this.searchHelper = searchHelper;
        this.tokenizationAdapter = tokenizationAdapter;
    }

    public JSONArray next() {
        JSONArray result = new JSONArray();
        JSONArray records = searchHelper.getSearchResult(searchHelper.getQueryBuilder());
        for (int i = 0; i < records.size(); i ++) {
            JSONObject record = processRecord(records.getJSONObject(i));
            result.add(record);
        }
        return result;
    }

    /**
     * 分离ADA和客户的对话，并将对话内容进行词频统计
     * @param source 对话分析
     * @return 处理后的对话分析，将ADA和客户的对话进行分离，并做词频统计
     */
    private JSONObject processRecord(JSONObject source) {
        JSONObject result = new JSONObject();
        int userChannelId = result.getInt(PROPERTY_NAME.USER_CHANNEL_ID.toString());
        StringBuilder adaText = new StringBuilder();
        StringBuilder clientText = new StringBuilder();
        StringBuilder allText = new StringBuilder();
        JSONArray conversations = source.getJSONArray(PROPERTY_NAME.TEXT.toString());
        for (int i = 0; i < conversations.size(); i ++) {
            JSONObject sentence = conversations.getJSONObject(i);
            String content = sentence.getString(PROPERTY_NAME.TEXT.toString());
            if (sentence.getInt("USER_CHANNEL_ID") == userChannelId) {
                adaText.append(content).append(CommonConstant.SEPARATOR.COMMA.toString());
            } else {
                clientText.append(content).append(CommonConstant.SEPARATOR.COMMA.toString());
            }
            allText.append(content).append(CommonConstant.SEPARATOR.COMMA.toString());
        }
        result.put(PROPERTY_NAME.ADA_TEXT.toString(), adaText.toString());
        result.put(PROPERTY_NAME.CLIENT_TEXT.toString(), clientText.toString());
        result.put(PROPERTY_NAME.ALL_TEXT.toString(), allText.toString());
        result.put(PROPERTY_NAME.ALL_WORD_FREQUENT.toString(), tokenizationAdapter.getTermFrequent(adaText.toString()));
        result.put(PROPERTY_NAME.CLIENT_WORD_FREQUENT.toString(), tokenizationAdapter.getTermFrequent(clientText.toString()));
        result.put(PROPERTY_NAME.ALL_WORD_FREQUENT.toString(), tokenizationAdapter.getTermFrequent(allText.toString()));
        return result;
    }

    enum PROPERTY_NAME {
        /**
         * 用户录音通道
         */
        USER_CHANNEL_ID("user_channel_id"),
        /**
         * 分析结果
         */
        ANALYZE_INFO("analyze_info"),
        /**
         * 对话文本
         */
        TEXT("text"),
        /**
         * ADA端对话文本
         */
        ADA_TEXT("adaText"),
        /**
         * 客户端对话文本
         */
        CLIENT_TEXT("clientText"),
        /**
         * 所有角色对话文本
         */
        ALL_TEXT("allText"),
        /**
         * ADA对话词频
         */
        ADA_WORD_FREQUENT("adaWordFrequent"),
        /**
         * 客户端对话词频
         */
        CLIENT_WORD_FREQUENT("clientWordFrequent"),
        /**
         * 所有端对话词频
         */
        ALL_WORD_FREQUENT("allWordFrequent"),
        ;
        private String propertyName;
        PROPERTY_NAME(String propertyName){
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String toString() {
            return propertyName;
        }
    }
}
